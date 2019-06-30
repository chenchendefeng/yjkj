package com.jiayi.platform.judge.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AreaConditionDto {
    private String objectValue;
    private String matchCondition;
    private Long minHappenAt;
    private Long maxHappenAt;
}
