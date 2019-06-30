package com.jiayi.platform.common.report;


import java.util.List;

public abstract class BaseGeography implements IGeographicArea{

    private List<Point> points;

    private String type;

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    public List<Point> getPoints() {
        return points;
    }

    /**
     * 传入的经纬度位置，是否在该地址位置区域
     * @param points 经纬度点
     * @return trun 在区域内 false不在
     */
    public abstract boolean isWithin(List<Point> points);
}
