package io.github.daegwonkim.event_coupon_issuance_system.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * AOP 헬퍼 클래스
 * 분산 락에서 lock을 획득한 후, 비즈니스 로직이 독립된 트랜잭션에서 안전하게 실행되도록 하기 위한 용도
 */
@Component
public class AopForTransaction {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Object proceed(ProceedingJoinPoint joinPoint) throws Throwable {
        return joinPoint.proceed();
    }
}
