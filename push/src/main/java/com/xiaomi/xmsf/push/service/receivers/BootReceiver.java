package com.xiaomi.xmsf.push.service.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.xiaomi.push.service.ClientEventDispatcher;

/**
 * Notifies the push client after Android reports that boot has completed.
 */
public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "MiPushBootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (context == null || intent == null) {
            Log.w(TAG, "Ignoring boot event: context or intent is null");
            return;
        }

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            try {
                new ClientEventDispatcher().notifyServiceStarted(context);
            } catch (RuntimeException e) {
                Log.e(TAG, "Unable to notify push client after boot", e);
            }
        }
    }
}
