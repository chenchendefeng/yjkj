package com.jiayi.platform.repo.monrepo.vo;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public class MonRepoRequest {
    @NotBlank(message = "库名称不能为空")
    @ApiModelProperty(value = "库名称", example = "扰乱市场库")
    private String repoName;
    @NotNull(message = "库类型不能为空")
    @ApiModelProperty(value = "库类型:1 黑名单/2 白名单/3 重点人员库/4 在逃人员库", example = "1")
    private Integer repoType;
    @ApiModelProperty(value = "所属部门ID", example = "1")
    private Integer departmentId;
    @ApiModelProperty(value = "库描述", example = "扰乱市场人员名单")
    private String repoDesc;
    @NotNull(message = "操作员ID不能为空")
    @ApiModelProperty(value = "操作员ID", example = "231")
    private Integer userId;
    @NotNull(message = "可见范围不能为空")
    @ApiModelProperty(value = "可见范围:公开库1,私密库2", example = "1")
    private Integer permissionType;
    @ApiModelProperty(value = "可见人员ID")
    private List<Integer> permissionUserIds;

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
}
