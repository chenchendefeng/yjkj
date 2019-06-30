package com.jiayi.platform.judge.query;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class AreaConditionQuery extends PageBaseQuery {
    private String objectValue;
    private Integer objectHash;
    private List<AreaConditionBean> conditionList;
    private Integer trackType;
}
