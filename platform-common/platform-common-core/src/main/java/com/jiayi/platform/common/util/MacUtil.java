package com.jiayi.platform.common.util;

import org.apache.commons.lang3.StringUtils;

public class MacUtil {

    /**
     * 转化为无":" "-" 其它字符,字母大字的MAC地址.
     * 例如输入为DC-FE-18-9A-33-84 输出为DCFE189A3384
     * dc:fe:18:9a:36:88 输出为DCFE189A3688
     * @param value ""
     * @return
     */
    public static String toTrimMac(String value){
        if (StringUtils.isEmpty(value)){
            return value;
        }
        return value.toUpperCase().replaceAll("[-:：\\s]", "");
    }

    /**
     * 取设备编码后12位生成mac地址
      */
    public static String generateMac(String code) {
        int length = code.length();
        if (length <= 12) {
            return code;
        } else {
            return code.substring(length - 12);
        }
    }
}