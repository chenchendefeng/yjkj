package com.jiayi.platform.basic.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceSubTypeRequest {
    private String name;
    private String dataType;//dataType,用‘，’隔开
    private String description;
    private Integer deviceType;// 设备主类型
}
