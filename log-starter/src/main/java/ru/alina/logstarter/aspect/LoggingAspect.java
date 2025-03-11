package ru.alina.logstarter.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alina.logstarter.config.LoggingProperties;


@Aspect
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
    private final LoggingProperties properties;

    public LoggingAspect(LoggingProperties properties) {
        this.properties = properties;
    }

    @AfterThrowing(pointcut = "@annotation(ru.alina.logstarter.aspect.annotation.CustomExceptionHandling)", throwing = "ex")
    public void logMethodThrowing(JoinPoint joinPoint, Exception ex) {
        String msg = "Method " + joinPoint.getSignature().toShortString()
                + " threw an exception " + ex.getClass().getName()
                + " with message: " + ex.getMessage();
        logMessageDependingOnLevel(msg, "warn");
    }

    @Around("@annotation(ru.alina.logstarter.aspect.annotation.CustomLoggingElapsedTime)")
    public Object logMethodElapsedTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result;
        try{
             result = joinPoint.proceed();
        } catch(Exception ex){
            String exMsg = "Method " + joinPoint.getSignature().toShortString() +
                    " threw an exception while counting elapsed time: " + ex.getMessage();
            logMessageDependingOnLevel(exMsg, "warn");
            throw ex;
        }
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        String msg = "Method " + joinPoint.getSignature().toShortString()
                + " completed in " + elapsedTime + " ms";
        logMessageDependingOnLevel(msg, "info");
        return result;
    }

    @AfterReturning(pointcut = "@annotation(ru.alina.logstarter.aspect.annotation.CustomLoggingResult)", returning = "result")
    public void logMethodReturning(JoinPoint joinPoint, Object result) {
        String msg = "Method " + joinPoint.getSignature().toShortString()
                + " returned " + (result != null ? result.toString() : "null");
        logMessageDependingOnLevel(msg, "info");
    }

    private void logMessageDependingOnLevel(String message, String messageLogLevel) {
        int configuredLevel = toNumber(properties.getLevel());
        int currentMsgLevel = toNumber(messageLogLevel);

        if (currentMsgLevel >= configuredLevel) {
            switch (messageLogLevel.toLowerCase()) {
                case "debug":
                    logger.debug(message);
                    break;
                case "warn":
                    logger.warn(message);
                    break;
                case "error":
                    logger.error(message);
                    break;
                default:
                    logger.info(message);
                    break;
            }
        }
    }

    private int toNumber(String level) {
        switch (level.toLowerCase()) {
            case "debug": return 1;
            case "info":  return 2;
            case "warn":  return 3;
            case "error": return 4;
            default:      return 2;
        }
    }

}
