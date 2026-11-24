package com.nihility.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;

import com.elvishew.xlog.Logger;
import com.elvishew.xlog.XLog;
import com.nihility.XMPushUtils;
import com.topjohnwu.superuser.Shell;
import com.xiaomi.push.sdk.MyPushMessageHandler;
import com.xiaomi.push.service.PushConstants;
import com.xiaomi.xmpush.thrift.NotificationType;
import com.xiaomi.xmpush.thrift.PushMetaInfo;
import com.xiaomi.xmpush.thrift.XmPushActionContainer;
import com.xiaomi.xmpush.thrift.XmPushActionNotification;

import top.trumeet.common.utils.Utils;

/**
 * C17 强制注册：清缓存 + startService 主路径 + 广播补充。
 */
public class RegistrationHelper {
    private static final Logger logger = XLog.tag("RegistrationHelper").build();
    private static final String TARGET_HANDLER =
            "com.xiaomi.mipush.sdk.PushMessageHandler";

    private final Context context;
    private final String packageName;

    public RegistrationHelper(Context context, String packageName) {
        this.context = context;
        this.packageName = packageName;
    }

    public static void tryForceRegister(String packageName) {
        Context app = Utils.getApplication();
        if (app == null) {
            logger.e("tryForceRegister: application null");
            return;
        }
        byte[] msgBytes = XMPushUtils.packToBytes(createForceRegisterMessage(packageName));
        long now = System.currentTimeMillis();

        // startService 主路径
        boolean serviceOk = false;
        try {
            Intent serviceIntent = new Intent(PushConstants.MIPUSH_ACTION_NEW_MESSAGE);
            serviceIntent.setComponent(new ComponentName(packageName, TARGET_HANDLER));
            serviceIntent.putExtra(PushConstants.MIPUSH_EXTRA_PAYLOAD, msgBytes);
            serviceIntent.putExtra(PushConstants.MESSAGE_RECEIVE_TIME, now);
            ComponentName cn;
            if (Build.VERSION.SDK_INT >= 26) {
                try {
                    cn = app.startForegroundService(serviceIntent);
                } catch (Throwable t) {
                    cn = app.startService(serviceIntent);
                }
            } else {
                cn = app.startService(serviceIntent);
            }
            serviceOk = cn != null;
            logger.i("[" + packageName + "] forceRegister startService cn=" + cn);
        } catch (Throwable t) {
            logger.e("[" + packageName + "] forceRegister startService: " + t.getMessage(), t);
        }

        // 广播补充（始终发，避免仅 service 失败时无路）
        try {
            Intent intent = new Intent(PushConstants.MIPUSH_ACTION_NEW_MESSAGE);
            intent.setPackage(packageName);
            intent.putExtra(PushConstants.MIPUSH_EXTRA_PAYLOAD, msgBytes);
            intent.putExtra(PushConstants.MESSAGE_RECEIVE_TIME, now);
            app.sendBroadcast(intent);
            logger.i("[" + packageName + "] forceRegister broadcast sent (serviceOk=" + serviceOk + ")");
        } catch (Throwable t) {
            logger.e("[" + packageName + "] forceRegister broadcast: " + t.getMessage(), t);
        }
    }

    @NonNull
    public static XmPushActionContainer createForceRegisterMessage(String packageName) {
        String id = "fake_expired_" + packageName + "_" + System.currentTimeMillis();
        XmPushActionNotification notification = new XmPushActionNotification();
        notification.setType(NotificationType.RegIdExpired.value);
        notification.setId(id);
        PushMetaInfo metaInfo = new PushMetaInfo();
        metaInfo.setId(id);
        XmPushActionContainer container =
                XMPushUtils.packToContainer(notification, packageName);
        container.setMetaInfo(metaInfo);
        return container;
    }

    public boolean removeMiPushXml() {
        String cmd = String.format(
                "rm -f " +
                        "/data/user/0/%s/shared_prefs/mipush*.xml " +
                        "/data/user_de/0/%s/shared_prefs/mipush*.xml " +
                        "/data_mirror/data_ce/null/0/%s/shared_prefs/mipush*.xml " +
                        "/data/data/%s/shared_prefs/mipush*.xml " +
                        "2>/dev/null; echo done",
                packageName, packageName, packageName, packageName);
        try {
            Shell.Result result = Shell.cmd(cmd).exec();
            logger.i("[" + packageName + "] removeMiPushXml ok=" + result.isSuccess()
                    + " " + result.getOut());
            return result.isSuccess();
        } catch (Throwable t) {
            logger.e("[" + packageName + "] removeMiPushXml: " + t.getMessage(), t);
            return false;
        }
    }

    public void deleteRegistrationInfoAndRetryForceRegister() {
        logger.i("[" + packageName + "] deleteRegistrationInfoAndRetryForceRegister");
        removeMiPushXml();
        try {
            MyPushMessageHandler.launchApp(context, createForceRegisterMessage(packageName));
        } catch (Throwable t) {
            logger.e("[" + packageName + "] launchApp: " + t.getMessage(), t);
        }
        tryForceRegister(packageName);
    }
}
