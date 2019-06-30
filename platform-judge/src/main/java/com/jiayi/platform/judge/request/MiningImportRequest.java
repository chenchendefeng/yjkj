package com.jiayi.platform.judge.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class MiningImportRequest extends JudgeRequest {
    private String resultName;
    private List<String> objTypes;
    private Long startTime;
    private Long endTime;
    private Long operateUserId;
}
