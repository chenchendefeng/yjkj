package com.jiayi.platform.repo.monrepo.vo;

import javax.validation.constraints.NotNull;

public class MonObjectSearchVo {
    @NotNull
    private Long repoId;
    private String objectName = "";
    private Integer objectType = -1;
    private String objectValue = "";
    private String name = "";
    private Integer page;
    private Integer size;

    public Long getRepoId() {
        return repoId;
    }
    public void setRepoId(Long repoId) {
        this.repoId = repoId;
    }

    public String getObjectName() {
        return objectName;
    }
    public void setObjectName(String objectName) {
        if (objectName == null) objectName = "";
        this.objectName = objectName;
    }

    public Integer getObjectType() {
        return objectType;
    }
    public void setObjectType(Integer objectType) {
        if (objectType == null) objectType = -1;
        this.objectType = objectType;
    }

    public String getObjectValue() {
        return objectValue;
    }
    public void setObjectValue(String objectValue) {
        if (objectValue == null) objectValue = "";
        this.objectValue = objectValue;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        if (name == null) name = "";
        this.name = name;
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
