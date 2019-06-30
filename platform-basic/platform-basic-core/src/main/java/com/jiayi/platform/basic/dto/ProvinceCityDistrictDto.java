package com.jiayi.platform.basic.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProvinceCityDistrictDto extends CityDto{

    private Long province;
    private Long city;

    public ProvinceCityDistrictDto(Long id, String name, String mergerName, Long province, Long city) {
        super(id, name, mergerName);
        this.province = province;
        this.city = city;
    }
}
