package com.jiayi.platform.common.web.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class PageableRequest<T> extends PageRequest {

    private static final int DEFAULT_PAGE = 0;

    private static final int DEFAULT_PAGE_SIZE = 10;

    private T params;

    public PageableRequest(int page, int size) {
        super(page, size);
    }

    @JsonCreator
    public PageableRequest(@JsonProperty(value = "page") int page,
                           @JsonProperty(value = "size") int size,
                           @JsonProperty("params") T params) {
        super(page == 0 ? DEFAULT_PAGE : page, size == 0 ? DEFAULT_PAGE_SIZE : size);
        this.params = params;
    }

    public PageableRequest(int page, int size, Sort.Direction direction, String... properties) {
        super(page, size, direction, properties);
    }

    public PageableRequest(int page, int size, Sort sort) {
        super(page, size, sort);
    }

    public T getParams() {
        return params;
    }

    public void setParams(T params) {
        this.params = params;
    }
}
