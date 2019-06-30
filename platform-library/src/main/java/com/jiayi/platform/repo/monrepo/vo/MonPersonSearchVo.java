package com.jiayi.platform.repo.monrepo.vo;

import javax.validation.constraints.NotNull;

public class MonPersonSearchVo {
    @NotNull
    private Long repoId;
    private String name = "";  //人员名称
    private String objectValue = ""; //MAC CD:68:AF:32:56:BD
    private Integer page;
    private Integer size;

    public Long getRepoId() {
        return repoId;
    }
    public void setRepoId(Long repoId) {
        this.repoId = repoId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        if (name == null) name = "";
        this.name = name;
    }

    public String getObjectValue() {
        return objectValue;
    }
    public void setObjectValue(String objectValue) {
        if (objectValue == null) objectValue = "";
        this.objectValue = objectValue;
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
