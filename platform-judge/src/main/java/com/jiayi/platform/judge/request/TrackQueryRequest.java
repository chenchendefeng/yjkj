package com.jiayi.platform.judge.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 轨迹查询、轨迹合并共用Request
 */
@Getter
@Setter
@ToString(callSuper=true)
public class TrackQueryRequest extends JudgeRequest {
    List<TrackRequest> trackRequests;
}
