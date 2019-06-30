package com.jiayi.platform.judge.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RuleAnalysisDetailResponse {
    private Long startAt;
    private String address = "";

    private String apMac = "";
    private String apName = "";
    private String power = "";
    private String channel = "";
    private String imsiImei = "";
}
