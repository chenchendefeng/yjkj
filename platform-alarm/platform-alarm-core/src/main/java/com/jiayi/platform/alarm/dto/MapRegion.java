package com.jiayi.platform.alarm.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MapRegion {
    @ApiModelProperty(value = "区域类型", required = true, example = "rectangle")
    private String type;
    @ApiModelProperty(value = "构成区域的点集", required = true)
    private List<Points> points;
}
