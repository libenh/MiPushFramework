package com.xiaomi.xmsf.push.service.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.network.Network;
import com.xiaomi.mipush.sdk.PushServiceClient;
import com.xiaomi.push.service.XMPushService;
import com.xiaomi.smack.util.TrafficUtils;

/**
 * Handles connectivity changes without allowing a rejected service launch to
 * abort the remaining network-recovery work.
 */
public class NetworkStatusReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, XMPushService.class);
        serviceIntent.setAction("com.xiaomi.push.network_status_changed");

        try {
            ContextCompat.startForegroundService(context, serviceIntent);
        } catch (RuntimeException e) {
            // ColorOS/realme UI may reject a background service start under
            // vendor power policy. Still notify the transport and registration
            // layers so they can recover when the service is already alive.
            MyLog.w("Unable to start XMPushService after network change");
            MyLog.e(e);
        }

        try {
            TrafficUtils.notifyNetworkChanage(context);
        } catch (RuntimeException e) {
            MyLog.w("Unable to notify MiPush traffic monitor");
            MyLog.e(e);
        }

        try {
            PushServiceClient client = PushServiceClient.getInstance(context);
            if (Network.hasNetwork(context) && client.isProvisioned()) {
                client.processRegisterTask();
            }
        } catch (RuntimeException e) {
            MyLog.w("Unable to schedule MiPush registration after network change");
            MyLog.e(e);
        }
    }
}
