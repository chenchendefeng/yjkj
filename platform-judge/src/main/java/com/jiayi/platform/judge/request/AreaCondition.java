package com.jiayi.platform.judge.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class AreaCondition {
    @ApiModelProperty(value = "用户给当前条件取的名称", required = true, example = "A", notes = "同一个请求中名称是唯一的")
    private String conditionName;
    @ApiModelProperty(value = "开始时间", required = true, example = "1546567212000")
    private Long beginDate;
    @ApiModelProperty(value = "结束时间", required = true, example = "1546570919000")
    private Long endDate;
    @ApiModelProperty(value = "目标区域")
    private List<Area> areaList;
}
