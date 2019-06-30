package com.jiayi.platform.repo.minerepo.vo;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class MiningRepoRequest {
    @NotBlank(message = "库名称不能为空")
    @ApiModelProperty(value = "库名称", example = "扰乱市场库")
    private String repoName;
    @NotNull(message = "库类型不能为空")
    @ApiModelProperty(value = "库类型", example = "1 规律类/2 职业类/3 其它类")
    private Integer repoType;
    @ApiModelProperty(value = "所属部门ID", example = "1")
    private Integer departmentId;
    @ApiModelProperty(value = "库描述", example = "扰乱市场人员名单")
    private String repoDesc;

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

}
