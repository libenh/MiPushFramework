package com.xiaomi.xmsf.push.service.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.xiaomi.channel.commonutils.network.Network;
import com.xiaomi.mipush.sdk.PushServiceClient;
import com.xiaomi.smack.util.TrafficUtils;

public class NetworkStatusReceiver extends BroadcastReceiver {
    private static final String TAG = "MiPushNetworkReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (context == null) {
            Log.w(TAG, "Ignoring network event: context is null");
            return;
        }

        final Intent serviceIntent = new Intent(
                context, com.xiaomi.push.service.XMPushService.class);
        serviceIntent.setAction("com.xiaomi.push.network_status_changed");

        // Keep service startup failure from preventing the remaining network recovery work.
        try {
            ContextCompat.startForegroundService(context, serviceIntent);
        } catch (RuntimeException e) {
            Log.e(TAG, "Unable to start push service after network change", e);
        }

        try {
            TrafficUtils.notifyNetworkChanage(context);
        } catch (RuntimeException e) {
            Log.e(TAG, "Unable to notify push transport about network change", e);
        }

        try {
            PushServiceClient client = PushServiceClient.getInstance(context);
            if (Network.hasNetwork(context) && client.isProvisioned()) {
                client.processRegisterTask();
            }
        } catch (RuntimeException e) {
            Log.e(TAG, "Unable to refresh push registration after network change", e);
        }
    }
}
