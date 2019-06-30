package com.jiayi.platform.basic.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeviceTypeDto {

    private Integer id;
    private String name;
    private List<DeviceTypeDto> nextLevel;
}
