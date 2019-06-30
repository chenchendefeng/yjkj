package com.jiayi.platform.alarm.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AlarmCaseMsgDto extends AlarmMsgCommonDto{

    @ApiModelProperty(value = "案件id")
    private Long caseId;
    @ApiModelProperty(value = "案件名称")
    private String caseName;

    public AlarmCaseMsgDto(Long id, Long caseId, String caseName, String strategyType, String status, String assembleGroupId, String mapRegion, String exInfo) {
        super(id, strategyType, status, assembleGroupId, mapRegion, exInfo);
        this.caseId = caseId;
        this.caseName = caseName;
    }
}
