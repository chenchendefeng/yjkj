package com.jiayi.platform.judge.response;

import com.jiayi.platform.judge.dto.DeviceAnalysisStatDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class DeviceAnalysisStatlResponse {
    private Integer minKey;
    private Integer maxKey;
    private String type;
    private List<DeviceAnalysisStatDto> data;
}
