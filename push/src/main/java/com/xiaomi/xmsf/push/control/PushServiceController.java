package com.xiaomi.xmsf.push.control;

import android.annotation.SuppressLint;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.content.ContextCompat;

import com.elvishew.xlog.Logger;
import com.elvishew.xlog.XLog;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.push.service.PushServiceConstants;
import com.xiaomi.xmsf.FirstRegister;
import com.xiaomi.xmsf.push.service.receivers.BootReceiver;
import com.xiaomi.xmsf.push.service.receivers.KeepAliveReceiver;


/**
 * Owns start/stop and boot integration for the embedded Xiaomi push service.
 */
@SuppressLint("WrongConstant")
final class PushServiceController {
    private static final Logger logger =
            XLog.tag(PushServiceController.class.getSimpleName()).build();

    private static final BroadcastReceiver liveReceiver = new KeepAliveReceiver();

    private PushServiceController() {
    }

    static void setEnabled(boolean enabled, Context context) {
        if (enabled) {
            start(context);
        } else {
            stop(context);
        }
    }

    private static void start(Context context) {
        logger.d("Starting...");
        Context wrappedContext = PushContextFactory.wrap(context);

        if (PushProcessUtils.isMainProcess(context)) {
            com.xiaomi.channel.commonutils.misc.ScheduledJobManager
                    .getInstance(wrappedContext)
                    .addOneShootJob(new FirstRegister(wrappedContext));
        }

        try {
            Intent serviceIntent = new Intent(context,
                    com.xiaomi.push.service.XMPushService.class);
            serviceIntent.putExtra(PushServiceConstants.EXTRA_TIME_STAMP,
                    System.currentTimeMillis());
            serviceIntent.setAction(PushServiceConstants.ACTION_TIMER);
            ContextCompat.startForegroundService(context, serviceIntent);
        } catch (Throwable e) {
            logger.e(e);
        }

        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_ON);
            context.registerReceiver(liveReceiver, filter);
        } catch (Throwable e) {
            logger.e(e);
        }
    }

    private static void stop(Context context) {
        logger.d("Stopping...");
        try {
            context.unregisterReceiver(liveReceiver);
        } catch (Throwable e) {
            logger.e(e);
        }

        MiPushClient.unregisterPush(PushContextFactory.wrap(context));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            JobScheduler scheduler = (JobScheduler) context.getSystemService(
                    Context.JOB_SCHEDULER_SERVICE);
            if (scheduler != null) {
                scheduler.cancelAll();
            }
        }

        context.stopService(new Intent(context,
                com.xiaomi.push.service.XMPushService.class));
    }

    static void setBootReceiverEnabled(boolean enabled, Context context) {
        context.getPackageManager().setComponentEnabledSetting(
                new ComponentName(context, BootReceiver.class),
                enabled ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                        : PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
}
