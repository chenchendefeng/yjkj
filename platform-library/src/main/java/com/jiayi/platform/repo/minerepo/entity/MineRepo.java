package com.jiayi.platform.repo.minerepo.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

public class MineRepo {
    private Long id;
    private String repoName;
    private Integer repoType;
    private Integer departmentId;
    private Integer recordNum;
    private String repoDesc;
    private String detailTableDesc;
    private Long createAt;
    private Long updateAt;

    @JsonSerialize(using = ToStringSerializer.class)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public Integer getRecordNum() {
        return recordNum;
    }

    public void setRecordNum(Integer recordNum) {
        this.recordNum = recordNum;
    }

    public String getRepoDesc() {
        return repoDesc;
    }

    public void setRepoDesc(String repoDesc) {
        this.repoDesc = repoDesc;
    }

    public String getDetailTableDesc() {
        return detailTableDesc;
    }

    public void setDetailTableDesc(String detailTableDesc) {
        this.detailTableDesc = detailTableDesc;
    }

    public Long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Long createAt) {
        this.createAt = createAt;
    }

    public Long getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Long updateAt) {
        this.updateAt = updateAt;
    }
}
