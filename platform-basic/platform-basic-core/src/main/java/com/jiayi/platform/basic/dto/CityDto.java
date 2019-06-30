package com.jiayi.platform.basic.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class CityDto {
    private Long id;
    private String name;
    private String mergerName;
    private List nextLevel;

    public CityDto(Long id, String name, String mergerName) {
        this.id = id;
        this.name = name;
        this.mergerName = mergerName;
    }
}
