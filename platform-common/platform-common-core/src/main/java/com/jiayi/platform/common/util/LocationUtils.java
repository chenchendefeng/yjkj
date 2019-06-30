package com.jiayi.platform.common.util;

public class LocationUtils {
    public static double distance(double lng1, double lat1, double lng2, double lat2) {
        double dx = lng2 - lng1; // 经度差值
        double dy = lat2 - lat1; // 纬度差值
        double b = (lat2 + lat1) / 2.0; // 平均纬度
        double lx = (dx * Math.PI / 180) * Math.cos((b * Math.PI / 180)); // 东西距离
        double ly = (dy * Math.PI / 180); // 南北距离
        return 6367000.0 * Math.sqrt(lx * lx + ly * ly); // 用平面的矩形对角距离公式计算总距离
    }
    
    /** 
     * lng1, lat1, lng2, lat2为矩形对角线两点的经纬度
     */
    public static double rectArea(double lng1, double lat1, double lng2, double lat2) {
        return distance(lng1, lat1, lng1, lat2) * distance(lng1, lat1, lng2, lat1);
    }
}