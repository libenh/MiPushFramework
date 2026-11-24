package com.nihility.utils;

import android.os.Build;
import android.text.TextUtils;

import com.elvishew.xlog.XLog;

/**
 * ColorOS / OxygenOS / OnePlus / realme 检测。
 * O系建议使用 vc105（startService 路径）或强制注册逻辑。
 */
public final class OPlusRom {
    private static Boolean cached;

    private OPlusRom() {}

    public static boolean isOPlus() {
        if (cached != null) {
            return cached;
        }
        boolean result = false;
        try {
            String brand = (Build.BRAND + " " + Build.MANUFACTURER + " " + Build.PRODUCT).toLowerCase();
            if (brand.contains("oppo") || brand.contains("oneplus") || brand.contains("realme")) {
                result = true;
            }
            if (!result) {
                String[] keys = {
                        "ro.build.version.oplusrom",
                        "ro.build.version.opporom",
                        "ro.oplus.version",
                        "ro.oppo.version"
                };
                for (String key : keys) {
                    String v = getProp(key);
                    if (!TextUtils.isEmpty(v)) {
                        result = true;
                        break;
                    }
                }
            }
        } catch (Throwable t) {
            XLog.tag("OPlusRom").e(t.getMessage(), t);
        }
        cached = result;
        XLog.tag("OPlusRom").i("isOPlus=" + result);
        return result;
    }

    public static String colorOsVersion() {
        for (String key : new String[]{
                "ro.build.version.oplusrom",
                "ro.build.version.opporom",
                "ro.oplus.version"
        }) {
            String v = getProp(key);
            if (!TextUtils.isEmpty(v)) {
                return v;
            }
        }
        return "";
    }

    private static String getProp(String key) {
        try {
            Class<?> clz = Class.forName("android.os.SystemProperties");
            return (String) clz.getMethod("get", String.class, String.class).invoke(null, key, "");
        } catch (Throwable ignored) {
            return "";
        }
    }
}
