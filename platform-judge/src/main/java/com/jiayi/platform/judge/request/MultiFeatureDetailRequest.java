package com.jiayi.platform.judge.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper=true)
public class MultiFeatureDetailRequest extends JudgeDetailRequest {
    private String objectValue;
    private Long beginDate;
    private Long endDate;
    private Double matchCount;
    private String matchObjectTypeName;
    private String matchObjectValue;
}
