package com.jiayi.platform.judge.query;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Getter
@Setter
@ToString
public class MultiTrackDetailQuery extends PageBaseQuery {
    private Set<String> objTableList;
    private Set<String> refTableList;
    private Integer objTrackType;
    private Integer refTrackType;
    private String objectValue;
    private Integer objectHash;
    private String refObjectValue;
    private Integer refObjectHash;
    private Long objRecentBeginHours;
    private Long objRecentEndHours;
    private Long objRecentBeginDate;
    private Long objRecentEndDate;
//    private Long objRecentDaysBeginHours;
//    private Long objRecentDaysEndHours;
//    private Long objRecentDaysBeginDate;
//    private Long objRecentDaysEndDate;
    private Long objBeginHours;
    private Long objEndHours;
    private Long objBeginDate;
    private Long objEndDate;
    private Long refRecentBeginHours;
    private Long refRecentEndHours;
    private Long refRecentBeginDate;
    private Long refRecentEndDate;
//    private Long refRecentDaysBeginHours;
//    private Long refRecentDaysEndHours;
//    private Long refRecentDaysBeginDate;
//    private Long refRecentDaysEndDate;
    private Long refBeginHours;
    private Long refEndHours;
    private Long refBeginDate;
    private Long refEndDate;
    private Long refTimeOffset;
    private Integer distance;
}
