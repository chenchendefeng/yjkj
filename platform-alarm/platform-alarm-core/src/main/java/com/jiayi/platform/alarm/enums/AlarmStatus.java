package com.jiayi.platform.alarm.enums;

/**
 * 布控状态
 */
public enum AlarmStatus {

    CLOSE(0),
    OPEN(1);

    private int code;

    AlarmStatus(int code) {
        this.code = code;
    }

    public int code(){
        return code;
    }
}
