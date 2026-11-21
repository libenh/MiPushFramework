package top.trumeet.common.utils.rom;

import android.os.Build;

import java.lang.reflect.Method;
import java.util.Locale;

/**
 * Detects ROMs based on the shared OPPO/OnePlus/realme platform.
 *
 * Detection prefers explicit vendor properties. Device brand alone is not
 * sufficient because devices from these vendors can also run AOSP-based ROMs.
 */
public class OplusChecker implements RomChecker {
    private static final String[] OPLUS_ROM_PROPERTIES = {
            "ro.build.version.oplusrom",
            "ro.build.version.oplusrom.display",
            "ro.oplus.version",
            "ro.oxygen.version"
    };

    @Override
    public boolean check() {
        StringBuilder fingerprint = new StringBuilder();
        append(fingerprint, Build.DISPLAY);
        append(fingerprint, Build.FINGERPRINT);

        for (String property : OPLUS_ROM_PROPERTIES) {
            String value = getSystemProperty(property);
            if (value != null && !value.isEmpty()) {
                return true;
            }
            append(fingerprint, value);
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
                || normalized.contains("oxygenos");
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
