package com.jiayi.platform.basic.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class DeviceStatSearchRequest {
	@ApiModelProperty(value = "区id", example = "403102")
	private String district;
	@ApiModelProperty(value = "部门id列表", example = "[1,2]")
	private List<Integer> departmentIds;
	@ApiModelProperty(value = "供应商id", example = "3")
	private Integer vendorId;
	@ApiModelProperty(value = "开始时间", example = "2018-09-01 00:00:00")
	private String beginDate;
	@ApiModelProperty(value = "结束时间", example = "2018-09-12 00:00:00")
	private String endDate;
	@ApiModelProperty(value = "场所类型id", example = "3")
	private Long placeTagId;
	@ApiModelProperty(value = "设备子类型", example = "1")
	private Integer deviceType;
	@ApiModelProperty(value = "设备状态：0离线，1在线；", example = "0")
	private Integer isOnline=-1;
//	@ApiModelProperty(value = "合格状态：0不合格，1合格，2待定", example = "0")
//	private Integer isQualified=-1;
    private Integer page;
	private Integer size;

}
