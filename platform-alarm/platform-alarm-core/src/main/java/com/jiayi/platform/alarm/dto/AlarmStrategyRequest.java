package com.jiayi.platform.alarm.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

@Getter
@Setter
@ToString
public class AlarmStrategyRequest {

    @NotNull(message = "请先选择案件")
    @ApiModelProperty(value = "案件id", example = "1")
    private Integer caseId;
    @NotBlank(message = "策略名称不能为空")
    @ApiModelProperty(value = "策略名称", example = "测试触碰名称")
    private String name;
    @NotNull(message = "策略类型不能为空")
    @ApiModelProperty(value = "策略类型,2触碰 3入圈 4出圈 5消失 6聚集", example = "2")
    private Integer type;
    @ApiModelProperty(value = "区域")
    private MapRegion mapRegion;
    @ApiModelProperty(value = "策略详情,中文对应参数名(消失时间:timeSE,告警人数:nnt,聚集类型:typeS(指定区域，动态聚集),统计时长:Period,聚集距离:distance)")
    private Map<String, String> exInfo;
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "用户id")
    private Long userId;
    @ApiModelProperty(value = "策略启用状态、0禁用、1启用", example = "0")
    private Integer status;
}
