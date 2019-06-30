package com.jiayi.platform.judge.enums;

import com.jiayi.platform.common.exception.ArgumentException;

public enum FileImportTypeEnum {
    MAC(1,"MAC","String"),
    IMSI(2,"IMSI","String"),
    IMEI(3,"IMEI","String"),
    CARNO(4,"车牌","String"),
    DATE(5,"日期","Bigint"),
    FIGURE(6,"数字","Bigint"),
    OTHER(7,"其他","String");

    private Integer id;
    private String name;
    private String type;

    FileImportTypeEnum(Integer id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public Integer id() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String type() {
        return type;
    }

    public static FileImportTypeEnum getTypeById(Integer id){
        for(FileImportTypeEnum value : values()){
            if(value.id().equals(id)){
                return value;
            }
        }
        throw new ArgumentException("针对导入列数据，请输入或选择正确的数据类型");
    }
}
