package com.jiayi.platform.common.enums;

/**
 * @author : weichengke
 * @date : 2019-03-06 11:48
 */
public enum CollectType {

    OTHER(0, "OTHER", "", CollectCategory.UNKNOWN),
    MAC(1, "MAC", "MAC", CollectCategory.TRACK),
    CARNO(2, "CARNO", "车牌", CollectCategory.TRACK),
    // NOTE:IMEI和IMSI是共存的，所以lable是一样的
    IMEI(3, "IMEI", "IMEI", CollectCategory.TRACK),
    IMSI(4, "IMSI", "IMSI", CollectCategory.TRACK),
    HEARTBEAT(5, "HEARTBEAT", "设备心跳", CollectCategory.HEARTBEAT),
    VIRTUAL(6, "VIRTUAL", "虚拟身份", CollectCategory.AUDIT),
    EMAIL(7, "EMAIL", "E-Mail", CollectCategory.AUDIT),
    BBS(8, "BBS", "论坛微博", CollectCategory.AUDIT),
    SEARCH(9, "SEARCH", "搜索引擎", CollectCategory.AUDIT),
    INTERNET(10, "INTERNET", "网页浏览", CollectCategory.AUDIT),
    FILE_TRANSFER(11, "FILE_TRANSFER", "文件传输", CollectCategory.AUDIT),
    TELNET(12, "TELNET", "远程登陆", CollectCategory.AUDIT),
    TERMINAL_LINE(13, "TERMINAL_LINE", "终端上下线", CollectCategory.AUDIT),
    AUTH(14, "AUTH", "账号认证", CollectCategory.AUDIT),
    PHONE(15, "PHONE", "PHONE", CollectCategory.TRACK),
    APMAC(16, "APMAC", "APMAC", CollectCategory.TRACK),

    V_QQ(1030001, "V_QQ", "QQ", CollectCategory.AUDIT),
    V_WE_CHAT(1030036, "V_WE_CHAT", "微信", CollectCategory.AUDIT),
    V_SINA_BLOGS(1330001, "V_SINA_BLOGS", "新浪微博", CollectCategory.AUDIT),
    V_TAOBAO(1279566, "V_TAOBAO", "淘宝", CollectCategory.AUDIT),
    V_OTHER(9999999, "V_OTHER", "其他虚拟身份", CollectCategory.AUDIT);


    int code;
    @Deprecated
    String lable;
    String desc;
    CollectCategory category;


    CollectType(int code, String lable, String desc, CollectCategory category) {
        this.code = code;
        this.lable = lable;
        this.desc = desc;
        this.category = category;
    }

    public String lable() {
        return lable;
    }

    public int code() {
        return code;
    }

    public String desc() {
        return desc;
    }

    public CollectCategory category() {
        return category;
    }

    public static CollectType getByCode(long code) {
        CollectType[] values = CollectType.values();
        for (int i = 0; i < values.length; i++) {
            if (values[i].code == code)
                return values[i];
        }
        return OTHER;
    }

    public static CollectType getByLabel(String lable) {
        CollectType[] values = CollectType.values();
        for (int i = 0; i < values.length; i++) {
            if (values[i].lable.equals(lable))
                return values[i];
        }
        return OTHER;
    }

    public static CollectType getByDesc(String desc) {
        CollectType[] values = CollectType.values();
        for (int i = 0; i < values.length; i++) {
            if (values[i].desc.equals(desc))
                return values[i];
        }
        return OTHER;
    }
    }
