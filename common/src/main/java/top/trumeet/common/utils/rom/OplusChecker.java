package top.trumeet.common.utils.rom;

import android.os.Build;

import java.lang.reflect.Method;
import java.util.Locale;

/**
 * Detects the shared OPPO/OnePlus/realme ROM family without relying on a
 * single vendor property, which may be absent on regional builds.
 */
public class OplusChecker implements RomChecker {
    private static final String[] ROM_PROPERTIES = {
            "ro.build.version.oplusrom",
            "ro.build.version.oplusrom.display",
            "ro.oplus.version",
            "ro.build.version.ota"
    };

    @Override
    public boolean check() {
        StringBuilder fingerprint = new StringBuilder();
        append(fingerprint, Build.DISPLAY);
        append(fingerprint, Build.FINGERPRINT);
        append(fingerprint, Build.MANUFACTURER);
        append(fingerprint, Build.BRAND);

        for (String property : ROM_PROPERTIES) {
            append(fingerprint, getSystemProperty(property));
        }
        return containsOplusRomMarker(fingerprint.toString());
    }

    static boolean containsOplusRomMarker(String value) {
        if (value == null) {
            return false;
        }
        String normalized = value.toLowerCase(Locale.ROOT);
        return normalized.contains("coloros")
                || normalized.contains("realme ui")
                || normalized.contains("realmeui")
                || normalized.contains("oxygenos")
                || normalized.contains("oplusrom");
    }

    private static void append(StringBuilder target, String value) {
        if (value != null && !value.isEmpty()) {
            target.append(' ').append(value);
        }
    }

    private static String getSystemProperty(String key) {
        try {
            Class<?> systemProperties = Class.forName("android.os.SystemProperties");
            Method get = systemProperties.getMethod("get", String.class);
            Object value = get.invoke(null, key);
            return value instanceof String ? (String) value : "";
        } catch (Throwable ignored) {
            return "";
        }
    }
}
