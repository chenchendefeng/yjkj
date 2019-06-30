package com.jiayi.platform.judge.query;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class AggregateQuery extends PageBaseQuery {
    private String aggregateType;
    private String resultType;
    private List<AggregateQueryBean> queryList = new ArrayList<>();
    private List<String> resultColumns = new ArrayList<>();
    private String referQueryColumn;
    private Integer referIndex;
    private String referCollisionType;
}
