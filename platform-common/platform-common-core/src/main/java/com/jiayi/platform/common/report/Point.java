package com.jiayi.platform.common.report;

import java.util.Objects;

public class Point {
    private double latitude;
    private double longitude;

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof Point){
            Point point = (Point) o;
            return Double.compare(point.latitude, latitude) == 0 &&
                    Double.compare(point.longitude, longitude) == 0;
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {

        return Objects.hash(latitude, longitude);
    }

    @Override
    public String toString() {
        return "Point{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
