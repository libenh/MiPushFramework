package com.xiaomi.xmsf.push.service.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

import com.elvishew.xlog.Logger;
import com.elvishew.xlog.XLog;
import com.nihility.utils.OPlusRom;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.push.service.PushServiceConstants;
import com.xiaomi.push.service.XMPushService;

/**
 * 保活：O 系缩短间隔，提高被冻结后恢复概率。
 */
public class KeepAliveReceiver extends BroadcastReceiver {
    private final Logger logger = XLog.tag(KeepAliveReceiver.class.getSimpleName()).build();

    private long lastActive = 0L;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            long now = System.currentTimeMillis();
            long minInterval = OPlusRom.isOPlus() ? 30_000L : 120_000L;
            if (lastActive > 0 && (now - lastActive) < minInterval) {
                return;
            }
            lastActive = now;

            String action = intent != null ? intent.getAction() : "null";
            logger.d("keepAlive action=" + action);
            Intent localIntent = new Intent(context, XMPushService.class);
            localIntent.putExtra(PushServiceConstants.EXTRA_TIME_STAMP, now);
            localIntent.setAction(PushServiceConstants.ACTION_CHECK_ALIVE);
            ContextCompat.startForegroundService(context, localIntent);
        } catch (Exception e) {
            MyLog.e(e);
            logger.e("keepAlive failed: " + e.getMessage(), e);
        }
    }
}
