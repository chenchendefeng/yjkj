package com.jiayi.platform.repo.monrepo.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import com.jiayi.platform.security.core.entity.Department;
import com.jiayi.platform.security.core.entity.UserBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MonRepoDto {
    private Long uid;
    private String repoName;
    private Integer repoType;
    private Integer departmentId;
    private Department department;
    private String repoDesc;
    private Integer totalPerson;
    private Integer totalObject;
    private Integer totalAll;
    private Long createAt;
    private Long updateAt;
    private Long userId;
    private String userName;
    private Integer permissionType;
    private String permissionUserIdStr;
    private List<UserBean> permissionUserIds;

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

    public Department getDepartment() {
        return department;
    }
    public void setDepartment(Department department) {
        this.department = department;
    }

    public String getRepoDesc() {
        return repoDesc;
    }
    public void setRepoDesc(String repoDesc) {
        this.repoDesc = repoDesc;
    }

    public Integer getTotalPerson() {
        return totalPerson;
    }
    public void setTotalPerson(Integer totalPerson) {
        this.totalPerson = totalPerson;
    }

    public Integer getTotalObject() {
        return totalObject;
    }
    public void setTotalObject(Integer totalObject) {
        this.totalObject = totalObject;
    }

    public Integer getTotalAll() {
        return totalAll;
    }
    public void setTotalAll(Integer totalAll) {
        this.totalAll = totalAll;
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

    public Long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(Integer permissionType) {
        this.permissionType = permissionType;
    }

    public List<UserBean> getPermissionUserIds() {
        return permissionUserIds;
    }

    public void setPermissionUserIds(List<UserBean> permissionUserIds) {
        this.permissionUserIds = permissionUserIds;
    }

    public List<Long> permissionUserIds() {
        if(StringUtils.isNotEmpty(permissionUserIdStr)){
            try {
                List<Long> userIds = new ArrayList<>();
                String[] userIdArray = permissionUserIdStr.substring(1,permissionUserIdStr.length()-1).split(",");
                for (String userId : userIdArray) {
                    if(StringUtils.isNotEmpty(userId))
                        userIds.add(Long.valueOf(userId));
                }
                return userIds;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return Collections.EMPTY_LIST;
    }

    public String getPermissionUserIdStr() {
        return permissionUserIdStr;
    }

    public void setPermissionUserIdStr(String permissionUserIdStr) {
        this.permissionUserIdStr = permissionUserIdStr;
    }

    @Override
    public String toString() {
        return "MonRespoDto{" +
                "uid=" + uid +
                ", repoName=" + repoName +
                ", repoType=" + repoType +
                ", departmentId=" + departmentId +
                ", totalPerson=" + totalPerson +
                ", totalObject=" + totalObject +
                ", totalAll=" + totalAll +
                '}';
    }

}
