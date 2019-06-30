package com.jiayi.platform.basic.dto;

import com.jiayi.platform.basic.entity.DeviceModel;
import com.jiayi.platform.basic.entity.DeviceSubType;
import com.jiayi.platform.basic.entity.Vendor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DeviceSubTypeDto {
    private List<DeviceSubType> subTypes;
    private List<Vendor> vendors;
    private List<DeviceModel> models;// 型号

    public DeviceSubTypeDto(List<DeviceSubType> subTypes, List<Vendor> vendors, List<DeviceModel> models) {
        this.subTypes = subTypes;
        this.vendors = vendors;
        this.models = models;
    }

    public DeviceSubTypeDto() {
    }
}
