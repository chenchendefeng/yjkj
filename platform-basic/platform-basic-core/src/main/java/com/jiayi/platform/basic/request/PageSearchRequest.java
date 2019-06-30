package com.jiayi.platform.basic.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PageSearchRequest {

    @ApiModelProperty(value = "分页0开始", example = "0")
    private Integer page = 0;
    @ApiModelProperty(value = "每页数量", example = "10")
    private Integer size = 10;
}
