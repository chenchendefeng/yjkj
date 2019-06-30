package com.jiayi.platform.basic.util;


import com.jiayi.platform.basic.dto.ProvinceCityDistrictDto;

import java.util.Map;
import java.util.Random;

/**
 * @program: platform
 * @description: 场所设备编码定义
 * @author: Mr.liang
 * @create: 2018-08-21 10:49
 **/
public class PlaceDeviceUtil {
    public static Map<Long, ProvinceCityDistrictDto> districtMap = null;

    /**
     * @param district     6位的区编码定义，GB/T 2260规定的行政区划代码生成
     * @param placeTagCode 1位的场所分类
     * @param placeType    场所类型 1：经营，2：非经营，3：围栏采集
     * @param seqence      第9至第14位用6位阿拉伯数字表示序列号
     * @return
     */
    public static String generatePlaceCode(String district, String placeTagCode, int placeType, long seqence) {
        StringBuilder sb = new StringBuilder();
        sb.append(district);
        switch (placeType) {
            case 1:
                sb.append("10");
                break;
            case 2:
                sb.append("2").append(placeTagCode);
                break;
            case 3:
                sb.append("3").append(placeTagCode);
                break;
        }
        String result = String.format("%0" + 6 + "d", seqence);
        return sb.toString() + result;
    }

    public static String getCityAreaName(String district) {
        try {
            Long id = Long.parseLong(district);
            return districtMap.get(id).getMergerName();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * @param district     6位的区编码定义，GB/T 2260规定的行政区划代码生成
     * @param placeTagCode 1位的场所分类
     * @param placeType    场所类型 1：经营，2：非经营，3：围栏采集
     * @return
     */
    public static String generatePlaceCode(String district, String placeTagCode, int placeType) {
        StringBuilder sb = new StringBuilder();
        sb.append(district);
        switch (placeType) {
            case 1:
                sb.append("100").append(getRandomString(5).toUpperCase());
                break;
            case 2:
                sb.append("2").append(placeTagCode).append(getRandomString(6).toUpperCase());
                break;
            case 3:
                sb.append("3").append(placeTagCode).append(getRandomString(6).toUpperCase());
                break;
        }
        return sb.toString();
    }

    /**
     * @param length 产生随机字符串的长度
     * @return
     */
    public static String getRandomString(int length) {
        String str = "aSTUpqbcdVWoefLMXghKYZnNOmHIrstJ45PQRuvwxyDEFG0123ijkl678zABC9";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }
}
