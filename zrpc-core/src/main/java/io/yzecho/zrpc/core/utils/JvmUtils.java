package io.yzecho.zrpc.core.utils;

public final class JvmUtils {
    private JvmUtils() {
    }

    public static int availableProcessors() {
        return Runtime.getRuntime().availableProcessors();
    }

    public static int getIntSystemProperty(String propertyName, int defaultValue) {
        try {
            String property = System.getProperty(propertyName);
            if (property == null) {
                return defaultValue;
            }
            return Integer.parseInt(property);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static boolean getBoolSystemProperty(String propertyName, boolean defaultValue) {
        try {
            String property = System.getProperty(propertyName);
            if (property == null) {
                return defaultValue;
            }
            return Boolean.parseBoolean(property);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
