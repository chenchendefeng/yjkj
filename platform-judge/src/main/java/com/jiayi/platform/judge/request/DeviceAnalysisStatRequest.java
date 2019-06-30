package com.jiayi.platform.judge.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper=true)
public class DeviceAnalysisStatRequest extends DeviceAnalysisDetailRequest {
    @ApiModelProperty(value = "统计类型", required = true, example = "HOUR")
    private String statType;
}
