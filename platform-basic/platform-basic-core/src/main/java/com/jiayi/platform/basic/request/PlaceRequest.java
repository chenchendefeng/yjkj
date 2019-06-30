package com.jiayi.platform.basic.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
public class PlaceRequest {
    @NotNull(message = "场所名称不能为空")
    @ApiModelProperty(value = "场所名称", example = "测试场所")
    private String name;
    @NotEmpty(message = "场所类型不能为空")
    @ApiModelProperty(value = "场所类型", example = "[1]")
    private List<Long> placeTags;
    @NotNull(message = "经营性质不能为空")
    @ApiModelProperty(value = "经营性质:1经营，2非经营，3围栏采集，4其他", example = "1")
    private Integer placeType;
    @ApiModelProperty(value = "安装时间")
    private Date installAt;
    @NotNull(message = "场所区县不能为空")
    @ApiModelProperty(value = "区县", example = "440306")
    private String district;
    @ApiModelProperty(value = "所属部门", example = "1")
    private Integer departmentId;
//    @NotNull(message = "数据源不能为空")
//    @ApiModelProperty(value = "数据源id", example = "1")
//    private Long src;
    @ApiModelProperty(value = "场所编码", example = "44128439001172")
    private String code;
    @NotBlank(message = "详细地址不能为空")
    @ApiModelProperty(value = "详细地址", example = "XX街道")
    private String address;
    @ApiModelProperty(value = "场所经度", example = "112.818206")
    private Double longitude;
    @ApiModelProperty(value = "场所纬度", example = "23.304879")
    private Double latitude;
    @ApiModelProperty(value = "安装人姓名", example = "王虎")
    private String inforMan;
    @ApiModelProperty(value = "安装人电话", example = "13826549852")
    private String inforManTel;
    @ApiModelProperty(value = "法人姓名", example = "张子安")
    private String principal;
    @ApiModelProperty(value = "法人联系方式", example = "0758-7762342")
    private String principalTel;
    @ApiModelProperty(value = "法人证件类型", example = "1021111")
    private String principalCertType;
    @ApiModelProperty(value = "法人证件号码", example = "44122619890228007x")
    private String principalCertCode;
    @ApiModelProperty(value = "营业开始时间", example = "2019-04-18 00:00:00")
    private String openAt;
    @ApiModelProperty(value = "营业结束时间", example = "2019-04-19 00:00:00")
    private String closeAt;
    @ApiModelProperty(value = "营业状态", example = "1 ")
    private Integer status;
    @ApiModelProperty(value = "网络认证账号或固定IP地址")
    private String authAccount;
    @ApiModelProperty(value = "接入服务商代码", example = "01")
    private String producerCode;
    @ApiModelProperty(value = "接入方式", example = "03")
    private String netType;
    @ApiModelProperty(value = "外网IP")
    private String exitIp;
    @ApiModelProperty(value = "场所标签code")
    private List<String> placeLabels;
    @ApiModelProperty(value = "删除部门")
    private boolean deleteDept;
    @ApiModelProperty(value="厂商组织机构编码")
    private String terminalFactoryOrgCode;

    //--------以下为大数据平台参数
    private String phone;
    private String contactName;
    private String contactPhone;
    private Long contactCertType;
    private String contactCertCode;
    private String province;
    private String city;
}
