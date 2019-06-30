package com.jiayi.platform.basic.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceModelRequest {
    private String name;
    private Integer deviceSubType;
    private Integer vendorId;
    private String description;
}
