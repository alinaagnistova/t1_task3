package ru.alina.application.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* ru.alina.application.service.TaskService.*(..))")
    public void logMethodExecution(JoinPoint joinPoint) {
        logger.info("Method {} has started execution", joinPoint.getSignature());
    }

    @AfterThrowing(pointcut = "@annotation(ru.alina.application.aspect.annotation.CustomExceptionHandling)", throwing = "ex")
    public void logMethodThrowing(JoinPoint joinPoint, Exception ex) {
        logger.warn("Method {} threw an exception", joinPoint.getSignature().toShortString());
        logger.warn("Exception {} with message {}", ex.getClass().getName(), ex.getMessage());
    }

    @Around("execution(* ru.alina.application.service.TaskService.addTask(..))")
    public Object logMethodElapsedTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result;
        try{
             result = joinPoint.proceed();
        } catch(Exception ex){
            logger.warn("Method {} threw an exception while counting elasped time: {}", joinPoint.getSignature().toShortString(), ex.getMessage());
            throw ex;
        }
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        logger.info("Method {} completed in {} ms", joinPoint.getSignature().toShortString(), elapsedTime);
        return result;
    }

    @AfterReturning(pointcut = "@annotation(ru.alina.application.aspect.annotation.CustomLogging)", returning = "result")
    public void logMethodReturning(JoinPoint joinPoint, Object result) {
        logger.info("Method {} returned {}", joinPoint.getSignature().toShortString(), result.toString());
    }


}
