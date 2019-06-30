package com.jiayi.platform.common.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CityCodeConfig {
    @Value("${prodcut.city.id:441200}")
    private String cityId;

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

}
