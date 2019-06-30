package com.jiayi.platform.common.web.dto;

import com.jiayi.platform.common.web.enums.MessageCodeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel(description = "返回结果")
public class JsonObject<T> implements Serializable {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "返回业务对象", name = "业务对象", example = "业务对象")
    private T payLoad;

    @ApiModelProperty(value = "消息代码描述", name = "消息代码对应的描述信息", example = "未登录")
    private String message;

    @ApiModelProperty(value = "消息代码", name = "code", example = "100")
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

    public T getPayLoad() {
        return payLoad;
    }

    public void setPayLoad(T payLoad) {
        this.payLoad = payLoad;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
