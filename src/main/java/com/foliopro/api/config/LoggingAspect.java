package com.foliopro.api.config;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);
    private final ThreadLocal<InvocationContext> invocationContext = new ThreadLocal<>();

    @Around("execution(public * com.foliopro.api.controller..*(..)) || " +
            "execution(public * com.foliopro.api.service..*(..))")
    public Object logInvocation(ProceedingJoinPoint joinPoint) throws Throwable {
        InvocationContext context = invocationContext.get();
        boolean rootInvocation = context == null;
        if (rootInvocation) {
            context = new InvocationContext();
            invocationContext.set(context);
        }
        context.depth++;

        String method = joinPoint.getSignature().toShortString();
        RequestInfo request = currentRequest();
        long startNanos = System.nanoTime();
        log.info("Invocation started: {}{}", method, request.logSuffix());

        try {
            Object result = joinPoint.proceed();
            log.info("Invocation completed: {} in {} ms{}", method,
                    elapsedMillis(startNanos), request.logSuffix());
            return result;
        } catch (Throwable exception) {
            if (context.loggedExceptions.add(exception)) {
                log.warn("Invocation failed: {}{}; exceptionType={}", method,
                        request.logSuffix(), exception.getClass().getSimpleName());
            }
            throw exception;
        } finally {
            context.depth--;
            if (context.depth == 0) {
                invocationContext.remove();
            }
        }
    }

    private RequestInfo currentRequest() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (!(attributes instanceof ServletRequestAttributes servletAttributes)) {
            return RequestInfo.none();
        }
        HttpServletRequest request = servletAttributes.getRequest();
        return new RequestInfo(request.getMethod(), sanitize(request.getRequestURI()));
    }

    private String sanitize(String value) {
        return value.replaceAll("[\\r\\n\\p{Cntrl}]", "?");
    }

    private long elapsedMillis(long startNanos) {
        return (System.nanoTime() - startNanos) / 1_000_000;
    }

    private record RequestInfo(String httpMethod, String path) {
        private static RequestInfo none() {
            return new RequestInfo(null, null);
        }

        private String logSuffix() {
            if (httpMethod == null) {
                return "";
            }
            return " [" + httpMethod + " " + path + "]";
        }
    }

    private static final class InvocationContext {
        private int depth;
        private final Set<Throwable> loggedExceptions =
                Collections.newSetFromMap(new IdentityHashMap<>());
    }
}
