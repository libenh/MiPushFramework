package com.xiaomi.xmsf.push.control;

import android.os.SystemClock;

/**
 * Bounds repeated registration-task scheduling caused by connectivity broadcast storms.
 *
 * Uses elapsed realtime so wall-clock changes cannot extend or bypass the throttle.
 * The first network-available event is always allowed.
 */
public final class NetworkRegistrationThrottle {
    public static final long MIN_PROCESS_INTERVAL_MS = 5 * 60 * 1000L;

    private static final Object LOCK = new Object();
    private static long lastProcessElapsedMs = Long.MIN_VALUE;

    private NetworkRegistrationThrottle() {
    }

    /**
     * @return true when the registration task should be skipped because a recent
     *         network event already scheduled one.
     */
    public static boolean shouldThrottle() {
        synchronized (LOCK) {
            long now = SystemClock.elapsedRealtime();
            if (lastProcessElapsedMs != Long.MIN_VALUE) {
                long elapsed = now - lastProcessElapsedMs;
                if (elapsed >= 0L && elapsed < MIN_PROCESS_INTERVAL_MS) {
                    return true;
                }
            }
            lastProcessElapsedMs = now;
            return false;
        }
    }

    /** Test/process-reset hook; does not affect an already scheduled MiPush task. */
    static void resetForTests() {
        synchronized (LOCK) {
            lastProcessElapsedMs = Long.MIN_VALUE;
        }
    }
}
