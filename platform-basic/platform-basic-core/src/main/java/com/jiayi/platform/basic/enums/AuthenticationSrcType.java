package com.jiayi.platform.basic.enums;

public enum AuthenticationSrcType {
    SRC1(1, "ID卡"),
    SRC2(2, "手工输入"),
    SRC3(3, "刷二代证"),
    SRC4(4, "扫描"),
    SRC5(5, "手机激活"),
    SRC6(6, "证件号码为主账户"),
    SRC7(7, "手机号码为主账户"),
    SRC8(8, "手机绑定点的普通开卡"),
    SRC9(9, "手机绑定点的会员开卡"),
    SRC10(10, "手机买序列号的普通卡"),
    SRC11(11, "手机买序列号的会员卡"),
    SRC12(12, "刷指纹获取卡号"),
    SRC13(13, "扫描任子行"),
    SRC14(14, "扫描聚宝APP"),
    SRC15(15, "扫描九威APP"),
    SRC16(16, "扫描万象APP"),
    SRC17(17, "人脸识别开卡"),
    SRC18(18, "其他");
    int id;
    String desc;

    AuthenticationSrcType(int id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public static String getDescById(int id) {
        for (AuthenticationSrcType src : values()) {
            if (src.id == id) {
                return src.desc;
            }
        }
        return "";
    }

    public static Integer getIdByDesc(String desc) {
        for (AuthenticationSrcType src : values()) {
            if (src.desc.equals(desc)) {
                return src.id;
            }
        }
        return null;
    }

    public String desc() {
        return desc;
    }

    public int id() {
        return id;
    }
}
