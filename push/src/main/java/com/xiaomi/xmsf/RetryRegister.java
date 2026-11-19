package com.xiaomi.xmsf;

import android.content.Context;

import com.xiaomi.xmsf.push.control.PushRegistrationController;

import java.util.Objects;

/**
 * Delayed MiPush registration retry job.
 */
public class RetryRegister implements Runnable {
    private final int retryCount;
    private final Context context;

    public RetryRegister(Context context, int retryCount) {
        this.context = Objects.requireNonNull(context, "context");
        this.retryCount = retryCount;
    }

    @Override
    public void run() {
        PushRegistrationController.retry(context, retryCount);
    }
}
