package org.example.homework_1.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public class AuditAspect {
    private static final Logger logger = LoggerFactory.getLogger(AuditAspect.class);

    // Перехват всех методов, помеченных аннотацией @Audit
    @Before("@annotation(Audit) && args(userId,..)")
    public void logUserAction(JoinPoint joinPoint, Long userId) {

        String methodName = joinPoint.getSignature().getName();
        logger.info("User action detected: {} performed by user with ID: {}", methodName, userId);
    }

    @After("@annotation(Audit) && args(userId,..)")
    public void logUserActionCompletion(JoinPoint joinPoint, Long userId) {
        String methodName = joinPoint.getSignature().getName();
        logger.info("Action {} completed by user with ID: {}", methodName, userId);
    }
}
