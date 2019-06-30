package com.jiayi.platform.alarm.dto;

import io.swagger.annotations.ApiModelProperty;

import java.util.Arrays;

public class AlarmBatchModRequest extends  AlarmRequest{
    @ApiModelProperty(value = "区域id数组", example = "[1,2]")
    private Long[] areaIds;

    public Long[] getAreaIds() {
        return areaIds;
    }

    public void setAreaIds(Long[] areaIds) {
        this.areaIds = areaIds;
    }

    @Override
    public String toString() {
        return "AlarmBatchModRequest{" +
                "areaIds=" + Arrays.toString(areaIds) +
                '}';
    }
}
