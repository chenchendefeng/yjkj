package com.jiayi.platform.alarm.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class SuspectsRequest {

    @NotNull(message = "请先选择案件")
    @ApiModelProperty(value = "案件id", example = "1")
    private Integer caseId;
    @NotBlank(message = "姓名不能为空")
    @ApiModelProperty(value = "姓名", example = "测试姓名")
    private String name;
    @ApiModelProperty(value = "手机号码")
    private String mobile;
    @ApiModelProperty(value = "身份证号")
    private String identitycard;
    @ApiModelProperty(value = "地址")
    private String address;
    @ApiModelProperty(value = "备注")
    private String remark;
    @NotNull(message = "录入人不能为空")
    @ApiModelProperty(value = "录入人id", example = "1")
    private long userId;
    @ApiModelProperty(value = "布控状态：0未布控，1布控", example = "1")
    private int status;
    @ApiModelProperty(value = "人员设备信息")
    private List<DeviceInfoRequest> deviceInfoData = new ArrayList<>();
}
