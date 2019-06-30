package com.jiayi.platform.judge.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString(callSuper=true)
public class AreaConditionRequest extends JudgeDetailRequest{
    private String objectId;
    private List<AreaCondition> conditionList;
}
