package com.jiayi.platform.basic.dto;

public class CountListDto {
    private Integer deviceId;
    private Long earliestTime; 
    private Long latestTime;
    private Long recordDate;
    private String trackTypeName;
    private Long LimitCount;
    
    public CountListDto(Integer deviceId, Long earliestTime, Long latestTime, Long recordDate, String trackTypeName,
            Long limitCount) {
        super();
        this.deviceId = deviceId;
        this.earliestTime = earliestTime;
        this.latestTime = latestTime;
        this.recordDate = recordDate;
        this.trackTypeName = trackTypeName;
        this.LimitCount = limitCount;
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public Long getEarliestTime() {
        return earliestTime;
    }
    public void setEarliestTime(Long earliestTime) {
        this.earliestTime = earliestTime;
    }
    public Long getLatestTime() {
        return latestTime;
    }
    public void setLatestTime(Long latestTime) {
        this.latestTime = latestTime;
    }
    public Long getRecordDate() {
        return recordDate;
    }
    public void setRecordDate(Long recordDate) {
        this.recordDate = recordDate;
    }
    public String getTrackTypeName() {
        return trackTypeName;
    }
    public void setTrackTypeName(String trackTypeName) {
        this.trackTypeName = trackTypeName;
    }
    public Long getLimitCount() {
        return LimitCount;
    }
    public void setLimitCount(Long limitCount) {
        LimitCount = limitCount;
    }
    
}
