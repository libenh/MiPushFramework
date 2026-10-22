package com.nihility.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.service.notification.StatusBarNotification
import com.elvishew.xlog.XLog
import java.lang.reflect.InvocationTargetException

object NotificationManagerEx {
    private const val TAG = "NotificationManagerEx"

    private lateinit var notificationManager: NotificationManager

    @JvmField
    var isHooked: Boolean = false

    @JvmStatic
    fun init(context: Context) {
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    /**
     * Try posting under the target app's identity on ROMs that expose the
     * privileged notifyAsPackage API to this process. Ordinary app processes
     * are not granted this capability automatically; retain a safe fallback.
     */
    fun notify(packageName: String, tag: String?, id: Int, notification: Notification) {
        XLog.d(TAG, "notify requested: target=$packageName tag=$tag id=$id")
        if (tryNotifyAsPackage(packageName, tag, id, notification)) return
        notificationManager.notify(tag, id, notification)
    }

    private fun tryNotifyAsPackage(
        packageName: String,
        tag: String?,
        id: Int,
        notification: Notification
    ): Boolean {
        if (packageName.isBlank()) return false
        return try {
            val method = NotificationManager::class.java.getMethod(
                "notifyAsPackage",
                String::class.java,
                String::class.java,
                Int::class.javaPrimitiveType,
                Notification::class.java
            )
            method.invoke(notificationManager, packageName, tag, id, notification)
            XLog.i(TAG, "notifyAsPackage succeeded for $packageName")
            true
        } catch (e: NoSuchMethodException) {
            XLog.d(TAG, "notifyAsPackage unavailable on this Android/ROM build")
            false
        } catch (e: InvocationTargetException) {
            XLog.w(TAG, "notifyAsPackage rejected for $packageName: ${e.targetException}")
            false
        } catch (e: ReflectiveOperationException) {
            XLog.w(TAG, "notifyAsPackage reflection failed for $packageName: $e")
            false
        } catch (e: SecurityException) {
            XLog.w(TAG, "notifyAsPackage permission denied for $packageName")
            false
        }
    }

    fun cancel(packageName: String, tag: String?, id: Int) {
        XLog.d(TAG, "cancel() called with: packageName = $packageName, tag = $tag, id = $id")
        notificationManager.cancel(tag, id)
    }

    fun createNotificationChannels(packageName: String, channels: List<NotificationChannel?>) {
        XLog.d(TAG, "createNotificationChannels() called with: packageName = $packageName, channels = $channels")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannels(channels.filterNotNull())
        }
    }

    fun getNotificationChannel(packageName: String, channelId: String?): NotificationChannel? {
        XLog.d(TAG, "getNotificationChannel() called with: packageName = $packageName, channelId = $channelId")
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) notificationManager.getNotificationChannel(channelId) else null
    }

    fun getNotificationChannels(packageName: String): List<NotificationChannel?>? {
        XLog.d(TAG, "getNotificationChannels() called with: packageName = $packageName")
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) notificationManager.notificationChannels else emptyList()
    }

    fun deleteNotificationChannel(packageName: String, channelId: String?) {
        XLog.d(TAG, "deleteNotificationChannel() called with: packageName = $packageName, channelId = $channelId")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) notificationManager.deleteNotificationChannel(channelId)
    }

    fun createNotificationChannelGroups(packageName: String, groups: List<NotificationChannelGroup?>) {
        XLog.d(TAG, "createNotificationChannelGroups() called with: packageName = $packageName, groups = $groups")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannelGroups(groups.filterNotNull())
        }
    }

    fun getNotificationChannelGroup(packageName: String, groupId: String?): NotificationChannelGroup? {
        XLog.d(TAG, "getNotificationChannelGroup() called with: packageName = $packageName, groupId = $groupId")
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return null
        return try {
            notificationManager.getNotificationChannelGroup(groupId)
        } catch (e: RuntimeException) {
            XLog.w(TAG, "Unable to query channel group $groupId: $e")
            null
        }
    }

    fun getNotificationChannelGroups(packageName: String): List<NotificationChannelGroup?>? {
        XLog.d(TAG, "getNotificationChannelGroups() called with: packageName = $packageName")
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) notificationManager.notificationChannelGroups else emptyList()
    }

    fun deleteNotificationChannelGroup(packageName: String, groupId: String?) {
        XLog.d(TAG, "deleteNotificationChannelGroup() called with: packageName = $packageName, groupId = $groupId")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) notificationManager.deleteNotificationChannelGroup(groupId)
    }

    fun areNotificationsEnabled(packageName: String): Boolean {
        XLog.d(TAG, "areNotificationsEnabled() called with: packageName = $packageName")
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) notificationManager.areNotificationsEnabled() else true
    }

    fun getActiveNotifications(packageName: String): Array<StatusBarNotification?>? {
        XLog.d(TAG, "getActiveNotifications() called with: packageName = $packageName")
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) notificationManager.activeNotifications else emptyArray()
    }
}
