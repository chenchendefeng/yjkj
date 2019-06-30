package com.jiayi.platform.repo.monrepo.vo;

public class MonRepoSearchVo {
    private String repoName="";
    private Integer repoType=-1;
    private Integer departmentId=-1;
    private Integer page = 0;
    private Integer size = 10;

    public String getRepoName() {
        return repoName;
    }
    public void setRepoName(String repoName) {
        if (repoName == null) repoName = "";
        this.repoName = repoName;
    }

    public Integer getRepoType() {
        return repoType;
    }
    public void setRepoType(Integer repoType) {
        if (repoType == null) repoType = -1;
        this.repoType = repoType;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }
    public void setDepartmentId(Integer departmentId) {
        if (departmentId == null) departmentId = -1;
        this.departmentId = departmentId;
    }

    public Integer getPage() {
        return page;
    }
    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }
    public void setSize(Integer size) {
        this.size = size;
    }


}
