package com.xiaomi.xmsf;

import android.content.Context;

import com.xiaomi.xmsf.push.control.PushRegistrationController;

import java.util.Objects;

/**
 * Initial MiPush registration job.
 */
public class FirstRegister implements Runnable {
    private final Context context;

    public FirstRegister(Context context) {
        this.context = Objects.requireNonNull(context, "context");
    }

    @Override
    public void run() {
        PushRegistrationController.registerInitial(context);
    }
}
