package com.xiaomi.push.sdk;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.P;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.catchingnow.icebox.sdk_client.IceBox;
import com.elvishew.xlog.Logger;
import com.elvishew.xlog.XLog;
import com.nihility.Global;
import com.nihility.XMPushUtils;
import com.topjohnwu.superuser.Shell;
import com.xiaomi.push.service.MIPushNotificationHelper;
import com.xiaomi.push.service.MyMIPushNotificationHelper;
import com.xiaomi.push.service.PushConstants;
import com.xiaomi.xmpush.thrift.PushMetaInfo;
import com.xiaomi.xmpush.thrift.XmPushActionContainer;
import com.xiaomi.xmsf.push.notification.NotificationController;
import com.xiaomi.xmsf.push.utils.Configurations;

import java.util.function.Consumer;

import top.trumeet.common.Constants;
import top.trumeet.common.ita.ITopActivity;
import top.trumeet.common.ita.TopActivityFactory;
import top.trumeet.common.utils.CustomConfiguration;
import top.trumeet.common.utils.Utils;

/**
 * C17 / O系：消息回传以 startService 为主，失败则降级广播。
 */
public class MyPushMessageHandler extends IntentService {
    private static final Logger logger = XLog.tag("MyPushMessageHandler").build();

    private static final int APP_CHECK_FRONT_MAX_RETRY = 8;
    private static final int APP_CHECK_SLEEP_DURATION_MS = 500;
    private static final int APP_CHECK_SLEEP_MAX_TIMEOUT_MS =
            APP_CHECK_FRONT_MAX_RETRY * APP_CHECK_SLEEP_DURATION_MS;

    private static final String TARGET_HANDLER =
            "com.xiaomi.mipush.sdk.PushMessageHandler";

    static ITopActivity iTopActivity = null;

    public MyPushMessageHandler() {
        super("my mipush message handler");
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        if (intent == null) {
            return;
        }
        byte[] payload = intent.getByteArrayExtra(PushConstants.MIPUSH_EXTRA_PAYLOAD);
        if (payload == null) {
            logger.e("mipush_payload is null");
            return;
        }

        final XmPushActionContainer container = XMPushUtils.packToContainer(payload);
        if (container == null) {
            logger.e("container is null");
            return;
        }

        try {
            if (startService(this, container, payload) != null) {
                cancelNotification(this, intent.getExtras(), container);
            }
        } catch (Exception e) {
            logger.e(e.getLocalizedMessage(), e);
        }
    }

    public static void cancelNotification(Context context, Bundle bundle) {
        if (bundle == null) {
            return;
        }
        byte[] payload = bundle.getByteArray(PushConstants.MIPUSH_EXTRA_PAYLOAD);
        if (payload == null) {
            logger.e("mipush_payload is null");
            return;
        }
        final XmPushActionContainer container = XMPushUtils.packToContainer(payload);
        if (container == null) {
            return;
        }
        cancelNotification(context, bundle, container);
    }

    public static void cancelNotification(Context context, Bundle bundle,
                                          XmPushActionContainer container) {
        int notificationId = bundle != null
                ? bundle.getInt(Constants.INTENT_NOTIFICATION_ID, 0) : 0;
        String notificationGroup = bundle != null
                ? bundle.getString(Constants.INTENT_NOTIFICATION_GROUP) : null;
        try {
            Configurations.getInstance().handle(container.packageName, container);
        } catch (Exception e) {
            logger.e("cancelNotification", e);
        }
        CustomConfiguration custom = XMPushUtils.getConfiguration(container);
        NotificationController.cancel(context, container,
                notificationId, notificationGroup, custom.clearGroup(false));
    }

    public static void launchApp(Context context, XmPushActionContainer container) {
        if (iTopActivity == null) {
            iTopActivity = TopActivityFactory.newInstance(
                    Global.ConfigCenter().getAccessMode(context));
        }
        if (!iTopActivity.isEnabled(context)) {
            iTopActivity.guideToEnable(context);
            return;
        }
        String targetPackage = container.getPackageName();
        activeApp(context, targetPackage);
        pullUpApp(context, targetPackage, container);
    }

    public static ComponentName startService(Context context,
                                             XmPushActionContainer container,
                                             byte[] payload) {
        launchApp(context, container);
        return forwardToTargetApplication(context, payload);
    }

    /**
     * C17 核心：优先 startService 到目标 PushMessageHandler；
     * 失败则用带 package 限定的广播作为降级。
     */
    public static ComponentName forwardToTargetApplication(Context context, byte[] payload) {
        XmPushActionContainer container = XMPushUtils.packToContainer(payload);
        if (container == null) {
            logger.e("forward: container null");
            return null;
        }
        PushMetaInfo metaInfo = container.getMetaInfo();
        String targetPackage = container.getPackageName();

        Intent localIntent = new Intent(PushConstants.MIPUSH_ACTION_NEW_MESSAGE);
        localIntent.setComponent(new ComponentName(targetPackage, TARGET_HANDLER));
        localIntent.putExtra(PushConstants.MIPUSH_EXTRA_PAYLOAD, payload);
        localIntent.putExtra(MIPushNotificationHelper.FROM_NOTIFICATION, true);
        if (metaInfo != null) {
            localIntent.addCategory(String.valueOf(metaInfo.getNotifyId()));
        }

        ComponentName componentName = null;
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                try {
                    componentName = context.startForegroundService(localIntent);
                } catch (Throwable t) {
                    logger.w(packageInfo(targetPackage,
                            "startForegroundService fail, fallback startService: " + t.getMessage()));
                    componentName = context.startService(localIntent);
                }
            } else {
                componentName = context.startService(localIntent);
            }
            logger.i(packageInfo(targetPackage, "startService result=" + componentName));
        } catch (Throwable t) {
            logger.e(packageInfo(targetPackage, "startService failed: " + t.getMessage()), t);
        }

        if (componentName == null) {
            // 降级：广播（部分 App Receiver 仍可收）
            try {
                Intent broadcast = new Intent(PushConstants.MIPUSH_ACTION_NEW_MESSAGE);
                broadcast.setPackage(targetPackage);
                broadcast.putExtra(PushConstants.MIPUSH_EXTRA_PAYLOAD, payload);
                broadcast.putExtra(MIPushNotificationHelper.FROM_NOTIFICATION, true);
                context.sendBroadcast(broadcast);
                logger.i(packageInfo(targetPackage, "fallback broadcast sent"));
            } catch (Throwable t) {
                logger.e(packageInfo(targetPackage, "broadcast failed: " + t.getMessage()), t);
            }
        }
        return componentName;
    }

    private static void activeApp(Context context, String targetPackage) {
        if (Global.ConfigCenter().isIceboxSupported(context)
                && Utils.isAppInstalled(IceBox.PACKAGE_NAME)) {
            try {
                if (ContextCompat.checkSelfPermission(context, IceBox.SDK_PERMISSION)
                        == PackageManager.PERMISSION_GRANTED) {
                    int enabledSetting = IceBox.getAppEnabledSetting(context, targetPackage);
                    if (enabledSetting != 0) {
                        logger.w(packageInfo(targetPackage, "active app by IceBox SDK"));
                        IceBox.setAppEnabledSettings(context, true, targetPackage);
                        return;
                    }
                } else {
                    logger.w(packageInfo(targetPackage,
                            "skip IceBox active due to lack of permissions"));
                }
            } catch (Throwable e) {
                logger.e(packageInfo(targetPackage, "activeApp failed " + e.getLocalizedMessage()), e);
            }
        }
        try {
            Shell.cmd("pm enable " + targetPackage).exec();
        } catch (Throwable ignored) {
        }
    }

    private static Intent getJumpIntent(Context context, XmPushActionContainer container) {
        Intent intent = MyMIPushNotificationHelper.getSdkIntent(context, container);
        if (intent == null) {
            intent = getJumpIntentFromPkg(context, container.packageName);
        }
        return intent;
    }

    private static Intent getJumpIntentFromPkg(Context context, String targetPackage) {
        try {
            return context.getPackageManager().getLaunchIntentForPackage(targetPackage);
        } catch (RuntimeException ignore) {
            return null;
        }
    }

    private void runWithAppStateElevatedToForeground(final String pkg,
                                                     final Consumer<Boolean> task) {
        if (SDK_INT < P) {
            task.accept(Boolean.FALSE);
            return;
        }
        final Intent intent = new Intent()
                .setClassName(pkg, MyMIPushNotificationHelper.CLASS_NAME_PUSH_MESSAGE_HANDLER);
        final Context appContext = getApplicationContext();
        final boolean successful = appContext.bindService(intent, new ServiceConnection() {
            private void runTaskAndUnbind() {
                task.accept(Boolean.TRUE);
                try {
                    appContext.unbindService(this);
                } catch (Throwable ignored) {
                }
            }

            @RequiresApi(P)
            @Override
            public void onNullBinding(final ComponentName name) {
                runTaskAndUnbind();
            }

            @Override
            public void onServiceConnected(final ComponentName name, final IBinder service) {
                runTaskAndUnbind();
            }

            @Override
            public void onServiceDisconnected(final ComponentName name) {
            }
        }, BIND_AUTO_CREATE | BIND_IMPORTANT | BIND_ABOVE_CLIENT);

        if (!successful) {
            task.accept(Boolean.FALSE);
        }
    }

    private static long pullUpApp(Context context, String targetPackage,
                                  XmPushActionContainer container) {
        long start = System.currentTimeMillis();
        try {
            if (iTopActivity == null) {
                iTopActivity = TopActivityFactory.newInstance(
                        Global.ConfigCenter().getAccessMode(context));
            }
            if (!iTopActivity.isAppForeground(context, targetPackage)) {
                logger.d(packageInfo(targetPackage, "app not front, pull up"));
                Intent intent = getJumpIntent(context, container);
                if (intent == null) {
                    throw new RuntimeException("no default activity for " + targetPackage);
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(intent);

                for (int i = 0; i < APP_CHECK_FRONT_MAX_RETRY; i++) {
                    if (iTopActivity.isAppForeground(context, targetPackage)) {
                        break;
                    }
                    Thread.sleep(APP_CHECK_SLEEP_DURATION_MS);
                    if (i == (APP_CHECK_FRONT_MAX_RETRY / 2)) {
                        Intent again = getJumpIntentFromPkg(context, targetPackage);
                        if (again != null) {
                            again.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            context.startActivity(again);
                        }
                    }
                }
                if ((System.currentTimeMillis() - start) >= APP_CHECK_SLEEP_MAX_TIMEOUT_MS) {
                    logger.w(packageInfo(targetPackage, "pull up timeout"));
                }
            } else {
                logger.d(packageInfo(targetPackage, "app foreground"));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (RuntimeException e) {
            logger.e(packageInfo(targetPackage, "pullUpApp failed " + e.getLocalizedMessage()), e);
        }
        return System.currentTimeMillis() - start;
    }

    private static String packageInfo(String packageName, String message) {
        return "[" + packageName + "] " + message;
    }
}
