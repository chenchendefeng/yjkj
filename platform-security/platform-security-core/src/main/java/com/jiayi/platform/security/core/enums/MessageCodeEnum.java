package com.jiayi.platform.security.core.enums;


import lombok.Getter;

@Getter
public enum MessageCodeEnum {
    SUCCESS(0, "success"),
    FAILED(1, "failed");

    private int code;
    private String message;

    MessageCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
