package com.jiayi.platform.judge.query;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
public class AreaCollisionQuery extends PageBaseQuery {
    private List<AreaConditionBean> conditionList;
    private Integer trackType;
    private Set<Long> totalDeviceIdList;
    private Integer matchCount;
    private Set<String> objectValueList;
}
