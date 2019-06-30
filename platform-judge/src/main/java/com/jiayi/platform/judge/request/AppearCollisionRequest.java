package com.jiayi.platform.judge.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString(callSuper=true)
public class AppearCollisionRequest extends JudgeRequest {
    @ApiModelProperty(value = "对象出现/消失开始时间", required = true, example = "1502858803000")
    private Long analyzeBeginDate;
    @ApiModelProperty(value = "对象出现/消失结束时间", required = true, example = "1502869803000")
    private Long analyzeEndDate;
    @ApiModelProperty(value = "时间间隔(天)", required = true, example = "7")
    private Integer refDuration;
    @ApiModelProperty(value = "容错(小时)", required = true, example = "3")
    private Integer bufferTime;
    @ApiModelProperty(value = "对象出现/消失的区域")
    private List<Area> analyzeAreaList;
    @ApiModelProperty(value = "对象消失/出现的区域")
    private List<Area> refAreaList;
    @ApiModelProperty(value = "筛选对象值列表")
    private List<String> objectValueList;
}
