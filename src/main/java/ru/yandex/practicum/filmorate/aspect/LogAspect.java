package ru.yandex.practicum.filmorate.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * Public class {@code LogAspect} aspect responsible for logging the results of the methods
 */
@Aspect
@Component
@Slf4j
public class LogAspect {
    /**
     * Advice for logging the start of the method marked with an annotation {@code @LogMethod"}
     *
     * @param joinPoint the {@code JoinPoint} an object that allows you to access information about the method
     */
    @Before("@annotation(ru.yandex.practicum.filmorate.aspect.Loggable)")
    public void logMethodBefore(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String methodName = methodSignature.getMethod().toString();
        log.info("Start method " + methodName);
    }

    /**
     * Advice for logging the finish of the method marked with an annotation {@code @LogMethod"}
     *
     * @param joinPoint the {@code JoinPoint} an object that allows you to access information about the method
     * @param returnedObject the {@code Object} the return value of the business logic method
     */
    @AfterReturning(pointcut = "@annotation(ru.yandex.practicum.filmorate.aspect.Loggable)", returning = "returnedObject")
    public void logMethodAfterReturning(JoinPoint joinPoint, Object returnedObject) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String methodName = methodSignature.getMethod().toString();
        if (returnedObject == null) {
            log.info("Correct finish method" + methodName);
            return;
        }
        log.info("Correct finish method " + methodName + ". Returning values: " + returnedObject);
    }

    /**
     * Advice for logging the throw exception to the method marked with an annotation {@code @LogMethod"}
     *
     * @param joinPoint the {@code JoinPoint} an object that allows you to access information about the method
     * @param exception the {@code Throwable} the thrown exception to the business logic method
     */
    @AfterThrowing(pointcut = "@annotation(ru.yandex.practicum.filmorate.aspect.Loggable)", throwing = "exception")
    public void logMethodAfterReturning(JoinPoint joinPoint, Throwable exception) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String methodName = methodSignature.getMethod().toString();
        log.info("Incorrect finish method " + methodName + ". Exception message: " + exception.getMessage());
    }
}
