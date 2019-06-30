package com.jiayi.platform.judge.common.bean;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;

/**
 * @author : weichengke
 * @date : 2019-04-21 15:26
 */
@Getter
@Setter
public class GeneralRequest<T> {
    @Valid
    private T request;
    private PageRequest pageRequest;
}
