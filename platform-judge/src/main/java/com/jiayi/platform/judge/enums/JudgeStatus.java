package com.jiayi.platform.judge.enums;

import com.jiayi.platform.common.exception.ArgumentException;

/**
 * 碰撞分析的查询历史状态
 */
public enum JudgeStatus {
    UNKNOWN(0,"未知"),
    WAITING(1, "等待执行"),
    CALCULATING(2, "运算中"),
    FAILED(3, "执行失败"),
    SUCCEED(4, "完成"),
    CANCELED(5, "取消"),
    DELETED(6, "已删除");

    private int code;
    private String message;

    JudgeStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }

    public static JudgeStatus getStatusByCode(int code) {
        for (JudgeStatus status : values()) {
            if (code == status.code())
                return status;
        }
        throw new ArgumentException("invalid status code: " + code);
    }
}