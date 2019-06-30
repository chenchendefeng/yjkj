package com.jiayi.platform.basic.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "部门设备统计结果")
public class DepartmentDeviceDto extends DeviceStatisticDto {
    @ApiModelProperty(value = "部门id", name = "部门id", example = "1")
    private Integer departmentId;
    @ApiModelProperty(value = "部门名称", name = "部门名称", example = "四会公安局")
    private String departmentName;
    @ApiModelProperty(value = "父部门id", name = "父部门id", example = "0")
    private Integer departmentPid;

    public Integer getDepartmentId() {
        return departmentId;
    }
    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }
    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Integer getDepartmentPid() {
        return departmentPid;
    }
    public void setDepartmentPid(Integer departmentPid) {
        this.departmentPid = departmentPid;
    }

    @Override
    public String getContent() {
        return departmentName + ",\t" + this.getDeviceCount() + ",\t" + this.getOnlineCount()+getOnlinePercent()
                + ",\t" + this.getOfflineCount()+getOfflinePercent()
                + ",\t" + this.getQualifiedCount() +getQualifiedPercent()+ ",\t" + this.getUnqualifiedCount()+getUnqualifiedPercent()
                + ",\t" + this.getUndeterminedCount()+getUndeterminedPercent()
                + ",\t" + this.getSevenDaysCount() + ",\t" + this.getNewDevicesCount();
    }
}

