package com.jiayi.platform.judge.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class JudgeDetailRequest {
    @ApiModelProperty(value = "对象类型", required = true, example = "mac")
    protected String objectTypeName;
}
