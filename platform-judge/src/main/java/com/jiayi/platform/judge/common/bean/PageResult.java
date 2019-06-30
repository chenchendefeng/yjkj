package com.jiayi.platform.judge.common.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PageResult<T> extends Result<T> {

    PageInfo pageInfo;

    public PageResult() {
        super();
        pageInfo = new PageInfo();
    }

    public PageResult(T payload) {
        super(payload);
        pageInfo = new PageInfo();
    }

    public PageResult(T payload, PageInfo pageInfo) {
        super(payload);
        this.pageInfo = pageInfo;
    }
}
