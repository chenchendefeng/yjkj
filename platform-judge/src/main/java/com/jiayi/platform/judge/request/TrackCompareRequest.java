package com.jiayi.platform.judge.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper=true)
public class TrackCompareRequest extends JudgeRequest {
    @ApiModelProperty(value = "开始时间", required = true, example = "1558296000000")
    private Long beginDate;
    @ApiModelProperty(value = "结束时间", required = true, example = "1558299600000")
    private Long endDate;
    @ApiModelProperty(value = "源对像类型（mac、imsi、imei、carno）", required = true, example = "mac")
    private String srcObjectTypeName;
    @ApiModelProperty(value = "源对像值", required = true, example = "7823FE691B0D")
    private String srcObjectValue;
    @ApiModelProperty(value = "目标对像类型（mac、imsi、imei、carno）", required = true, example = "mac")
    private String desObjectTypeName;
    @ApiModelProperty(value = "目标对像值", required = true, example = "A8B359E7CD14")
    private String desObjectValue;
    @ApiModelProperty(value = "时间配置周期范围，单位分钟", required = true, example = "5")
    private Integer duration;
    @ApiModelProperty(value = "是否全部匹配（0：否，1:是）", required = true, example = "1")
    private Integer match;
}
