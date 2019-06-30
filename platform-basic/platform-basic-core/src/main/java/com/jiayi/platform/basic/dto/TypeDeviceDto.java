package com.jiayi.platform.basic.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TypeDeviceDto extends DeviceStatisticDto {

    @ApiModelProperty(value = "供应商设备数据类型", example = "1:wifi围栏，2：电子围栏，3：其它")
    private Integer type;//1:mac,2:imsi和imei,3:carno
    @ApiModelProperty(value = "后台不传", example = "前端根据type值来展示")
    private String typeName;

    public TypeDeviceDto() {
        super();
    }

    public TypeDeviceDto(Integer type, String typeName) {
        super();
        this.type = type;
        this.typeName = typeName;
    }

//    public String getTypeName() {
//        if (type == 1) {
//            return "WIFI围栏";
//        }
//        if (type == 2) {
//            return "电子围栏";
//        }
//        if (type == 3) {
//            return "其它";
//        }
//        return "";
//    }

    @Override
    public String getContent() {
        return getTypeName() + ",\t" + this.getPlaceCount() + ",\t" + this.getDeviceCount() + ",\t" + this.getOnlineCount()
//                + getOnlinePercent()
                + ",\t" + this.getOfflineCount()
//                + getOfflinePercent()+ ",\t" + this.getQualifiedCount() +getQualifiedPercent()+ ",\t" + this.getUnqualifiedCount()+getUnqualifiedPercent()
//                + ",\t" + this.getUnqualifiedCount()+getUndeterminedPercent()
                + ",\t" + this.getSevenDaysCount() + ",\t" + this.getNewDevicesCount();
    }
}
