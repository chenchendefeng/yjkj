package com.jiayi.platform.basic.enums;

import lombok.Getter;

@Getter
public enum OperatingStatus {
    STATUS1(1, "装机开业在线"),
    STATUS2(2, "装机开业离线"),
    STATUS6(6, "勒令停业"),
    STATUS10(10, "其他"),
    STATUS12(12, "暂停营业"),
    STATUS14(14, "预约安装");
    private Integer status;
    private String desc;

    private OperatingStatus(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static String getNameByStatus(Integer status) {
        for (OperatingStatus pt : values()) {
            if (pt.status == status)
                return pt.getDesc();
        }
        return null;
    }

    public static Integer getStatusByDesc(String desc) {
        for (OperatingStatus pt : values()) {
            if (pt.desc.equals(desc))
                return pt.status;
        }
        return null;
    }
}
