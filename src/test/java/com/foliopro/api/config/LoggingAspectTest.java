package com.foliopro.api.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LoggingAspectTest {
    private final LoggingAspect aspect = new LoggingAspect();

    @Test
    void preservesSuccessfulReturnValue() throws Throwable {
        ProceedingJoinPoint joinPoint = joinPoint("ApiController.getDashboard()");
        when(joinPoint.proceed()).thenReturn("dashboard");

        assertEquals("dashboard", aspect.logInvocation(joinPoint));
    }

    @Test
    void propagatesOriginalException() throws Throwable {
        ProceedingJoinPoint joinPoint = joinPoint("FolioService.getDashboard()");
        IllegalStateException expected = new IllegalStateException("synthetic failure");
        when(joinPoint.proceed()).thenThrow(expected);

        IllegalStateException actual = assertThrows(IllegalStateException.class,
                () -> aspect.logInvocation(joinPoint));

        assertSame(expected, actual);
    }

    private ProceedingJoinPoint joinPoint(String signatureText) {
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        Signature signature = mock(Signature.class);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.toShortString()).thenReturn(signatureText);
        return joinPoint;
    }
}
