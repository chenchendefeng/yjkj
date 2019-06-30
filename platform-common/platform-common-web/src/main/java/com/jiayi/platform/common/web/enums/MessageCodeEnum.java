package com.jiayi.platform.common.web.enums;

public enum MessageCodeEnum {
    SUCCESS(0, "success"),
    FAILED(1, "failed"),
    ;

    private int code;
    private String message;

    MessageCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
