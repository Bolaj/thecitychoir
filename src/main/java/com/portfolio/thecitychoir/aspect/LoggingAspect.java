package com.portfolio.thecitychoir.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    /**
     * Pointcut that matches all repositories, services, and Web REST endpoints.
     */
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerPointcut() {}

    /**
     * Advice that logs when a method is entered and exited.
     */
    @Around("controllerPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        // Log Request Entry
        log.info("Enter: {}.{}() with argument[s] = {}",
                className, methodName, Arrays.toString(joinPoint.getArgs()));

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - start;

            // Log Success Exit
            log.info("Exit: {}.{}() with result = {}. Execution time: {}ms",
                    className, methodName, result, executionTime);

            return result;
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument: {} in {}.{}()",
                    Arrays.toString(joinPoint.getArgs()), className, methodName);
            throw e;
        } catch (Exception e) {
            log.error("Exception in {}.{}(): {}", className, methodName, e.getMessage());
            throw e;
        }
    }
}