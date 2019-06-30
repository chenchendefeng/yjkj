package com.jiayi.platform.judge.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString(callSuper=true)
public class MultiTrackCollisionRequest extends JudgeRequest {
    @ApiModelProperty(value = "对象值", required = true, example = "C4:0B:CB:E3:8C:C4")
    private String objectValue;
    @ApiModelProperty(value = "开始时间", required = true, example = "1502858803000")
    private Long beginDate;
    @ApiModelProperty(value = "结束时间", required = true, example = "1502858803000")
    private Long endDate;
    @ApiModelProperty(value = "伴随对象类型（结果集的对象类型）", required = true, example = "imsi")
    private String followObjectTypeName;
    @ApiModelProperty(value = "匹配系数", required = true, example = "2")
    private Integer matchCount;
    @ApiModelProperty(value = "伴随时间", required = true, example = "120")
    private Integer timeOffset;
    @ApiModelProperty(value = "伴随距离", required = true, example = "200")
    private Integer distance;
    @ApiModelProperty(value = "筛选对象值列表")
    private List<String> objectValueList;
}
