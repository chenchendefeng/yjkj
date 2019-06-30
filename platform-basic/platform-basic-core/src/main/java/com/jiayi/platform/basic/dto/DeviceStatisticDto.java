package com.jiayi.platform.basic.dto;

import com.jiayi.platform.common.web.util.CsvContent;
import io.swagger.annotations.ApiModelProperty;

import java.text.DecimalFormat;

public abstract class DeviceStatisticDto implements CsvContent {
    @ApiModelProperty(value = "设备在线数", example = "100")
    private Long onlineCount = 0l;
    @ApiModelProperty(value = "设备离线数", example = "20")
    private Long offlineCount = 0l;
    @ApiModelProperty(value = "设备合格数", example = "30")
    private Long qualifiedCount = 0l;
    @ApiModelProperty(value = "设备待定数", example = "80")
    private Long undeterminedCount = 0l;
    @ApiModelProperty(value = "设备不合格数", example = "10")
    private Long unqualifiedCount = 0l;
    @ApiModelProperty(value = "设备数", example = "120")
    private Integer deviceCount = 0;
    @ApiModelProperty(value = "7天上报数", example = "50")
    private Long sevenDaysCount = 0l;
    @ApiModelProperty(value = "30天新增数", example = "10")
    private Long newDevicesCount = 0l;

    @ApiModelProperty(value = "该供应商设备总数", example = "120")
    private Integer deviceTotalCount = 0;

    @ApiModelProperty(value = "场所数", example = "100")
    private Long placeCount = 0l;

    public DeviceStatisticDto() {

    }

//    public DeviceCountDto(Long onlineCount, Long offlineCount, Long qualifiedCount, Long undeterminedCount,
//            Long unqualifiedCount, Integer deviceCount, Long sevenDaysCount, Long newDevicesCount) {
//        super();
//        this.onlineCount = onlineCount;
//        this.offlineCount = offlineCount;
//        this.qualifiedCount = qualifiedCount;
//        this.undeterminedCount = undeterminedCount;
//        this.unqualifiedCount = unqualifiedCount;
//        this.deviceCount = deviceCount;
//        this.sevenDaysCount = sevenDaysCount;
//        this.newDevicesCount = newDevicesCount;
//    }

    public String getOnlinePercent() {
        if (getDeviceCount() > 0 && getOnlineCount() > 0) {
            double percent = (getOnlineCount() / (float) getDeviceTotalCount()) * 100;
            DecimalFormat df = new DecimalFormat("0.00");
            return "(" + df.format(percent) + "%)";
        } else {
            return "(0%)";
        }
    }

    public String getOfflinePercent() {
        if (getDeviceCount() > 0 && getOfflineCount() > 0) {
            double percent = (getOfflineCount() / (float) getDeviceTotalCount()) * 100;
            DecimalFormat df = new DecimalFormat("0.00");
            return "(" + df.format(percent) + "%)";
        } else {
            return "(0%)";
        }
    }

    public String getQualifiedPercent() {
        if (getDeviceCount() > 0 && getQualifiedCount() > 0) {
            double percent = (getQualifiedCount() / (float) getDeviceTotalCount()) * 100;
            DecimalFormat df = new DecimalFormat("0.00");
            return "(" + df.format(percent) + "%)";
        } else {
            return "(0%)";
        }
    }

    public String getUnqualifiedPercent() {
        if (getDeviceCount() > 0 && getUnqualifiedCount() > 0) {
            double percent = (getUnqualifiedCount() / (float) getDeviceTotalCount()) * 100;
            DecimalFormat df = new DecimalFormat("0.00");
            return "(" + df.format(percent) + "%)";
        } else {
            return "(0%)";
        }
    }

    public String getUndeterminedPercent() {
        if (getDeviceCount() > 0 && getUndeterminedCount() > 0) {
            double percent = (getUndeterminedCount() / (float) getDeviceTotalCount()) * 100;
            DecimalFormat df = new DecimalFormat("0.00");
            return "(" + df.format(percent) + "%)";
        } else {
            return "(0%)";
        }
    }

    public Long getOnlineCount() {
        return onlineCount;
    }

    public void setOnlineCount(Long onlineCount) {
        this.onlineCount = onlineCount;
    }

    public Long getOfflineCount() {
        return offlineCount;
    }

    public void setOfflineCount(Long offlineCount) {
        this.offlineCount = offlineCount;
    }

    public Long getQualifiedCount() {
        return qualifiedCount;
    }

    public void setQualifiedCount(Long qualifiedCount) {
        this.qualifiedCount = qualifiedCount;
    }

    public Long getUndeterminedCount() {
        return undeterminedCount;
    }

    public void setUndeterminedCount(Long undeterminedCount) {
        this.undeterminedCount = undeterminedCount;
    }

    public Long getUnqualifiedCount() {
        return unqualifiedCount;
    }

    public void setUnqualifiedCount(Long unqualifiedCount) {
        this.unqualifiedCount = unqualifiedCount;
    }

    public Integer getDeviceCount() {
        return deviceCount;
    }

    public void setDeviceCount(Integer deviceCount) {
        this.deviceCount = deviceCount;
    }

    public Long getSevenDaysCount() {
        return sevenDaysCount;
    }

    public void setSevenDaysCount(Long sevenDaysCount) {
        this.sevenDaysCount = sevenDaysCount;
    }

    public Long getNewDevicesCount() {
        return newDevicesCount;
    }

    public void setNewDevicesCount(Long newDevicesCount) {
        this.newDevicesCount = newDevicesCount;
    }

    public Integer getDeviceTotalCount() {
        return deviceTotalCount;
    }

    public void setDeviceTotalCount(Integer deviceTotalCount) {
        this.deviceTotalCount = deviceTotalCount;
    }

    public Long getPlaceCount() {
        return placeCount;
    }

    public void setPlaceCount(Long placeCount) {
        this.placeCount = placeCount;
    }
}
