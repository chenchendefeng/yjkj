package com.jiayi.platform.basic.enums;

import lombok.Getter;

@Getter
public enum NetType {
    CERT_1("01", "专网、真实IP地址"),
    CERT_2("02", "专线"),
    CERT_3("03", "ADSL拨号"),
    CERT_4("04", "ISDN"),
    CERT_5("05", "普通拨号"),
    CERT_6("06", "Cable modem拨号"),
    CERT_7("07", "电力线"),
    CERT_8("08", "无线上网"),
    CERT_9("99", "其他");

    private String id;
    private String name;

    private NetType(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public static String getNameById(String id) {
        for (NetType pt : values()) {
            if (pt.id.equals(id))
                return pt.getName();
        }
        return null;
    }

    public static String getIdByName(String name) {
        for (NetType pt : values()) {
            if (pt.name.equals(name))
                return pt.getId();
        }
        return null;
    }
}
