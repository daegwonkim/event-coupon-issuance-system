package io.github.daegwonkim.event_coupon_issuance_system.config;

import io.github.daegwonkim.event_coupon_issuance_system.dto.CouponIssueRequest;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConfig {

    private static final String BOOTSTRAP_SERVERS = "localhost:9092";

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> config = new HashMap<>();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(ProducerConfig.ACKS_CONFIG, "all");

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ConsumerFactory<String, CouponIssueRequest> consumerFactory() {
        Map<String, Object> config = new HashMap<>();

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        // JsonDeserializer 신뢰 패키지 설정
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return new DefaultKafkaConsumerFactory<>(
                config,
                new StringDeserializer(),
                new JsonDeserializer<>(CouponIssueRequest.class, false)
        );
    }

    @Bean
    public CommonErrorHandler errorHandler(KafkaTemplate<String, Object> kafkaTemplate) {
        // DLQ로 메시지를 전송하는 Recoverer
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                // DLQ 토픽명 결정 로직: {원본토픽}.DLQ
                (record, ex) -> {
                    String dlqTopic = record.topic() + ".DLQ";
                    return new TopicPartition(dlqTopic, record.partition());
                }
        );

        // 재시도 정책: 1초 간격으로 3번 재시도
        FixedBackOff backOff = new FixedBackOff(1000L, 3L);

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);

        // 특정 예외는 재시도 없이 바로 DLQ로 전송
        errorHandler.addNotRetryableExceptions(
                IllegalArgumentException.class,
                NullPointerException.class
        );

        return errorHandler;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CouponIssueRequest> kafkaListenerContainerFactory(
            CommonErrorHandler errorHandler
    ) {
        ConcurrentKafkaListenerContainerFactory<String, CouponIssueRequest> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(3);

        // DLQ 에러 핸들러 설정
        factory.setCommonErrorHandler(errorHandler);

        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CouponIssueRequest> batchKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, CouponIssueRequest> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory());

        // Batch 모드 설정
        factory.setBatchListener(true);

        factory.setConcurrency(3);

        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

        return factory;
    }
}
