package com.jiayi.platform.alarm.enums;

public enum AssembleType {

    Area(1,"指定区域"),DISTANCE(2,"动态聚集");

    private int code;
    private String name;

    AssembleType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static AssembleType getEnumByName(String name) {
        for(AssembleType assembleType : AssembleType.values()) {
            if(assembleType.name.equals(name)){
                return assembleType;
            }
        }
        return null;
    }
}
