package com.jiayi.platform.repo.minerepo.vo;

import io.swagger.annotations.ApiModelProperty;

public class RepoResponse {

    @ApiModelProperty(value = "库ID")
    private Long uid;
    @ApiModelProperty(value = "库名称")
    private String repoName;

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getRepoName() {
        return repoName;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }
}
