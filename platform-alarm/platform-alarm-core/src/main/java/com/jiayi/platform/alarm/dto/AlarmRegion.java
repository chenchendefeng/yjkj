package com.jiayi.platform.alarm.dto;

import com.jiayi.platform.common.bo.Location;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class AlarmRegion {
    @ApiModelProperty(value = "区域类型", required = true, example = "rect")
    private String type;
    @ApiModelProperty(value = "构成区域的点集", required = true)
    private List<Location> points;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Location> getPoints() {
        return points;
    }

    public void setPoints(List<Location> points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return "AlarmRegion [type=" + type + ", points=" + points + "]";
    }
}
