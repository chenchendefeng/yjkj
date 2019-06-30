package com.jiayi.platform.alarm.dto;

import com.jiayi.platform.alarm.enums.AlarmType;
import com.jiayi.platform.alarm.util.CityCodeUtil;

import java.util.Date;
import java.util.List;

public class AlarmDistrictDto {

    private Long id;
    private String name;
    private Integer type;
    private String mapRegion;
    private String exInfo;
    private Integer district;
    private List<String> objTypes;
    private Date startTime;
    private Date endTime;
    private Integer beLongValid;
    private List<String> userIds;
    private String remark;
    private Date createAt;
    private Date updateAt;
    private Integer status;

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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getTypeStr() {
        return AlarmType.getTaskStatusByValue(type).getDescription();
    }

    public String getMapRegion() {
        return mapRegion;
    }

    public void setMapRegion(String mapRegion) {
        this.mapRegion = mapRegion;
    }

    public String getExInfo() {
        return exInfo;
    }

    public void setExInfo(String exInfo) {
        this.exInfo = exInfo;
    }

    public Integer getDistrict() {
        return district;
    }

    public void setDistrict(Integer district) {
        this.district = district;
    }

    public String getDistrictStr() {
        return CityCodeUtil.getCityAreaName(district);
    }

    public List<String> getObjTypes() {
        return objTypes;
    }

    public void setObjTypes(List<String> objTypes) {
        this.objTypes = objTypes;
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

    public Integer getBeLongValid() {
        return beLongValid;
    }

    public void setBeLongValid(Integer beLongValid) {
        this.beLongValid = beLongValid;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
