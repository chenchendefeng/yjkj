package com.jiayi.platform.repo.minerepo.vo;

import io.swagger.annotations.ApiModelProperty;

public class PageRequest {

    @ApiModelProperty(value = "当前页", required = true, example = "0")
    private Long pageIndex;
    @ApiModelProperty(value = "每页多少条数据", required = true, example = "20")
    private Integer pageSize;

    public PageRequest () {

    }

    public PageRequest (Long pageIndex, Integer pageSize) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }

    public Long getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Long pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Long calOffset() {
        return pageIndex * pageSize;
    }
}
