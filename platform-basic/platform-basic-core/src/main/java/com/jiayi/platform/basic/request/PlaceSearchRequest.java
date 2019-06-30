package com.jiayi.platform.basic.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PlaceSearchRequest {

    private Integer page = 0;
    private Integer size = 10;
    @ApiModelProperty(value = "场所名称或地址", example = "测试A1")
    private String name;
    @ApiModelProperty(value = "场所编码")
    private String code;
    @ApiModelProperty(value = "所属区县id", example = "440306")
    private String districtId;
    @ApiModelProperty(value = "所属部门id", example = "5")
    private Integer departmentId;
//    @ApiModelProperty(value = "数据源id", example = "1")
//    private Long srcId;
//    @ApiModelProperty(value = "供应商id", example = "1")
//    private Long vendorId;
    @ApiModelProperty(value = "安装时间（开始）", example = "1554792483000")
    private String beginDate;
    @ApiModelProperty(value = "安装时间（结束）", example = "1554792663000")
    private String endDate;
    @ApiModelProperty(value = "是否包含空部门的场所")
    private boolean ignoreNull;
}
