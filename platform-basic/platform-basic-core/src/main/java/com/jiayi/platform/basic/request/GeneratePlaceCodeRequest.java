package com.jiayi.platform.basic.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class GeneratePlaceCodeRequest {
    private String district;
    private List<Long> placeTags;
    private Integer placeType;
}
