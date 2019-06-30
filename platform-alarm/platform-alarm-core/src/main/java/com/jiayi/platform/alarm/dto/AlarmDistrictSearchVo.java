package com.jiayi.platform.alarm.dto;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class AlarmDistrictSearchVo extends PageSearchVo{

    @ApiModelProperty(value = "布控名称")
    private String name;
    @ApiModelProperty(value = "策略类型：2触碰、3入圈、4出圈、5消失、6聚集", example = "2")
    private Integer type;
    @ApiModelProperty(value = "归属地")
    private Integer district;
    @ApiModelProperty(value = "添加开始时间(格式: yyyy-MM-dd HH:mm:ss)")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    @ApiModelProperty(value = "添加结束时间(格式: yyyy-MM-dd HH:mm:ss)")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getDistrict() {
        return district;
    }

    public void setDistrict(Integer district) {
        this.district = district;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
