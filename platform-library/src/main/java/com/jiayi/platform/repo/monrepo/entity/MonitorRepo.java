package com.jiayi.platform.repo.monrepo.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.util.List;

public class MonitorRepo {
    private Long uid;
    private String repoName;
    private Integer repoType;
    private Integer departmentId;
    private String repoDesc;
//    @ManyToOne
//    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
//    private User user;
    private Integer userId;
    private Long createAt;
    private Long updateAt;
    private Integer permissionType;
    private List<Integer> permissionUserIds;
    private String permissionUserIdStr;

    @JsonSerialize(using = ToStringSerializer.class)
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

    public String getRepoDesc() {
        return repoDesc;
    }
    public void setRepoDesc(String repoDesc) {
        this.repoDesc = repoDesc;
    }

    public Integer getUserId() {
        return userId;
    }
    public void setUserId(Integer userId) {
        this.userId = userId;
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

    public Integer getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(Integer permissionType) {
        this.permissionType = permissionType;
    }

    public List<Integer> getPermissionUserIds() {
        return permissionUserIds;
    }

    public void setPermissionUserIds(List<Integer> permissionUserIds) {
        this.permissionUserIds = permissionUserIds;
    }

    public String getPermissionUserIdStr() {
        return permissionUserIdStr;
    }

    public void setPermissionUserIdStr(String permissionUserIdStr) {
        this.permissionUserIdStr = permissionUserIdStr;
    }
}
