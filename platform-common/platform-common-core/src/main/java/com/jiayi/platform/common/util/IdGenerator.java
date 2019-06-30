package com.jiayi.platform.common.util;

public class IdGenerator {
    public static long generateDeviceId(Object src, String code) {
//        return src << 48 | (FnvHash.fnv1Hash64(code) & 0xFFFFFFFFFFFFL);
    	return FnvHash.fnv1Hash64(src+ "|" +code);
    }

//    public static long generateDeviceId(long src, String code) {
//        int len = code.length();
//        try {
//            if (src == 1001L) {
//                if (len == 12) {
//                    //try as mac
//                    return src << 48 | Long.parseLong(code, 16);
//                } else if (len == 21) {
//                    return src << 48 | Long.parseLong(code.substring(9), 16);
//                } else {
//                    return genHashId(src, code);
//                }
//            } else if (src == 1002L || src == 1003L) {
//                return src << 48 | (Long.parseLong(code) & 0xFFFFFFFFFFFFL);
//            } else {
//                return genHashId(src, code);
//            }
//        } catch (Exception e) {
//            return genHashId(src, code);
//        }
//    }

    public static long generateObjectId(String type, String code) {
//        try {
//            switch (type.toUpperCase()) {
//                case "MAC":
//                    return Long.parseLong(code, 16);
//                case "IMEI":
//                case "IMSI":
//                    return Long.parseLong(code, 10);
//                default:
                    return FnvHash.fnv1Hash64(code);
//            }
//        } catch (Exception e) {
//            return FnvHash.fnv1Hash64(code);
//        }
    }
}