package com.jiayi.platform.basic.serviceImpl;

import com.jiayi.platform.basic.dao.DeviceTimeStreamStatisticDao;
import com.jiayi.platform.basic.entity.DeviceTimeStreamStatistic;
import com.jiayi.platform.basic.service.DeviceTimeStreamStatisticService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class DeviceTimeStreamStatisticServiceImpl implements DeviceTimeStreamStatisticService {

    @Autowired
    private DeviceTimeStreamStatisticDao deviceTimeStreamStatisticDao;

    @Override
    public List<DeviceTimeStreamStatistic> findDeviceStatusBySrcAndCode(Set<String> srcAndCodes) {
        return deviceTimeStreamStatisticDao.findDeviceStatusInfo(srcAndCodes);
    }
}
