package com.jiayi.platform.judge.util;

import com.jiayi.platform.common.util.IdGenerator;

public class ImpalaDataUtil {
    public static final int OBJECT_HASH_SIZE = 100;

    public static double convertLngAndLat2Double(long value) {
        return convertLngAndLat2Double(value, 1e6);
    }

    public static double convertLngAndLat2Double(long value, double scale) {
        return value / scale;
    }

    public static String addMacCodeColons(String objCode, String type) {
        if (!type.toLowerCase().equals("mac") || objCode.length() != 12) {
            return objCode;
        }
        return objCode.replaceAll("(\\w{2})(?!$)", "$0:");
    }

    public static long objectValue2Id(String objectValue, String objectType) {
        String newObjectValue = objectValue.toUpperCase().replaceAll("[-:：\\s]", "");

        return IdGenerator.generateObjectId(objectType, newObjectValue);
    }

    public static int getObjectHash(String objectValue) {
        return Math.abs(objectValue.hashCode() % OBJECT_HASH_SIZE);
    }
}