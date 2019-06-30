package com.jiayi.platform.judge.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class AggregateCollideFieldRequest {
    private List<Long> uidList;
}
