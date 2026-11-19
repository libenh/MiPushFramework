package com.xiaomi.xmsf.push.control;

import android.content.Context;

/**
 * Backward-compatible entry points for the MiPush runtime.
 *
 * Lifecycle, registration, preferences and process detection are implemented
 * by focused controllers so callers do not need to know their internals.
 */
public class PushControllerUtils {
    public static void registerPush(Context context, int retryIndex) {
        PushRegistrationController.scheduleRetry(context, retryIndex);
    }

    public static boolean pushRegistered(Context context) {
        return PushRegistrationController.isRegistered(context);
    }

    public static boolean isPrefsEnable(Context context) {
        return PushPreferences.isEnabled(context);
    }

    public static void setPrefsEnable(boolean value, Context context) {
        PushPreferences.setEnabled(context, value);
    }

    public static boolean isAppMainProc(Context context) {
        return PushProcessUtils.isMainProcess(context);
    }

    public static void setServiceEnable(boolean enable, Context context) {
        PushServiceController.setEnabled(enable, context);
    }

    public static void setAllEnable(boolean enable, Context context) {
        setPrefsEnable(enable, context);
        setServiceEnable(enable, context);
        PushServiceController.setBootReceiverEnabled(enable, context);
    }

    public static Context wrapContext(Context context) {
        return PushContextFactory.wrap(context);
    }
}
