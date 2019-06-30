package com.jiayi.platform.repo.minerepo.vo;

import java.util.List;

public class MiningRepoTwoCollisionRequest {
    private Integer caseId;
    private String resultName;
    private List<String> objTypes;
    private Long startTime;
    private Long endTime;
    private Long operateUserId;

    public Integer getCaseId() {
        return caseId;
    }

    public void setCaseId(Integer caseId) {
        this.caseId = caseId;
    }

    public String getResultName() {
        return resultName;
    }

    public void setResultName(String resultName) {
        this.resultName = resultName;
    }

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

    public Long getOperateUserId() {
        return operateUserId;
    }

    public void setOperateUserId(Long operateUserId) {
        this.operateUserId = operateUserId;
    }
}
