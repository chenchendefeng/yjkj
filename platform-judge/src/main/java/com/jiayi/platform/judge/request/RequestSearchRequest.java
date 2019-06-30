package com.jiayi.platform.judge.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RequestSearchRequest {
    private String caseId;
    private String userId;
    private String requestType;
}
