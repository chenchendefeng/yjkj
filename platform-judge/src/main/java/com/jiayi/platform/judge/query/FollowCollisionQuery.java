package com.jiayi.platform.judge.query;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Getter
@Setter
@ToString
public class FollowCollisionQuery extends PageBaseQuery {
    private Set<String> objTableList;
    private Set<String> refTableList;
    private Integer trackType;
    private Long objRecentBeginHours;
    private Long objRecentEndHours;
    private Long objRecentBeginDate;
    private Long objRecentEndDate;
//    private Long objBeginHours;
//    private Long objEndHours;
    private Long objBeginDate;
    private Long objEndDate;
    private Long refRecentBeginHours;
    private Long refRecentEndHours;
    private Long refRecentBeginDate;
    private Long refRecentEndDate;
    private Long refBeginHours;
    private Long refEndHours;
    private Long refBeginDate;
    private Long refEndDate;
    private String refObjectValue;
    private Long refTimeOffset;
//    private Set<Long> gridList;
    private Set<Long> deviceIdList;
    private Integer matchCount;
    private Set<String> objectValueList;
    private Integer splitHours;
}
