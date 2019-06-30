package com.jiayi.platform.judge.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class MonitorRepoParam extends JudgeRequest {
    private Long repoId;
    private String repoName;
    private List<String> objTypes;
    private Integer repoType;
}
