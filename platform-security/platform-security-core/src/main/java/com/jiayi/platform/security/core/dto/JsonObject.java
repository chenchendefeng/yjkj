package com.jiayi.platform.security.core.dto;

import com.jiayi.platform.security.core.enums.MessageCodeEnum;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class JsonObject<T> implements Serializable {
    private T payLoad;
    private String message;
    private int code;

    public JsonObject(T payLoad) {
        this(payLoad, MessageCodeEnum.SUCCESS);
    }

    public JsonObject(T payLoad, MessageCodeEnum messageCode) {
        this(payLoad, messageCode.getCode(), messageCode.getMessage());
    }

    public JsonObject(T payLoad, int code, String message) {
        this.payLoad = payLoad;
        this.message = message;
        this.code = code;
    }

    public JsonObject() {
    }
}
