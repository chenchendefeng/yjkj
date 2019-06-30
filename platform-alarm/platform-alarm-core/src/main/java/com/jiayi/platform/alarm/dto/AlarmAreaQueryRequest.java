package com.jiayi.platform.alarm.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AlarmAreaQueryRequest {
    @ApiModelProperty(value = "区域名称", example = "城中派出所门口")
    private String name;
    @ApiModelProperty(value = "是否启用", example = "1")
    private Integer enable;
    @ApiModelProperty(value = "父区域id,顶层根区域传0，子区域传的父区域id", example = "1")
    private Long parentAreaId;
    @ApiModelProperty(value = "第几页", example = "1")
    private Integer page;
    @ApiModelProperty(value = "分页大小", example = "20")
    private Integer size;
    @ApiModelProperty(value = "0:不需要，1：需要", example = "是否需要返回父区域")
    private Integer isParentArea=0;
    @ApiModelProperty(value = "1为mac,2为imsi,4为carno", example = "1为mac,2为imsi,4为carno")
    private Integer type=0;
}
