package com.bootstrap.workshop.observability;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Aspect for automatic observability (logging, tracing, metrics) across
 * services and controllers.
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class ObservabilityAspect {

    private final Tracer tracer;
    private final MeterRegistry meterRegistry;

    @Pointcut("execution(public * com.bootstrap.workshop.service.*.*(..)) || " +
            "execution(public * com.bootstrap.workshop.controller.*.*(..))")
    public void monitoredMethods() {
    }

    @Around("monitoredMethods()")
    public Object observe(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String fullMethodName = className + "." + methodName;

        // 1. Create Span
        Span newSpan = tracer.nextSpan().name(fullMethodName);

        try (Tracer.SpanInScope ws = tracer.withSpan(newSpan.start())) {
            // 2. Log Entry
            // Sanitize args - avoiding logging potential sensitive data directly in
            // production
            // For workshop/dev we can log args, but let's be careful with passwords
            Object[] args = joinPoint.getArgs();
            log.info("Entering {}.{} with args: {}", className, methodName,
                    Arrays.toString(args)); // In real prod, use specific masking

            long start = System.nanoTime();
            Object result;
            try {
                // 3. Proceed
                result = joinPoint.proceed();

                // 4. Record Success Metric
                recordMetrics(className, methodName, "success");

                return result;
            } catch (Throwable t) {
                // 4. Record Failure Metric
                recordMetrics(className, methodName, t.getClass().getSimpleName());
                throw t;
            } finally {
                // 4. Record Timer
                long duration = System.nanoTime() - start;
                Timer.builder("method.exec.time")
                        .tag("class", className)
                        .tag("method", methodName)
                        .register(meterRegistry)
                        .record(duration, TimeUnit.NANOSECONDS);

                log.info("Exiting {}.{} - Duration: {} ms", className, methodName,
                        TimeUnit.NANOSECONDS.toMillis(duration));
                newSpan.end();
            }
        }
    }

    private void recordMetrics(String className, String methodName, String status) {
        Counter.builder("method.exec.count")
                .tag("class", className)
                .tag("method", methodName)
                .tag("result", status)
                .register(meterRegistry)
                .increment();
    }
}
