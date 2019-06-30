package com.jiayi.platform.judge.query;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AggregateQueryBean {
    private Long uid;
    private String collisionType;
    private String queryValue;
    private AggregateMiningParam miningParam;
    private Boolean referTo;
}
