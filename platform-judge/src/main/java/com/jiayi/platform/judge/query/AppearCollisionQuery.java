package com.jiayi.platform.judge.query;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Getter
@Setter
@ToString
public class AppearCollisionQuery extends PageBaseQuery {
    private Set<String> analyzeTableList;
    private Set<String> refTableList;
    private Integer trackType;
    private Long analyzeRecentBeginHours;
    private Long analyzeRecentEndHours;
    private Long analyzeRecentBeginDate;
    private Long analyzeRecentEndDate;
    private Long analyzeBeginHours;
    private Long analyzeEndHours;
    private Long analyzeBeginDate;
    private Long analyzeEndDate;
    private Long refRecentBeginHours;
    private Long refRecentEndHours;
    private Long refRecentBeginDate;
    private Long refRecentEndDate;
    private Long refBeginHours;
    private Long refEndHours;
    private Long refBeginDate;
    private Long refEndDate;
//    private Set<Long> analyzeGridList;
    private Set<Long> analyzeDeviceIdList;
//    private Set<Long> refGridList;
    private Set<Long> refDeviceIdList;
    private Boolean selectRefArea;
    private Set<String> objectValueList;
}
