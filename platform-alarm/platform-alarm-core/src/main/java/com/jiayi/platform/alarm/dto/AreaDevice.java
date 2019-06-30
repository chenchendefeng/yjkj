package com.jiayi.platform.alarm.dto;

import java.util.List;

public class AreaDevice {
    private Long areaId;
    private List<String> deviceIds;

    public AreaDevice(Long areaId, List<String> deviceIds) {
        super();
        this.areaId = areaId;
        this.deviceIds = deviceIds;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    public List<String> getDeviceIds() {
        return deviceIds;
    }

    public void setDeviceIds(List<String> deviceIds) {
        this.deviceIds = deviceIds;
    }

    @Override
    public String toString() {
        return "AreaDevice [areaId=" + areaId + ", deviceIds=" + deviceIds + "]";
    }
}
