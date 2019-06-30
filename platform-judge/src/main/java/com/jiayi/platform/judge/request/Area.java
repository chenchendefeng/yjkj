package com.jiayi.platform.judge.request;

import com.jiayi.platform.common.bo.Location;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author : weichengke
 * @date : 2019-04-20 14:08
 */
@Setter
@Getter
@ToString
public class Area {
    @ApiModelProperty(value = "区域类型", required = true, example = "rect", allowableValues = "[rect,line]")
    private String type;
    @ApiModelProperty(value = "用户给当前区域取的名称", required = true, example = "A",notes = "同一个请求中名称是唯一的")
    private String name;
    @ApiModelProperty(value = "构成形状的点的集合", required = true)
    private List<Location> points;
    @ApiModelProperty(value = "当前区域id（时间戳格式）", required = true)
    private Long id;
    @ApiModelProperty(value = "是否启用该区域", required = true)
    private Boolean used = true;
}
