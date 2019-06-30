package com.jiayi.platform.repo.minerepo.vo;

import java.util.List;

public class MonitorRepoParam {
    private Long repoId;
    private String repoName;
    private List<String> objTypes;
    private Integer repoType;

    public Long getRepoId() {
        return repoId;
    }

    public void setRepoId(Long repoId) {
        this.repoId = repoId;
    }

    public String getRepoName() {
        return repoName;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    public List<String> getObjTypes() {
        return objTypes;
    }

    public void setObjTypes(List<String> objTypes) {
        this.objTypes = objTypes;
    }

    public Integer getRepoType() {
        return repoType;
    }

    public void setRepoType(Integer repoType) {
        this.repoType = repoType;
    }
}
