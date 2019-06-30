package com.jiayi.platform.alarm.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class AlarmDistrictMsgSearchVo extends PageSearchVo{

    @NotNull(message = "用户id不能为空")
    @ApiModelProperty(value = "用户id",required = true)
    private Integer userId;
    @ApiModelProperty(value = "布控名称")
    private String name;
    @ApiModelProperty(value = "策略类型：0全部、2触碰、3入圈、4出圈、5消失、6指定区域聚集、7动态聚集")
    private Integer type;
    @ApiModelProperty(value = "布控对象归属地")
    private Integer district;
    @ApiModelProperty(value = "特征类型", example = "车牌")
    private List<String> objType;
    @ApiModelProperty(value = "开始时间")
    private long startTime;
    @ApiModelProperty(value = "结束时间")
    private long endTime;
    @ApiModelProperty(value = "状态:0未读 1已读")
    private Integer status;
}
