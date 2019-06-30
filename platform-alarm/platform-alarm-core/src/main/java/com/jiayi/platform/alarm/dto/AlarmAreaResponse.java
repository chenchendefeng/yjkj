package com.jiayi.platform.alarm.dto;

public class AlarmAreaResponse {
    private Long id;
    private String name;
    private AlarmRegion mapRegion;
    private Double area;
    private String placeId;
    private Long startTime;
    private Long endTime;
    private Long warningNum;
    private Long maxNum;
    private Long period;
    private Double factor;
    private Integer enable;
    //父层下的子区域数
    private Long subAreaNum;
    //父层下的设备数量
    private Integer deviceNum;

    public Long getSubAreaNum() {
        return subAreaNum;
    }

    public void setSubAreaNum(Long subAreaNum) {
        this.subAreaNum = subAreaNum;
    }

    public Integer getDeviceNum() {
        return deviceNum;
    }

    public void setDeviceNum(Integer deviceNum) {
        this.deviceNum = deviceNum;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Double getArea() {
        return area;
    }

    public void setArea(Double area) {
        this.area = area;
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

    @Override
    public String toString() {
        return "AlarmAreaResponse [id=" + id + ", name=" + name + ", mapRegion=" + mapRegion + ", area=" + area
                + ", placeId=" + placeId + ", startTime=" + startTime + ", endTime=" + endTime + ", warningNum="
                + warningNum + ", maxNum=" + maxNum + ", period=" + period + ", factor=" + factor + ", enable=" + enable
                + "]";
    }
}
