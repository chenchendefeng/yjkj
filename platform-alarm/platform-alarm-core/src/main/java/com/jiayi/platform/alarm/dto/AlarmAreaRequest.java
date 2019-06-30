package com.jiayi.platform.alarm.dto;

import io.swagger.annotations.ApiModelProperty;

public class AlarmAreaRequest {
    @ApiModelProperty(value = "区域名称", example = "城中派出所门口")
    private String name;
    @ApiModelProperty(value = "布控区域")
    private AlarmRegion mapRegion;
    @ApiModelProperty(value = "场所id", example = "1")
    private String placeId;
    @ApiModelProperty(value = "开始时间", example = "1530374400000")
    private Long startTime;
    @ApiModelProperty(value = "结束时间", example = "1535731200000")
    private Long endTime;
    @ApiModelProperty(value = "预警人数", example = "20000")
    private Long warningNum;
    @ApiModelProperty(value = "人流上限", example = "50000")
    private Long maxNum;
    @ApiModelProperty(value = "统计类型", example = "1：MAC;2:IMSI;3:CARNO")
    private Long period;
    @ApiModelProperty(value = "倍率系数", example = "1.5")
    private Double factor;
    @ApiModelProperty(value = "是否启用", example = "1")
    private Integer enable;

    @ApiModelProperty(value = "父区域id,顶层根区域传0，子区域传的父区域id", example = "1")
    private Long parentAreaId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AlarmRegion getMapRegion() {
        return mapRegion;
    }

    public void setMapRegion(AlarmRegion mapRegion) {
        this.mapRegion = mapRegion;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

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

    public Long getParentAreaId() {
        return parentAreaId;
    }

    public void setParentAreaId(Long parentAreaId) {
        this.parentAreaId = parentAreaId;
    }

    @Override
    public String toString() {
        return "AlarmAreaRequest{" +
                "name='" + name + '\'' +
                ", mapRegion=" + mapRegion +
                ", placeId='" + placeId + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", warningNum=" + warningNum +
                ", maxNum=" + maxNum +
                ", period=" + period +
                ", factor=" + factor +
                ", enable=" + enable +
                ", parentAreaId=" + parentAreaId +
                '}';
    }
}
