package com.jiayi.platform.judge.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FileObjectTypeInfo {
    @ApiModelProperty(value = "数据名称", required = true)
    private String fieldDesc;
    @ApiModelProperty(value = "数据类型:1Mac、2IMSI、3IMEI、4车牌、5日期、6数字、7其他", required = true)
    private Integer fieldType;
    @ApiModelProperty(value = "是否导入", required = true)
    private boolean isImport;
    @ApiModelProperty(value = "title的下标,1开始", required = true)
    private Integer index;
    @ApiModelProperty(value = "数据类型:String、BigInt等", required = true)
    private String type;
}
