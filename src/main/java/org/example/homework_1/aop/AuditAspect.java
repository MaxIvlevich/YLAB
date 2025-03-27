package org.example.homework_1.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public class AuditAspect {
    static {
        System.out.println("🔥 AuditAspect загружен!");
    }

    private static final Logger logger = LoggerFactory.getLogger(AuditAspect.class);
    private static final ThreadLocal<String> currentUser = new ThreadLocal<>();

    public static void setCurrentUser(String username) {
        currentUser.set(username);
    }

    @Before("execution(* org.example.service.*.*(..))") // Все методы в пакете service
    public void logUserAction(JoinPoint joinPoint) {
        String username = currentUser.get() != null ? currentUser.get() : "Anonymous";
        System.out.println("🔥 Логируем метод: " + joinPoint.getSignature());
        logger.info("User {} invoked method {}", username, joinPoint.getSignature());
    }

    @Around("execution(* org.example.service.*.*(..))") // Логируем время выполнения
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long end = System.currentTimeMillis();
        logger.info("Method {} executed in {} ms", joinPoint.getSignature(), (end - start));
        return result;
    }
}
