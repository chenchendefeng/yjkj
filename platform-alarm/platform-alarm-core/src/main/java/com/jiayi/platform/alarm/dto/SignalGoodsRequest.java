package com.jiayi.platform.alarm.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class SignalGoodsRequest {

    @NotNull(message = "请先选择案件")
    @ApiModelProperty(value = "案件Id", example = "1")
    private Integer caseId;
    @NotBlank(message = "物品名称不能为空")
    @Length(max = 200, message = "物品名称长度不能大于200")
    @ApiModelProperty(value = "物品名称", example = "测试物品名称")
    private String name;
    @NotNull(message = "实体类型不能为空")
    @ApiModelProperty(value = "实体类型:1mac 2carno 3imei 4imsi", example = "1")
    private Integer objType;
    @NotBlank(message = "实体值不能为空")
    @Length(max = 50, message = "实体值长度不能大于50")
    @ApiModelProperty(value = "实体值", example = "DC55835A9775")
    private String objValue;
    @Length(max = 50, message = "姓名长度不能大于50")
    @ApiModelProperty(value = "姓名")
    private String userName;
    //	@Pattern(regexp="^1[3|5|8]{1}[0-9]{9}$",message="手机号码不正确")
    @ApiModelProperty(value = "手机号码")
    private String mobile;
    //	@Pattern(regexp="(^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$)|(^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{2}$)",message="身份证号码不正确")
    @ApiModelProperty(value = "身份证号码")
    private String identitycard;
    @Length(max = 200, message = "家庭住址长度不能大于200")
    @ApiModelProperty(value = "家庭住址")
    private String address;
    @Length(max = 255, message = "备注长度不能大于255")
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "布控状态:0未布控、1布控")
    private Integer status;

}
