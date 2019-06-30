package com.jiayi.platform.alarm.dto;

import io.swagger.annotations.ApiModelProperty;

public class AlarmRequest {
    @ApiModelProperty(value = "预警人数", example = "20000")
    private Long warningNum;
    @ApiModelProperty(value = "人流上限", example = "50000")
    private Long maxNum;
    @ApiModelProperty(value = "统计周期", example = "10")
    private Long period;
    @ApiModelProperty(value = "倍率系数", example = "1.5")
    private Double factor;
    @ApiModelProperty(value = "是否启用", example = "1")
    private Integer enable;


    public Long getWarningNum() {
        return warningNum;
    }

    public void setWarningNum(Long warningNum) {
        this.warningNum = warningNum;
    }

    public Long getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(Long maxNum) {
        this.maxNum = maxNum;
    }

    public Long getPeriod() {
        return period;
    }

    public void setPeriod(Long period) {
        this.period = period;
    }

    public Double getFactor() {
        return factor;
    }

    public void setFactor(Double factor) {
        this.factor = factor;
    }

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    @Override
    public String toString() {
        return "AlarmConfRequest{" +
                ", warningNum=" + warningNum +
                ", maxNum=" + maxNum +
                ", period=" + period +
                ", factor=" + factor +
                ", enable=" + enable +
                '}';
    }
}
