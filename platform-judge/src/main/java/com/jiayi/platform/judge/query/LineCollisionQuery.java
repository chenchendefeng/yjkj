package com.jiayi.platform.judge.query;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Set;

/**
 * @author : weichengke
 * @date : 2019-04-20 14:43
 */
@Getter
@Setter
@ToString
public class LineCollisionQuery extends PageBaseQuery {
    private Set<String> tableNameList;
    private Integer trackType;
    private Long recentBeginHours;
    private Long recentEndHours;
    private Long recentBeginDate;
    private Long recentEndDate;
    private Long beginHours;
    private Long endHours;
    private Long beginDate;
    private Long endDate;
//    private Set<Long> gridList;
    private Set<Long> deviceIdList;
    private Integer matchCount;
    private Set<String> objectValueList;
}
