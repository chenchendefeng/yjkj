package com.jiayi.platform.alarm.entity;

import javax.persistence.*;

//fixme 未使用到
@Entity
@Table(name = "adjusted_device_location")
public class AdjustDeviceLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_id")
    private Long pkId;
    private Long id;
    @Column(name = "place_id")
    private Long placeId;
    private Long longitude;
    private Long latitude;
    private String name;

    public Long getPkId() {
        return pkId;
    }

    public void setPkId(Long pkId) {
        this.pkId = pkId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Long placeId) {
        this.placeId = placeId;
    }

    public Long getLongitude() {
        return longitude;
    }

    public void setLongitude(Long longitude) {
        this.longitude = longitude;
    }

    public Long getLatitude() {
        return latitude;
    }

    public void setLatitude(Long latitude) {
        this.latitude = latitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "AdjustDeviceLocation [pkId=" + pkId + ", id=" + id + ", placeId=" + placeId + ", longitude=" + longitude
                + ", latitude=" + latitude + ", name=" + name + "]";
    }
}
