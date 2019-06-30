package com.jiayi.platform.judge.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString(callSuper=true)
public class FollowCollisionRequest extends JudgeRequest {
    @ApiModelProperty(value = "伴随对象值", required = true, example = "A1:B2:C3:D4:E5:F6")
    private String objectValue;
    @ApiModelProperty(value = "开始时间", required = true, example = "1546567212000")
    private Long beginDate;
    @ApiModelProperty(value = "结束时间", required = true, example = "1546570919000")
    private Long endDate;
    @ApiModelProperty(value = "匹配伴随数", required = true, example = "2")
    private Integer matchCount;
    @ApiModelProperty(value = "伴随区域")
    private List<Area> areaList;
    @ApiModelProperty(value = "伴随时间（秒）", required = true, example = "120")
    private Integer timeOffset;
    @ApiModelProperty(value = "筛选对象值列表")
    private List<String> objectValueList;
}
