package com.jiayi.platform.alarm.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
public class AlarmDistrictRequest {

    @NotBlank(message = "布控名称不能为空")
    @ApiModelProperty(value = "布控名称", required = true)
    private String name;
    @NotNull(message = "策略类型不能为空")
    @ApiModelProperty(value = "策略类型：2触碰、3入圈、4出圈、6聚集、5消失", example = "2", required = true)
    private Integer type;
    @ApiModelProperty(value = "布控区域")
    private MapRegion mapRegion;
    @ApiModelProperty(value = "布控详情")
    private Map<String, String> exInfo;
    @NotNull(message = "布控对象归属地不能为空")
    @ApiModelProperty(value = "布控对象归属地", required = true)
    private Integer district;
    @NotEmpty(message = "布控对象类型不能为空")
    @ApiModelProperty(value = "布控对象类型", example = "[IMSI,车牌,手机号]", required = true)
    private List<String> objTypes;
    @ApiModelProperty(value = "开始时间格式yyyy-MM-dd", example = "2019-01-17")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date startTime;
    @ApiModelProperty(value = "结束时间格式yyyy-MM-dd", example = "2019-01-17")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date endTime;
    @ApiModelProperty(value = "是否长期,0否 1是", example = "0")
    private Integer beLongValid;
    @NotEmpty(message = "消息推送人员不能为空")
    @ApiModelProperty(value = "消息推送人员id", example = "[1,2,3]")
    private List<Long> userIds;
    @ApiModelProperty(value = "备注")
    private String remark;
}
