package com.jiayi.platform.judge.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class MiningQueryHistoryParam extends JudgeRequest {
    private Integer repoId;
    private String repoName;
    private List<String> objTypes;
    private Long startTime;
    private Long endTime;
    private Integer repoType;
}
