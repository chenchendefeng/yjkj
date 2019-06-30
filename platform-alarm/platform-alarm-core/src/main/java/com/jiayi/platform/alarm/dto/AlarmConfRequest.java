package com.jiayi.platform.alarm.dto;

import io.swagger.annotations.ApiModelProperty;

public class AlarmConfRequest extends  AlarmRequest{
    @ApiModelProperty(value = "开始时间", example = "1530374400000")
    private Long startTime;
    @ApiModelProperty(value = "结束时间", example = "1535731200000")
    private Long endTime;


    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }



    @Override
    public String toString() {
        return "AlarmConfRequest{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
