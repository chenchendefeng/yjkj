package com.jiayi.platform.repo.minerepo.dto;


import com.jiayi.platform.repo.minerepo.vo.MineRepoTableDesc;

import java.util.List;
import java.util.Map;

public class MineRepoDetailDto {
    private List<MineRepoTableDesc.FieldDesc> titles;
    private List<Map<String, Object>> data;
    private Long total;
    private Integer page;
    private Integer size;

    public MineRepoDetailDto() {}

    public MineRepoDetailDto(List<MineRepoTableDesc.FieldDesc> titles, List<Map<String, Object>> data, Long total, Integer page, Integer size) {
        this.titles = titles;
        this.data = data;
        this.total = total;
        this.page = page;
        this.size = size;
    }

    public List<MineRepoTableDesc.FieldDesc> getTitles() {
        return titles;
    }

    public void setTitles(List<MineRepoTableDesc.FieldDesc> titles) {
        this.titles = titles;
    }

    public List<Map<String, Object>> getData() {
        return data;
    }

    public void setData(List<Map<String, Object>> data) {
        this.data = data;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
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
