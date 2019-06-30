package com.jiayi.platform.common.report;


import java.util.List;

public class RectGeography extends BaseGeography{
    private double minLat;
    private double maxLat;
    private double minLng;
    private double maxLng;
    private boolean isInit = false;

    public RectGeography() {

    }

    public RectGeography(List<Point> points) {
        if (points == null || points.size() < 2){
            return;
        }
        minLat = points.get(0).getLatitude();
        minLng = points.get(0).getLongitude();
        maxLat = points.get(0).getLatitude();
        maxLng = points.get(0).getLongitude();
        for (int i = 1; i < points.size(); i++) {
            minLat = Math.min(minLat, points.get(i).getLatitude());
            minLng = Math.min(minLng, points.get(i).getLongitude());
            maxLat = Math.max(maxLat, points.get(i).getLatitude());
            maxLng = Math.max(maxLng, points.get(i).getLongitude());
        }
    }

    private void init(){
        if (isInit){
            return;
        }
        List<Point>  points = getPoints();
        if ( points == null || points.size() < 2){
            return;
        }
        minLat = points.get(0).getLatitude();
        minLng = points.get(0).getLongitude();
        maxLat = points.get(0).getLatitude();
        maxLng = points.get(0).getLongitude();
        for (int i = 1; i < points.size(); i++) {
            minLat = Math.min(minLat, points.get(i).getLatitude());
            minLng = Math.min(minLng, points.get(i).getLongitude());
            maxLat = Math.max(maxLat, points.get(i).getLatitude());
            maxLng = Math.max(maxLng, points.get(i).getLongitude());
        }
        isInit = true;
    }

    public RectGeography(double minLat, double minLng, double maxLat, double maxLng) {
        this.minLat = minLat;
        this.maxLat = maxLat;
        this.minLng = minLng;
        this.maxLng = maxLng;
    }

    public double getMinLat() {
        return minLat;
    }

    public void setMinLat(double minLat) {
        this.minLat = minLat;
    }

    public double getMaxLat() {
        return maxLat;
    }

    public void setMaxLat(double maxLat) {
        this.maxLat = maxLat;
    }

    public double getMinLng() {
        return minLng;
    }

    public void setMinLng(double minLng) {
        this.minLng = minLng;
    }

    public double getMaxLng() {
        return maxLng;
    }

    public void setMaxLng(double maxLng) {
        this.maxLng = maxLng;
    }

    @Override
    public boolean isWithin(double latitude, double longitude) {
        init();
        if (latitude >= getMinLat() && latitude <= getMaxLat() && longitude >= getMinLng()
                && longitude <= getMaxLng()) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * 传入的经纬度位置，是否在该地址位置区域
     * @param points 经纬度点
     * @return trun 在区域内 false不在
     */
    @Override
    public boolean isWithin(List<Point> points){
        if (points == null || points.size() < 1){
            return false;
        }
        init();

        for (Point point:points) {
            if (!isWithin(point.getLatitude(),point.getLongitude())){
                return false;
            }
        }
        return true;
    }
}
