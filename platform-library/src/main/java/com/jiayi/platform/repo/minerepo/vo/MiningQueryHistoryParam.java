package com.jiayi.platform.repo.minerepo.vo;

import java.util.List;

public class MiningQueryHistoryParam {
    private Long repoId;
    private String repoName;
    private List<String> objTypes;
    private Long startTime;
    private Long endTime;
    private Integer repoType;

    public Long getRepoId() {
        return repoId;
    }

    public void setRepoId(Long repoId) {
        this.repoId = repoId;
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

    public String getRepoName() {
        return repoName;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    public Integer getRepoType() {
        return repoType;
    }

    public void setRepoType(Integer repoType) {
        this.repoType = repoType;
    }
}
