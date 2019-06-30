package com.jiayi.platform.judge.query;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DeviceAnalysisStatQuery extends DeviceAnalysisDetailQuery {
    private String statType;
}
