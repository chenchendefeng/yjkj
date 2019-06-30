package com.jiayi.platform.judge.query;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 轨迹查询、轨迹合并共用Query
 */
@Getter
@Setter
@ToString
public class TrackQueryQuery extends PageBaseQuery {
    private List<TrackQuery> queryList;
}
