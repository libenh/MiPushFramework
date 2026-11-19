package com.xiaomi.xmsf.push.control;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PushRegistrationControllerTest {
    @Test
    public void retryIntervalsIncreaseAndThenCapAtOneDay() {
        assertEquals(3_600_000,
                PushRegistrationController.getRetryIntervalMs(0));
        assertEquals(7_200_000,
                PushRegistrationController.getRetryIntervalMs(1));
        assertEquals(14_400_000,
                PushRegistrationController.getRetryIntervalMs(2));
        assertEquals(28_800_000,
                PushRegistrationController.getRetryIntervalMs(3));
        assertEquals(86_400_000,
                PushRegistrationController.getRetryIntervalMs(4));
        assertEquals(86_400_000,
                PushRegistrationController.getRetryIntervalMs(10));
    }

    @Test
    public void negativeRetryIndexUsesInitialInterval() {
        assertEquals(3_600_000,
                PushRegistrationController.getRetryIntervalMs(-1));
    }
}
