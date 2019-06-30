package com.jiayi.platform.judge.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString(callSuper=true)
public class LocationAnalysisDetailRequest extends JudgeDetailRequest {
    @ApiModelProperty(value = "对象值", required = true, example = "C4:0B:CB:E3:8C:C4")
    private String objectValue;
    @ApiModelProperty(value = "开始时间", required = true, example = "1502858803000")
    private Long beginDate;
    @ApiModelProperty(value = "结束时间", required = true, example = "1502858803000")
    private Long endDate;
    @ApiModelProperty(value = "密度等级", required = true, example = "1")
    private Integer density;
    @ApiModelProperty(value = "聚点网格")
    private Long gridCode;
    @ApiModelProperty(value = "区域")
    private List<Area> areaList;
}
