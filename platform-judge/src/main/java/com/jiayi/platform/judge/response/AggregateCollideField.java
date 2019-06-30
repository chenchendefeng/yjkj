package com.jiayi.platform.judge.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class AggregateCollideField {
    private Long uid;
    private List<AggregateCollideFieldInfo> resultFieldList;
    private String requestType;

    public AggregateCollideField(Long uid, List<AggregateCollideFieldInfo> resultFieldList, String requestType) {
        this.uid = uid;
        this.resultFieldList = resultFieldList;
        this.requestType = requestType;
    }
}
