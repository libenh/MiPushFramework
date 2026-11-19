package com.xiaomi.xmsf.push.control;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import top.trumeet.common.Constants;

/**
 * Owns the persisted user preference for the MiPush runtime.
 */
final class PushPreferences {
    private PushPreferences() {
    }

    private static SharedPreferences get(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(
                context.getApplicationContext());
    }

    static boolean isEnabled(Context context) {
        return get(context).getBoolean(Constants.KEY_ENABLE_PUSH, true);
    }

    static void setEnabled(Context context, boolean enabled) {
        get(context).edit()
                .putBoolean(Constants.KEY_ENABLE_PUSH, enabled)
                .apply();
    }
}
