package com.jiayi.platform.basic.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class StreamStatisticGroupPK implements Serializable {
    private String src;
    private String code;
}
