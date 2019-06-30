package com.jiayi.platform.basic.dao;

import com.jiayi.platform.basic.entity.Device;
import com.jiayi.platform.basic.entity.DeviceTimeStreamStatistic;
import com.jiayi.platform.basic.serviceImpl.DeviceServiceImpl;
import com.jiayi.platform.common.enums.CollectType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

/**
 * @author : weichengke
 * @date : 2019-03-01 10:32
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DeviceDaoTest {

    private Logger log = LoggerFactory.getLogger(DeviceDaoTest.class);

    @Autowired
    private DeviceDao deviceDao;
    @Autowired
    private DeviceTimeStreamStatisticDao deviceTimeStreamStatisticDao;

    @Autowired
    private DeviceServiceImpl deviceService;

    @Test
    public void findAllValidDevices() {
        long startTime = System.currentTimeMillis();
        List<Device> result = deviceDao.findAllValidDevices();
        long cost = System.currentTimeMillis() - startTime;
        log.info("total device count is: {}, cost {}ms", result.size(), cost);
    }

    @Test
    public void countAllValidDevices() {
        long startTime = System.currentTimeMillis();
        log.info("total device count is: {}, cost {}ms", deviceDao.countAllValidDevices(), System.currentTimeMillis() - startTime);
    }

    @Test
    public void countPlaceAndDevice() {
        Map<String, Object> map = deviceService.countPlaceAndDevice();
        System.out.println(map);
    }

    @Test
    public void findDeviceStatusInfo() {
        Set<String> values = new HashSet() {{
            add("1001-C8EEA62C2AF8");
            add("3-C8EEA6354B37");
        }};
        List<DeviceTimeStreamStatistic> list = deviceTimeStreamStatisticDao.findDeviceStatusInfo(values);
        System.out.println(list);
    }

    @Test
    public void findByTypeIn() {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        List<Device> result = deviceDao.findByTypeIn(list);
        System.out.println(result);
    }

    @Test
    public void findOne() {
        Device decice = deviceDao.findOneBySrcAndCode("1001", "C8EEA62C2AF8");
        System.out.println(decice.getPkId());
    }

    @Test
    public void findBySrCode() {
        Set set = new HashSet() {
            {
                add("1001003|C8EEA62C2AF8");
                add("1001003|123456789ABC1002");
            }
        };
        List<Device> list = deviceDao.findBySrcAndCode(set);
        System.out.println(list.size());
    }

    @Test
    public void findByMainType() {
        List<Device> list = deviceDao.findByMainType(4);
        System.out.println(list.size());
    }

    public void findByType() {
        List<Device> devices = deviceDao.findByType(1);
        System.out.println("======>" + devices.size());
    }

    @Test
    public void findByCollectType(){
        List<Device> devices = deviceDao.findByCollectType(CollectType.MAC.name());
        System.out.println("======>" + devices.size());
    }

    @Test
    public void findByPkIds() {
        Set<Long> pkIds = new HashSet<>();
        pkIds.add(11450L);
        pkIds.add(11452L);
        pkIds.add(11473L);
        List<Device> devices = deviceDao.findByPkIdIn(pkIds);
        System.out.println("======>" + devices.size());
    }

    @Test
    public void findByIds() {
        Set<Long> ids = new HashSet<>();
        ids.add(5343279113474782103L);
        ids.add(-7768552054782819370L);
        ids.add(8225244733950261118L);
        List<Device> devices = deviceDao.findByIdIn(ids);
        System.out.println("======>" + devices.size());
    }
}
