package com.jiayi.platform.judge.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TrackRequest {
    @ApiModelProperty(value = "对像类型（mac、imsi、imei、carno）", required = true, example = "mac")
    private String objectTypeName;
    @ApiModelProperty(value = "对象值", required = true, example = "C4:0B:CB:E3:8C:C4")
    private String objectValue;
    @ApiModelProperty(value = "开始时间", required = true, example = "1502858803000")
    private Long beginDate;
    @ApiModelProperty(value = "结束时间", required = true, example = "1502858803000")
    private Long endDate;
}
