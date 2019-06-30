package com.jiayi.platform.judge.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString(callSuper=true)
public class AreaCollisionRequest extends JudgeRequest {
    @ApiModelProperty(value = "匹配系数", required = true, example = "3")
    private Integer matchCount;
    @ApiModelProperty(value = "条件设置列表")
    private List<AreaCondition> conditionList;
    @ApiModelProperty(value = "对象值列表")
    private List<String> objectValueList;
}
