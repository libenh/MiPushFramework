package com.xiaomi.xmsf.push.service.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

import com.elvishew.xlog.Logger;
import com.elvishew.xlog.XLog;
import com.xiaomi.push.service.ClientEventDispatcher;
import com.xiaomi.push.service.XMPushService;

/**
 * 开机拉起推送服务（C17 下必须 startForegroundService）。
 */
public class BootReceiver extends BroadcastReceiver {
    private static final Logger logger = XLog.tag("BootReceiver").build();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) {
            return;
        }
        if (!Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())
                && !Intent.ACTION_LOCKED_BOOT_COMPLETED.equals(intent.getAction())
                && !"android.intent.action.QUICKBOOT_POWERON".equals(intent.getAction())
                && !"com.htc.intent.action.QUICKBOOT_POWERON".equals(intent.getAction())) {
            return;
        }

        logger.i("boot action=" + intent.getAction() + " -> start XMPushService");
        try {
            new ClientEventDispatcher().notifyServiceStarted(context);
        } catch (Throwable t) {
            logger.e("notifyServiceStarted: " + t.getMessage(), t);
        }

        try {
            Intent service = new Intent(context, XMPushService.class);
            ContextCompat.startForegroundService(context, service);
        } catch (Throwable t) {
            logger.e("startForegroundService: " + t.getMessage(), t);
            try {
                context.startService(new Intent(context, XMPushService.class));
            } catch (Throwable t2) {
                logger.e("startService fallback: " + t2.getMessage(), t2);
            }
        }
    }
}
