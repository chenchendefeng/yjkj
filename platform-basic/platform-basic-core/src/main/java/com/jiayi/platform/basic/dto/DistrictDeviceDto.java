package com.jiayi.platform.basic.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "行政区域设备统计结果")
public class DistrictDeviceDto extends DeviceStatisticDto {
    @ApiModelProperty(value = "区id", name = "区id", example = "440306")
    private Integer districtId;
    @ApiModelProperty(value = "区名称", name = "区名称", example = "宝安")
    private String district;

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public Integer getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Integer districtId) {
        this.districtId = districtId;
    }

    @Override
    public String getContent() {
        return district + ",\t" + this.getPlaceCount() + ",\t" + this.getDeviceCount() + ",\t" + this.getOnlineCount() + getOnlinePercent()
                + ",\t" + this.getOfflineCount() + getOfflinePercent()
                + ",\t" + this.getSevenDaysCount() + ",\t" + this.getNewDevicesCount();
    }
}
