package com.jiayi.platform.judge.common.bean;

import com.jiayi.platform.judge.enums.ResultStatusEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Result<T> {
    protected int status;
    protected T payload;

    public Result() {
    }

    public Result(T payload) {
        status = ResultStatusEnum.SUCCESS.code();
        this.payload = payload;
    }
}
