package com.jiayi.platform.basic.enums;

import lombok.Getter;

@Getter
public enum ProducerType {
    CERT_1("01", "中国电信", "中国电信互联网"),
    CERT_2("02", "中国网通", "中国网通互联网"),
    CERT_3("03", "中国联通", "中国联通互联网"),
    CERT_4("04", "中国长城宽带", "中国长城宽带互联网"),
    CERT_5("05", "中国铁通", "中国铁通互联网"),
    CERT_6("06", "中国移动", "中国移动互联网"),
    CERT_8("08", "教育部门", "中国教育和科研计算机网"),
    CERT_9("09", "中科院", "中国科技网"),
    CERT_11("11", "广电部门", "广电网"),
    CERT_99("99", "其他", "其他网络");

    private ProducerType(String id, String shortName, String desc) {
        this.id = id;
        this.shortName = shortName;
        this.desc = desc;
    }

    private String id;
    private String shortName;
    private String desc;

    public static String getNameById(String id) {
        for (ProducerType pt : values()) {
            if (id.equals(pt.id))
                return pt.shortName;
        }
        return null;
    }

    public static String getIdByShortName(String shortName) {
        for (ProducerType pt : values()) {
            if (pt.shortName.equals(shortName))
                return pt.id;
        }
        return null;
    }
}
