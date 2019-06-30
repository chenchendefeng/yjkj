package com.jiayi.platform.repo.minerepo.vo;

import java.util.HashMap;
import java.util.Map;

public class MiningRepoDetailSearchVO {
    private Integer page;
    private Integer size;
    private Integer repoId;

    private Map<String, String> params = new HashMap<>();

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

    public Integer getRepoId() {
        return repoId;
    }

    public void setRepoId(Integer repoId) {
        this.repoId = repoId;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }
}
