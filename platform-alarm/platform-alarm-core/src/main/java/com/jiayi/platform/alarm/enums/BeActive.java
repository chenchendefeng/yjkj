package com.jiayi.platform.alarm.enums;

/**
 * 策略删除状态
 */
public enum BeActive {
    NORMAL(0),
    DELETE(1);

    private int code;

    BeActive(int code) {
        this.code = code;
    }

    public int code(){
        return code;
    }
}
