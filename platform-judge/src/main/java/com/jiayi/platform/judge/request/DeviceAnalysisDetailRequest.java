package com.jiayi.platform.judge.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString(callSuper=true)
public class DeviceAnalysisDetailRequest extends JudgeDetailRequest {
    @ApiModelProperty(value = "对象id", required = true)
    private String objectId;
    @ApiModelProperty(value = "开始时间", required = true, example = "1502858803000")
    private Long beginDate;
    @ApiModelProperty(value = "结束时间", required = true, example = "1502858803000")
    private Long endDate;
    @ApiModelProperty(value = "区域")
    private List<Area> areaList;
}
