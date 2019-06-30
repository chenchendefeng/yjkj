package com.jiayi.platform.alarm.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class AlarmCaseMsgSearchVo extends PageSearchVo{

    @NotNull(message = "用户id不能为空")
    @ApiModelProperty(value = "用户id",required = true)
    private Integer userId;
    @ApiModelProperty(value = "案件名称")
    private String caseName;
    @ApiModelProperty(value = "预警对象")
    private String suspects;
    @ApiModelProperty(value = "特征类型", example = "车牌")
    private String objectType;
    @ApiModelProperty(value = "特征值")
    private String objectValue;
    @ApiModelProperty(value = "策略类型:0全部、2触碰、3入圈、4出圈、5消失、6指定区域聚集、7动态聚集")
    private Integer type;
    @ApiModelProperty(value = "开始时间")
    private long startTime;
    @ApiModelProperty(value = "结束时间")
    private long endTime;
    @ApiModelProperty(value = "状态:0未读 1已读")
    private Integer status;
}
