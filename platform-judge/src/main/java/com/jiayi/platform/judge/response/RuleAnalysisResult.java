package com.jiayi.platform.judge.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class RuleAnalysisResult<T> {
    private T resultList;// 表格信息
    private List<String> trackCountList;// 聚点间路径统计信息

    public RuleAnalysisResult() {
    }

    public RuleAnalysisResult(T resultList, List<String> trackCountList) {
        this.resultList = resultList;
        this.trackCountList = trackCountList;
    }
}
