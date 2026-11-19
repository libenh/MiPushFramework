package com.xiaomi.xmsf.push.control;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.util.Objects;

import top.trumeet.common.Constants;

/**
 * Owns registration state and retry scheduling for the XMSF push identity.
 *
 * Retry timing and the existing registration entry points are intentionally
 * preserved so this extraction does not change the runtime protocol.
 */
public final class PushRegistrationController {
    private static final int[] RETRY_INTERVAL_MS = {
            3_600_000, 7_200_000, 14_400_000, 28_800_000, 86_400_000
    };

    private PushRegistrationController() {
    }

    public static boolean isRegistered(Context context) {
        return !android.text.TextUtils.isEmpty(
                MiPushClient.getRegId(context));
    }

    public static int getRetryIntervalMs(int retryIndex) {
        int index = Math.max(0, retryIndex);
        index = Math.min(index, RETRY_INTERVAL_MS.length - 1);
        return RETRY_INTERVAL_MS[index];
    }

    public static void scheduleRetry(Context context, int retryIndex) {
        Objects.requireNonNull(context, "context");
        int intervalMs = getRetryIntervalMs(retryIndex);
        MyLog.i("for make sure xmsf register push succ, schedule register after "
                + intervalMs / 1000 + " sec");
        new Handler(Looper.getMainLooper()).postDelayed(
                new com.xiaomi.xmsf.RetryRegister(context, retryIndex),
                intervalMs);
    }

    public static void registerInitial(Context context) {
        Objects.requireNonNull(context, "context");
        MiPushClient.registerPush(context, Constants.APP_ID, Constants.APP_KEY);
        if (isRegistered(context)) {
            MyLog.i("register successed");
        } else {
            scheduleRetry(context, 0);
        }

        // Preserve the existing post-registration settling delay.
        try {
            Thread.sleep(100L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            MyLog.e("register push interrupted error", e);
        }
    }

    public static void retry(Context context, int retryCount) {
        Objects.requireNonNull(context, "context");
        if (isRegistered(context)) {
            MyLog.i("register successed, stop retry");
            return;
        }

        MiPushClient.registerPush(context, Constants.APP_ID, Constants.APP_KEY);
        int nextRetryCount = retryCount + 1;
        if (nextRetryCount <= 10) {
            MyLog.i("register not successed, register again, retryIndex: "
                    + nextRetryCount);
            scheduleRetry(context, nextRetryCount);
        } else {
            MyLog.i("register not successed, but retry too many times, stop retry");
        }
    }
}
