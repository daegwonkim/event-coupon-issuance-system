package io.github.daegwonkim.event_coupon_issuance_system.aop;

import io.github.daegwonkim.event_coupon_issuance_system.annotation.DistributedLock;
import io.github.daegwonkim.event_coupon_issuance_system.parser.SpELParser;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 분삭 락 AOP 클래스
 */
@Aspect
@Component
@RequiredArgsConstructor
public class DistributedLockAop {

    private final RedissonClient redissonClient;
    private final AopForTransaction aopForTransaction;
    private final SpELParser spELParser;

    /**
     * 분산 락으로 메서드를 감싸는 Around 어드바이스
     *
     * @param joinPoint 호출된 메서드
     * @param distributedLock 분산락 어노테이션
     * @return 비즈니스 로직 실행 결과
     */
    @Around("@annotation(distributedLock)")
    public Object lock(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String key = spELParser.createDynamicKey(signature.getParameterNames(), joinPoint.getArgs(), distributedLock.key());
        RLock lock = redissonClient.getLock(key);

        try {
            // 락을 얻기 위해 최대 10초간 대기하면서 시도하고, 락을 얻었다면 1초 동안만 유지한 후 자동으로 해제
            if (!lock.tryLock(10, 1, TimeUnit.SECONDS)) {
                throw new IllegalStateException("락 획득에 실패했습니다.");
            }

            // 별도 트랜잭션으로 비즈니스 로직 수행
            return aopForTransaction.proceed(joinPoint);
        } finally {
            // 현재 Redis 상에 lock 키가 존재하고, 현재 실행중인 스레드가 그 락의 소유자인지 확인하여 안전하게 해제
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
