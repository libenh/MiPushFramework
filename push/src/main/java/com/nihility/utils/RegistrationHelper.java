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
 * 强制注册：清理应用端 mipush 缓存并触发重注册。
 * ColorOS 17 上优先 startService，广播作为补充。
 */
public class RegistrationHelper {
    private static final Logger logger = XLog.tag("RegistrationHelper").build();

    Context context;
    String packageName;

    public RegistrationHelper(Context context, String packageName) {
        this.context = context;
        this.packageName = packageName;
    }

    public static void tryForceRegister(String packageName) {
        Context app = Utils.getApplication();
        byte[] msgBytes = XMPushUtils.packToBytes(createForceRegisterMessage(packageName));

        // 1) startService 到目标 PushMessageHandler（O系/高版本 Android 更可靠）
        try {
            Intent serviceIntent = new Intent(PushConstants.MIPUSH_ACTION_NEW_MESSAGE);
            serviceIntent.setComponent(new ComponentName(
                    packageName, "com.xiaomi.mipush.sdk.PushMessageHandler"));
            serviceIntent.putExtra(PushConstants.MIPUSH_EXTRA_PAYLOAD, msgBytes);
            serviceIntent.putExtra(PushConstants.MESSAGE_RECEIVE_TIME, System.currentTimeMillis());
            if (Build.VERSION.SDK_INT >= 26) {
                try {
                    app.startForegroundService(serviceIntent);
                } catch (Throwable t) {
                    app.startService(serviceIntent);
                }
            } else {
                app.startService(serviceIntent);
            }
            logger.i("[" + packageName + "] force register via startService");
        } catch (Throwable t) {
            logger.e("[" + packageName + "] startService force register failed: " + t.getMessage(), t);
        }

        // 2) 广播补充
        try {
            Intent intent = new Intent(PushConstants.MIPUSH_ACTION_NEW_MESSAGE);
            intent.setPackage(packageName);
            intent.putExtra(PushConstants.MIPUSH_EXTRA_PAYLOAD, msgBytes);
            intent.putExtra(PushConstants.MESSAGE_RECEIVE_TIME, System.currentTimeMillis());
            app.sendBroadcast(intent);
            logger.i("[" + packageName + "] force register via broadcast");
        } catch (Throwable t) {
            logger.e("[" + packageName + "] broadcast force register failed: " + t.getMessage(), t);
        }
    }

    @NonNull
    public static XmPushActionContainer createForceRegisterMessage(String packageName) {
        String id = "fake_expired_" + packageName + "_" + System.currentTimeMillis();
        XmPushActionNotification regIdExpiredNotification = new XmPushActionNotification();
        regIdExpiredNotification.setType(NotificationType.RegIdExpired.value);
        regIdExpiredNotification.setId(id);
        PushMetaInfo metaInfo = new PushMetaInfo();
        metaInfo.setId(id);
        XmPushActionContainer regIdExpiredContainer =
                XMPushUtils.packToContainer(regIdExpiredNotification, packageName);
        regIdExpiredContainer.setMetaInfo(metaInfo);
        return regIdExpiredContainer;
    }

    public boolean removeMiPushXml() {
        // 多路径：主用户 / CE / 多用户常见目录
        String cmd = String.format(
                "rm -f " +
                        "/data/user/0/%s/shared_prefs/mipush*.xml " +
                        "/data/user_de/0/%s/shared_prefs/mipush*.xml " +
                        "/data_mirror/data_ce/null/0/%s/shared_prefs/mipush*.xml " +
                        "/data/data/%s/shared_prefs/mipush*.xml " +
                        "2>/dev/null; echo done",
                packageName, packageName, packageName, packageName);
        Shell.Result result = Shell.cmd(cmd).exec();
        logger.i("[" + packageName + "] removeMiPushXml success=" + result.isSuccess()
                + " out=" + result.getOut());
        return result.isSuccess();
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
