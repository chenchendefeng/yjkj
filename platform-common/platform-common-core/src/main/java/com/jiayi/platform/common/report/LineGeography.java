package com.jiayi.platform.common.report;

import java.util.List;

public class LineGeography extends BaseGeography{
    @Override
    public boolean isWithin(double latitude, double longitude) {
        return false;
    }

    /**
     * 传入的经纬度位置，是否在该地址位置区域
     * @param points 经纬度点
     * @return trun 在区域内 false不在
     */
    @Override
    public  boolean isWithin(List<Point> points){
        return false;
    }
}
