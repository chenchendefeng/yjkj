package com.jiayi.platform.basic.dto;

public class DeviceLocationDetailDto {
    private long latitude;
    private long longitude;
    private long type;
    private long pkId;
    private long placeId;
    private long id;
    private String name;

    public DeviceLocationDetailDto(long latitude, long longitude, long type, long pkId, long placeId, long id,
                                   String name) {
        super();
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
        this.pkId = pkId;
        this.placeId = placeId;
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getLatitude() {
        return latitude;
    }

    public void setLatitude(long latitude) {
        this.latitude = latitude;
    }

    public long getLongitude() {
        return longitude;
    }

    public void setLongitude(long longitude) {
        this.longitude = longitude;
    }

    public long getDeviceId() {
        return id;
    }

    public void setDeviceId(long deviceId) {
        this.id = deviceId;
    }

    public void setType(long type) {
        this.type = type;
    }

    public long getType() {
        return type;
    }

    public long getPkId() {
        return pkId;
    }

    public void setPkId(long pkId) {
        this.pkId = pkId;
    }

    public double getFloatLatitude() {
        return latitude / 1000000000000.0;
    }

    public double getFloatLongitude() {
        return longitude / 1000000000000.0;
    }

    public void setPlaceId(long placeId) {
        this.placeId = placeId;
    }

    public long getPlaceId() {
        return placeId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
