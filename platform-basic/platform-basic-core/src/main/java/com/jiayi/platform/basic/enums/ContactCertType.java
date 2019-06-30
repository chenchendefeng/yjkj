package com.jiayi.platform.basic.enums;

//todo t_certification_type表和类只留一个吧？@chengke
public enum ContactCertType {
    CERT_1("1021133", "学生证"),
    CERT_2("1021111", "身份证"),
    CERT_3("1021335", "驾驶证"),
    CERT_4("1021114", "军官证"),
    CERT_5("1021123", "警官证"),
    CERT_6("1021113", "户口簿"),
    CERT_7("1021414", "护照"),
    CERT_8("1021511", "台胞证"),
    CERT_9("1021516", "回乡证"),
    CERT_10("1021159", "社保卡"),
    CERT_11("1021233", "士兵证/军人证"),
    CERT_12("1029999", "其他");

    private String id;
    private String desc;

    private ContactCertType(String id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static String getNameById(String id) {
        for (ContactCertType pt : values()) {
            if (pt.id.equals(id))
                return pt.desc;
        }
        return null;
    }

    public static String getIdByDesc(String desc) {
        for (ContactCertType pt : values()) {
            if (pt.desc.equals(desc))
                return pt.id;
        }
        return null;
    }
}
