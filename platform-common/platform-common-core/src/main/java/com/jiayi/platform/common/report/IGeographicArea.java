package com.jiayi.platform.common.report;

public interface IGeographicArea {

    /**
     * 传入的经纬度位置，是否在该地址位置区域
     * @param latitude
     * @param longitude
     * @return trun 在区域内 false不在
     */
    public boolean isWithin (double latitude, double longitude);

}
