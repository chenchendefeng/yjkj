package com.jiayi.platform.repo.minerepo.vo;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;

public class RepoSearchVo {
    @ApiModelProperty(value = "库类型:20全部 21常用库 22挖掘库")
    private Integer repoType;
    @ApiModelProperty(value = "搜索关键字")
    @NotBlank(message = "搜索关键字不能为空")
    private String value;
    @ApiModelProperty(value = "页码0开始")
    private Integer page = 0;
    @ApiModelProperty(value = "每页数量")
    private Integer size = 10;

    public Integer getRepoType() {
        return repoType;
    }

    public void setRepoType(Integer repoType) {
        this.repoType = repoType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
