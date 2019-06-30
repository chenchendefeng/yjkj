package com.jiayi.platform.judge.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString(callSuper=true)
public class LocationAnalysisRequest extends JudgeRequest {
    @ApiModelProperty(value = "对象值", required = true, example = "A1:B2:C3:D4:E5:F6")
    private String objectValue;
    @ApiModelProperty(value = "开始时间", required = true, example = "1541001600000")
    private Long beginDate;
    @ApiModelProperty(value = "结束时间", required = true, example = "1543507200000")
    private Long endDate;
    @ApiModelProperty(value = "统计方式（0:天 / 1:周 / 2:月）", required = true, example = "0")
    private Integer countTime;
    @ApiModelProperty(value = "密度等级", required = true, example = "1")
    private Integer density;
    @ApiModelProperty(value = "区域")
    private List<Area> areaList;
}
