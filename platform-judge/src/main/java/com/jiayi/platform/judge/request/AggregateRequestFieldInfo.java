package com.jiayi.platform.judge.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AggregateRequestFieldInfo {
    private Long uid;
    private String resultSetName;
    private Long fieldId;
    private String fieldType;
    private String fieldDesc;
    private String fieldName;
    private String requestType;
    private Integer referTo;
}
