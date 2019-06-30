package com.jiayi.platform.basic.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class PlaceLabelDto {

    private String name;
    private String code;
    private String remark;

    private List<PlaceLabelDto> nextLevel = new ArrayList<>();

    public PlaceLabelDto(String name, String code, String remark) {
        this.name = name;
        this.code = code;
        this.remark = remark;
    }
}
