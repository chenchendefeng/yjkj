package com.jiayi.platform.judge.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DeviceAnalysisDetailResponse {
    private Long recordAt;
    private String address = "";
}
