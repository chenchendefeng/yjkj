package com.jiayi.platform.alarm.entity;


import com.jiayi.platform.report.geography.BaseGeography;
import com.jiayi.platform.report.geography.FactoryGeography;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "alarm_area_info")
public class AlarmAreaInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "parent_id")
    private Long parentId;
    private String name;
    @Column(name = "place_id")
    private Long placeId;
    @Column(name = "map_region")
    private String mapRegion;
    private Double area;
    @Column(name = "start_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;
    @Column(name = "end_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endTime;
    @Column(name = "warning_num")
    private Long warningNum;
    @Column(name = "max_num")
    private Long maxNum;
    @Column(name = "type")
    private Long period;
    private Double factor;
    private Boolean enable;
    private Boolean valid;
    @Column(name = "create_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createAt;
    @Column(name = "update_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Long placeId) {
        this.placeId = placeId;
    }

    public String getMapRegion() {
        return mapRegion;
    }

    public void setMapRegion(String mapRegion) {
        this.mapRegion = mapRegion;
    }

    public Double getArea() {
        return area;
    }

    public void setArea(Double area) {
        this.area = area;
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

    public Long getWarningNum() {
        return warningNum;
    }

    public void setWarningNum(Long warningNum) {
        this.warningNum = warningNum;
    }

    public Long getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(Long maxNum) {
        this.maxNum = maxNum;
    }

    public Long getPeriod() {
        return period;
    }

    public void setPeriod(Long period) {
        this.period = period;
    }

    public Double getFactor() {
        return factor;
    }

    public void setFactor(Double factor) {
        this.factor = factor;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
    public BaseGeography getGeography(){
        return FactoryGeography.getInstanceByJson(mapRegion);
    }

    @Override
    public String toString() {
        return "AlarmAreaInfo{" +
                "id=" + id +
                ", parentId=" + parentId +
                ", name='" + name + '\'' +
                ", placeId=" + placeId +
                ", mapRegion='" + mapRegion + '\'' +
                ", area=" + area +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", warningNum=" + warningNum +
                ", maxNum=" + maxNum +
                ", period=" + period +
                ", factor=" + factor +
                ", enable=" + enable +
                ", valid=" + valid +
                ", createAt=" + createAt +
                ", updateAt=" + updateAt +
                '}';
    }
}
