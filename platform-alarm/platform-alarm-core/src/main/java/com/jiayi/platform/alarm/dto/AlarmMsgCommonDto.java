package com.jiayi.platform.alarm.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AlarmMsgCommonDto {

    private Long id;
    @ApiModelProperty(value = "策略类型")
    private String strategyType;
    @ApiModelProperty(value = "区域信息")
    private String mapRegion;
    @ApiModelProperty(value = "扩展信息")
    private String exInfo;
    @ApiModelProperty(value = "状态")
    private String status;

    private String assembleGroupId;
    @ApiModelProperty(value = "预警对象信息")
    private List<AlarmSuspectInfo> alarmSuspects = new ArrayList<>();

    public AlarmMsgCommonDto(Long id, String strategyType, String status, String assembleGroupId, String mapRegion, String exInfo) {
        this.id = id;
        this.strategyType = strategyType;
        this.status = status;
        this.assembleGroupId = assembleGroupId;
        this.mapRegion = mapRegion;
        this.exInfo = exInfo;
    }
}
