package com.jiayi.platform.alarm.util;

import com.jiayi.platform.basic.dto.CityDto;

import java.util.Map;

public class CityCodeUtil {
    public static Map<Long, CityDto> districtMap=null;

    public static String getCityAreaName(Integer district){
        try {
            return districtMap.get(district.longValue()).getMergerName();
        } catch (Exception e) {
            return "";
        }
    }
}
