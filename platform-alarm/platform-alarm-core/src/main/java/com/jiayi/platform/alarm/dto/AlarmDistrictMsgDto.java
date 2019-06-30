package com.jiayi.platform.alarm.dto;

import com.jiayi.platform.alarm.util.CityCodeUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlarmDistrictMsgDto extends AlarmMsgCommonDto{

    @ApiModelProperty(value = "布控名称")
    private String alarmName;
    @ApiModelProperty(value = "布控对象归属地id")
    private Integer district;

    public AlarmDistrictMsgDto(Long id, String alarmName, Integer district, String strategyType, String status, String assembleGroupId, String mapRegion, String exInfo) {
        super(id, strategyType, status, assembleGroupId, mapRegion, exInfo);
        this.alarmName = alarmName;
        this.district = district;
    }

    public String getDistrictStr(){
        if(district != null)
            return CityCodeUtil.getCityAreaName(district);
        return null;
    }
}
