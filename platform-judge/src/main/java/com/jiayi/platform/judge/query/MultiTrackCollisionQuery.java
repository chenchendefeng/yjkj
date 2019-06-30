package com.jiayi.platform.judge.query;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Getter
@Setter
@ToString
public class MultiTrackCollisionQuery extends PageBaseQuery {
//    private String objectTypeName;
//    private String refObjectTypeName;
    private Set<String> objTableList;
    private Set<String> refTableList;
    private Integer objTrackType;
    private Integer refTrackType;
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
    private Integer matchCount;
    private Integer distance;
    private Set<String> objectValueList;
    private Integer splitHours;
}
