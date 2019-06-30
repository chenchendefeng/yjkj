package com.jiayi.platform.basic.service;

import com.jiayi.platform.basic.entity.DeviceTimeStreamStatistic;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Set;

public interface DeviceTimeStreamStatisticService {

    List<DeviceTimeStreamStatistic> findDeviceStatusBySrcAndCode(@RequestBody Set<String> srcAndCodes);
}
