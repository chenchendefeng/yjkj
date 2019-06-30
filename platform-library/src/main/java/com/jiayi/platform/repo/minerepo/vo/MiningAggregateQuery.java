package com.jiayi.platform.repo.minerepo.vo;

import java.util.List;

public class MiningAggregateQuery {
    private List<String> objTypes;
    private Long startTime;
    private Long endTime;
    private String tableName;
    private String objTypeFieldName;
    private String startTimeFieldName;
    private String endTimeFieldName;

    public List<String> getObjTypes() {
        return objTypes;
    }

    public void setObjTypes(List<String> objTypes) {
        this.objTypes = objTypes;
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

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getObjTypeFieldName() {
        return objTypeFieldName;
    }

    public void setObjTypeFieldName(String objTypeFieldName) {
        this.objTypeFieldName = objTypeFieldName;
    }

    public String getStartTimeFieldName() {
        return startTimeFieldName;
    }

    public void setStartTimeFieldName(String startTimeFieldName) {
        this.startTimeFieldName = startTimeFieldName;
    }

    public String getEndTimeFieldName() {
        return endTimeFieldName;
    }

    public void setEndTimeFieldName(String endTimeFieldName) {
        this.endTimeFieldName = endTimeFieldName;
    }
}
