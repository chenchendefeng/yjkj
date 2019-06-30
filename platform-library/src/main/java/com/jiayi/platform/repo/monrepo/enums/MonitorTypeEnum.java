package com.jiayi.platform.repo.monrepo.enums;

import com.jiayi.platform.common.exception.ArgumentException;

public enum MonitorTypeEnum {
    WEIWEN(1, "维稳"),
    QIANKE(2, "前科"),
    HUANGDUDU(3, "黄赌毒"),
    DAOQIE(4, "盗窃"),
    QIANGDUO(5, "抢夺"),
    ZHONGXING(6, "重型"),
    ;

    private int type;
    private String desc;

    private MonitorTypeEnum(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public int getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

    public static int getTypeByDesc(String desc) {
        for (MonitorTypeEnum monType : values()) {
            if (monType.getDesc().equals(desc)) {
                return monType.getType();
            }
        }
        throw new ArgumentException("invalid monitor type desc: " + desc);
    }

    public static String getDescByType(int type) {
        for (MonitorTypeEnum monType : values()) {
            if(monType.type == type)
                return monType.getDesc();
        }
        throw new ArgumentException("invalid monitor type: " + type);
    }
}
