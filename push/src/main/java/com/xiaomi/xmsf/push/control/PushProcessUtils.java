package com.xiaomi.xmsf.push.control;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Process;

import java.util.List;

/**
 * Process-related checks used by the MiPush runtime.
 */
final class PushProcessUtils {
    private PushProcessUtils() {
    }

    static boolean isMainProcess(Context context) {
        ActivityManager manager = (ActivityManager)
                context.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }

        List<ActivityManager.RunningAppProcessInfo> processes =
                manager.getRunningAppProcesses();
        if (processes == null) {
            return false;
        }

        String packageName = context.getPackageName();
        int pid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo process : processes) {
            if (process.pid == pid && packageName.equals(process.processName)) {
                return true;
            }
        }
        return false;
    }
}
