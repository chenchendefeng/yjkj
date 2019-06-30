package com.jiayi.platform.judge.enums;

public enum ResultStatusEnum {
    SUCCESS(0,"success"),
    WARNING(1,"warning"),
    FAILURE(2,"failure"),
    DUPLICATE(3, "duplicate");

    private int code;
    private String message;

    ResultStatusEnum(int code, String message){
        this.code = code;
        this.message = message;
    }

    public int code(){
        return code;
    }

    public String message(){
        return message;
    }
}
