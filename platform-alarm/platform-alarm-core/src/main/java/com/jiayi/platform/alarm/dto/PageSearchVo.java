package com.jiayi.platform.alarm.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageSearchVo {

    @ApiModelProperty(value = "页码（0开始）", example = "0", required = true)
    private Integer page = 0;
    @ApiModelProperty(value = "每页数量", example = "10" ,required = true)
    private Integer size = 10;

}
