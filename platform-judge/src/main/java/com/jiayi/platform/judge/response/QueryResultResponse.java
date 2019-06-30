package com.jiayi.platform.judge.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class QueryResultResponse<T> {
    private List<QueryResultFieldInfo> fieldNames;
    private List<T> responseList;
}
