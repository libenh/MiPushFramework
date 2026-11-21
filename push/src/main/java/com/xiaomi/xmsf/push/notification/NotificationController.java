package com.xiaomi.xmsf.push.notification;

import static com.xiaomi.push.service.MyMIPushNotificationHelper.getNotificationTag;
import static com.xiaomi.push.service.MyNotificationIconHelper.KiB;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.drawable.IconCompat;

import com.elvishew.xlog.Logger;
import com.elvishew.xlog.XLog;
import com.nihility.Global;
import com.nihility.XMPushUtils;
import com.nihility.notification.NotificationManagerEx;
import com.xiaomi.push.service.MyNotificationIconHelper;
import com.xiaomi.xmpush.thrift.PushMetaInfo;
import com.xiaomi.xmpush.thrift.XmPushActionContainer;
import com.xiaomi.xmsf.R;
import com.xiaomi.xmsf.push.utils.Configurations;
import com.xiaomi.xmsf.push.utils.IconConfigurations;
import com.xiaomi.xmsf.utils.ColorUtil;

import top.trumeet.common.utils.CustomConfiguration;
import top.trumeet.common.utils.ImgUtils;
import top.trumeet.mipushframework.main.AdvancedSettingsPage;

/**
 * @author Trumeet
 * @date 2018/1/25
 */

public class NotificationController {
    private static final Logger logger = XLog.tag("NotificationController").build();

    private static final String NOTIFICATION_LARGE_ICON = "mipush_notification";
    private static final String NOTIFICATION_SMALL_ICON = "mipush_small_notification";

    public static final String CHANNEL_WARN = "warn";

    public static NotificationManagerEx getNotificationManagerEx() {
        return NotificationManagerEx.INSTANCE;
    }


    @TargetApi(Build.VERSION_CODES.N)
    private static void updateSummaryNotification(Context context, PushMetaInfo metaInfo, String packageName, String groupId) {
        if (groupId == null) {
            return;
        }
        if (!needGroupOfNotifications(packageName, groupId)) {
            getNotificationManagerEx().cancel(packageName, null, groupId.hashCode());
            return;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,
                getExistsChannelId(context, metaInfo, packageName));
        builder.setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN);

        builder.setCategory(Notification.CATEGORY_EVENT)
                .setGroupSummary(true)
                .setGroup(groupId);
        notify(context, groupId.hashCode(), packageName, builder, metaInfo);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private static boolean needGroupOfNotifications(String packageName, String groupId) {
        int notificationCntInGroup = getNotificationCountOfGroup(packageName, groupId);
        return notificationCntInGroup > 1;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private static int getNotificationCountOfGroup(String packageName, String groupId) {
        StatusBarNotification[] activeNotifications =
                getNotificationManagerEx().getActiveNotifications(packageName);


        int notificationCntInGroup = 0;
        for (StatusBarNotification statusBarNotification : activeNotifications) {
            if (groupId.equals(statusBarNotification.getNotification().getGroup())) {
                notificationCntInGroup++;
            }
        }
        return notificationCntInGroup;
    }

    public static void publish(Context context, PushMetaInfo metaInfo, int notificationId, String packageName, NotificationCompat.Builder notificationBuilder) {
        String channelId = getExistsChannelId(context, metaInfo, packageName);
        notificationBuilder.setChannelId(channelId);

        notificationBuilder.setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN);

        //for VERSION < Oero
        notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
        notificationBuilder.setPriority(Notification.PRIORITY_HIGH);

        Notification notification = notify(context, notificationId, packageName, notificationBuilder, metaInfo);

        updateSummaryNotification(context, metaInfo, packageName, notification.getGroup());
    }

    @NonNull
    public static String getExistsChannelId(Context context, PushMetaInfo metaInfo, String packageName) {
        CustomConfiguration custom = XMPushUtils.getConfiguration(metaInfo);
        String channelId = custom.borrowChannelId(null);
        if (TextUtils.isEmpty(channelId) ||
                getNotificationManagerEx().getNotificationChannel(packageName, channelId) == null) {
            // Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
            NotificationChannelManager.registerChannelIfNeeded(context, metaInfo, packageName);
            channelId = NotificationChannelManager.getChannelId(metaInfo, packageName);
        }
        return channelId;
    }

    private static Notification notify(
            Context context, int notificationId, String packageName,
            NotificationCompat.Builder notificationBuilder, PushMetaInfo metaInfo) {
        // Make the behavior consistent with official MIUI
        Bundle extras = new Bundle();
        extras.putString("target_package", packageName);
        notificationBuilder.addExtras(extras);

        // Set small icon
        processIcon(context, packageName, notificationBuilder);

        CustomConfiguration configuration = XMPushUtils.getConfiguration(metaInfo);
        boolean liveUpdateRequested = isLiveUpdateRequested(configuration);
        if (liveUpdateRequested) {
            // ColorOS 16/17 Fluid Cloud follows Android's promoted ongoing notification contract.
            // Keep one stable notification identity: repeated MiPush updates replace this entry,
            // while NotificationController.cancel() ends the activity.
            notificationBuilder
                    .setOngoing(true)
                    .setAutoCancel(false)
                    .setOnlyAlertOnce(true)
                    .setCategory(Notification.CATEGORY_PROGRESS)
                    .setGroup(null)
                    .setGroupSummary(false);
        }
        String iconUri = configuration.notificationLargeIconUri(null);
        Bitmap largeIcon = getLargeIcon(context, metaInfo, iconUri);
        if (largeIcon != null) {
            notificationBuilder.setLargeIcon(largeIcon);
        }

        String subText = configuration.subText(null);
        buildExtraSubText(context, packageName, notificationBuilder, subText);

        String focusParam = configuration.focusParam(null);
        if (focusParam != null) {
            Bundle focusBundle = new Bundle();
            focusBundle.putString("miui.focus.param", focusParam);

            Bundle picsBundle = new Bundle();
            for (String key : configuration.keys()) {
                if (key.startsWith("miui.focus.pic_")) {
                    String url = configuration.get(key, null);
                    focusBundle.putString(key, url);
                    picsBundle.putParcelable(key,
                            getBitmapFromUri(context, iconUri, 200 * KiB));
                }
            }
            if (!picsBundle.isEmpty()) {
                focusBundle.putBundle("miui.focus.pics", picsBundle);
            }
            notificationBuilder.addExtras(focusBundle);
        }

        if (!liveUpdateRequested) {
            notificationBuilder.setAutoCancel(true);
        }
        Notification notification = notificationBuilder.build();
        if (liveUpdateRequested && isEligibleForLiveUpdate(notification)) {
            // EXTRA_REQUEST_PROMOTED_ONGOING is API 36+. Use the platform key so this
            // project can retain its current compileSdk while C17/API 37 handles promotion.
            notification.extras.putBoolean("android.requestPromotedOngoing", true);
            notification.extras.putBoolean("mipush.live_update", true);
        }
        getNotificationManagerEx().notify(
                packageName, getNotificationTag(packageName), notificationId, notification);
        return notification;
    }

    /**
     * Live Updates are opt-in per MiPush payload. Ordinary chat/alert notifications must not be
     * promoted into an ongoing activity. The stable notificationId supplied to publish() is the
     * activity identity; publishing again updates it and cancel() ends it.
     */
    private static boolean isLiveUpdateRequested(CustomConfiguration configuration) {
        return Build.VERSION.SDK_INT >= 36
                && "true".equalsIgnoreCase(configuration.get("mipush.live_update", "false"));
    }

    private static boolean isEligibleForLiveUpdate(Notification notification) {
        if (notification == null || notification.extras == null) {
            return false;
        }
        if ((notification.flags & Notification.FLAG_ONGOING_EVENT) == 0
                || (notification.flags & Notification.FLAG_GROUP_SUMMARY) != 0
                || notification.contentView != null
                || notification.bigContentView != null
                || notification.headsUpContentView != null) {
            return false;
        }
        CharSequence title = notification.extras.getCharSequence(Notification.EXTRA_TITLE);
        if (TextUtils.isEmpty(title)) {
            return false;
        }
        String template = notification.extras.getString("android.template");
        return TextUtils.isEmpty(template)
                || template.contains("BigTextStyle")
                || template.contains("ProgressStyle")
                || template.contains("CallStyle")
                || template.contains("MetricStyle");
    }

    @Nullable
    public static Bitmap getLargeIcon(Context context, PushMetaInfo metaInfo, String iconUri) {
        Bitmap largeIcon = Global.IconCache().getBitmap(context, iconUri,
                (context1, iconUri1) -> getBitmapFromUri(context1, iconUri1, 200 * KiB));
        if (largeIcon != null) {
            largeIcon = roundLargeIconIfConfigured(metaInfo, largeIcon);
        }
        return largeIcon;
    }

    public static Bitmap roundLargeIconIfConfigured(PushMetaInfo metaInfo, Bitmap largeIcon) {
        CustomConfiguration custom = XMPushUtils.getConfiguration(metaInfo);
        if (custom.roundLargeIcon(false)) {
            largeIcon = ImgUtils.trimImgToCircle(largeIcon, Color.TRANSPARENT);
        }
        return largeIcon;
    }

    @Nullable
    public static Bitmap getBitmapFromUri(Context context, String iconUri, int maxDownloadBytes) {
        Bitmap bitmap = null;
        if (iconUri != null) {
            if (iconUri.startsWith("http")) {
                MyNotificationIconHelper.GetIconResult result =
                        MyNotificationIconHelper.getIconFromUrl(context, iconUri, maxDownloadBytes);
                if (result != null) {
                    bitmap = result.bitmap;
                }
            } else {
                bitmap = MyNotificationIconHelper.getIconFromUri(context, iconUri);
            }
        }
        return bitmap;
    }

    public static void cancel(Context context, XmPushActionContainer container,
                              int notificationId, String notificationGroup, boolean clearGroup) {
        getNotificationManagerEx().cancel(container.getPackageName(),
                getNotificationTag(container), notificationId);

        if (clearGroup) {
            getNotificationManagerEx().cancel(container.getPackageName(),
                    getNotificationTag(container), notificationGroup.hashCode());
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationGroup != null) {
                XmPushActionContainer copy = container.deepCopy();
                try {
                    Configurations.getInstance().handle(container.packageName, copy);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                updateSummaryNotification(context, copy.metaInfo, container.getPackageName(), notificationGroup);
            }
        }
    }


    /**
     * @param ctx context
     * @param pkg packageName
     * @return 0 if not processed
     */
    public static int getIconColor(final Context ctx, final String pkg) {
        return Global.IconCache().getAppColor(ctx, pkg, (ctx1, iconBitmap) -> {
            if (iconBitmap == null) {
                return Notification.COLOR_DEFAULT;
            }
            int color = ColorUtil.getIconColor(iconBitmap);
            if (color != Notification.COLOR_DEFAULT) {
                final float[] hsl = new float[3];
                ColorUtils.colorToHSL(color, hsl);
                hsl[1] = 0.94f;
                hsl[2] = Math.min(hsl[2] * 0.6f, 0.31f);
                return ColorUtils.HSLToColor(hsl);
            } else {
                return Notification.COLOR_DEFAULT;
            }
        });
    }


    public static void processIcon(Context context, String packageName, NotificationCompat.Builder notificationBuilder) {
        notificationBuilder.setSmallIcon(R.drawable.ic_notifications_black_24dp);

        // refer: https://dev.mi.com/console/doc/detail?pId=2625#_5_0
        Context pkgContext = null;
        try {
            pkgContext = context.createPackageContext(packageName, Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
        } catch (PackageManager.NameNotFoundException e) {
            return;
        }
        int largeIconId = getIconId(context, packageName, NOTIFICATION_LARGE_ICON);
        int smallIconId = getIconId(context, packageName, NOTIFICATION_SMALL_ICON);

        if (largeIconId > 0) {
            notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(pkgContext.getResources(), largeIconId));
        }

        notificationBuilder.setColor(getIconColor(context, packageName));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            IconConfigurations.IconConfig iconConfig = Global.IconConfigurations().get(packageName);
            if (iconConfig != null && iconConfig.isEnabled && iconConfig.isEnabledAll) {
                Bitmap iconBitmap = iconConfig.bitmap();
                if (iconBitmap != null) {
                    notificationBuilder.setSmallIcon(IconCompat.createWithBitmap(iconBitmap));
                    notificationBuilder.setColor(iconConfig.color());
                    return;
                }
            }

            if (smallIconId > 0) {
                notificationBuilder.setSmallIcon(IconCompat.createWithResource(pkgContext, smallIconId));
                return;
            }
            if (largeIconId > 0) {
                notificationBuilder.setSmallIcon(IconCompat.createWithResource(pkgContext, largeIconId));
                return;
            }

            Bitmap iconBitmap = iconConfig == null ? null : iconConfig.bitmap();
            if (iconBitmap != null && iconConfig.isEnabled) {
                notificationBuilder.setSmallIcon(IconCompat.createWithBitmap(iconBitmap));
                notificationBuilder.setColor(iconConfig.color());
                return;
            }

            IconCompat iconCache = Global.IconCache().getIconCache(context, packageName, (ctx, b) -> IconCompat.createWithBitmap(b));
            if (iconCache != null) {
                notificationBuilder.setSmallIcon(iconCache);
                return;
            }
        }
    }

    public static void buildExtraSubText(Context context, String packageName, NotificationCompat.Builder localBuilder, CharSequence text) {
        if ("".equals(text)) {
            localBuilder.setSubText(null);
            return;
        }
        if (text == null) {
            text = Global.ApplicationNameCache().getAppName(context, packageName);
        }
        int color = localBuilder.getColor();
        if (color == Notification.COLOR_DEFAULT) {
            localBuilder.setSubText(text);
            return;
        }
        CharSequence subText = ColorUtil.createColorSubtext(text, color);
        localBuilder.setSubText(subText);
    }

    private static int getIconId(Context context, String packageName, String resourceName) {
        return context.getResources().getIdentifier(resourceName, "drawable", packageName);
    }


    public static void test(Context context, String packageName, String title, String description) {
        NotificationChannelManager.registerChannelIfNeeded(context, new PushMetaInfo(), packageName);

        int id = (int) (System.currentTimeMillis() / 1000L);

        NotificationCompat.Builder localBuilder = new NotificationCompat.Builder(context);

        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
        style.bigText(description);
        style.setBigContentTitle(title);
        style.setSummaryText(description);
        localBuilder.setStyle(style);
        localBuilder.setWhen(System.currentTimeMillis());
        localBuilder.setShowWhen(true);

        Intent notifyIntent = new Intent(context, AdvancedSettingsPage.class);
        // Set the Activity to start in a new, empty task
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Create the PendingIntent
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(
                context, 0, notifyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        localBuilder.setContentIntent(notifyPendingIntent);

        NotificationController.publish(context, new PushMetaInfo(), id, packageName, localBuilder);
    }

}
