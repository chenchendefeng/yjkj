package com.jiayi.platform.judge.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RuleAnalysisDetailDto {
    private Long recordAt;
    private Long deviceId;
    private String apMac = "";
    private String apName = "";
    private Integer power;
    private Integer channel;
    private String imsiImei = "";
}
