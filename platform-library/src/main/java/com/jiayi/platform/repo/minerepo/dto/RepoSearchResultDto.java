package com.jiayi.platform.repo.minerepo.dto;

import java.util.List;
import java.util.Map;

public class RepoSearchResultDto {

    private String repoName;
    private List<Map<String, Object>> datas;
    private List<Map<String, Object>> groupMap;
    private Long total;

    public String getRepoName() {
        return repoName;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    public List<Map<String, Object>> getDatas() {
        return datas;
    }

    public void setDatas(List<Map<String, Object>> datas) {
        this.datas = datas;
    }

    public List<Map<String, Object>> getGroupMap() {
        return groupMap;
    }

    public void setGroupMap(List<Map<String, Object>> groupMap) {
        this.groupMap = groupMap;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
