package com.jiayi.platform.alarm.dto;

public class PlaceAlarmMsgVo {
    private String name;
    private Long timeDiff;
    private String address;
    private Long distance;

    public PlaceAlarmMsgVo(String name, Long timeDiff, String address, Long distance) {
        super();
        this.name = name;
        this.timeDiff = timeDiff;
        this.address = address;
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTimeDiff() {
        return timeDiff;
    }

    public void setTimeDiff(Long timeDiff) {
        this.timeDiff = timeDiff;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getDistance() {
        return distance;
    }

    public void setDistance(Long distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "PlaceAlarmMsgVo [name=" + name + ", timeDiff=" + timeDiff + ", address=" + address + ", distance="
                + distance + "]";
    }
}
