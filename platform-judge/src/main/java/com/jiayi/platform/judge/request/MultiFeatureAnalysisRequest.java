package com.jiayi.platform.judge.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString(callSuper=true)
public class MultiFeatureAnalysisRequest extends JudgeRequest {
    @ApiModelProperty(value = "对象值", required = true, example = "A1:B2:C3:D4:E5:F6")
    private String objectValue;
    @ApiModelProperty(value = "开始时间", required = true, example = "1541001600000")
    private Long beginDate;
    @ApiModelProperty(value = "结束时间", required = true, example = "1543507200000")
    private Long endDate;
    @ApiModelProperty(value = "匹配天数", required = true, example = "2")
    private Integer matchDays;
    @ApiModelProperty(value = "匹配系数", required = true, example = "0.5")
    private Double matchCount;
    @ApiModelProperty(value = "结果类型", required = true)
    private List<String> objectTypeNameList;
}
