package jpabook.jpashop.aop;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {


    @Pointcut("@within(jpabook.jpashop.aop.annotations.Loggable)")
    public void loggableClass() {
    }

    @Around("loggableClass()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        log.debug("Executing method: {}", joinPoint.getSignature().toShortString());

        Object proceed = joinPoint.proceed();

        long executionTime = System.currentTimeMillis() - start;
        log.debug("{} executed in {}ms", joinPoint.getSignature().toShortString(), executionTime);
        return proceed;
    }



}
