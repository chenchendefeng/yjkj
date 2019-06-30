package com.jiayi.platform.basic.serviceImpl;

import com.google.common.collect.Lists;
import com.jiayi.platform.basic.dao.*;
import com.jiayi.platform.basic.dto.DeviceTypeInfo;
import com.jiayi.platform.basic.entity.Device;
import com.jiayi.platform.basic.entity.DeviceSubType;
import com.jiayi.platform.basic.entity.DeviceTimeStatistic;
import com.jiayi.platform.basic.entity.DeviceTimeStreamStatistic;
import com.jiayi.platform.basic.request.DeviceMapRequest;
import com.jiayi.platform.basic.vo.DeviceVo;
import com.jiayi.platform.common.util.MyDateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DeviceMapService {

    @Autowired
    private DeviceDao deviceDao;
    @Autowired
    private DeviceSubTypeDao deviceSubTypeDao;
    @Autowired
    private DeviceTypeDao deviceTypeDao;
    @Autowired
    private DeviceTimeStreamStatisticDao deviceTimeStreamStatisticDao;
    @Autowired
    private DeviceTimeStatisticDao deviceTimeStatisticDao;
    @Value("${device.online.time:3600}")
    private Long onlineTime;//秒
    @Value("${device.type.id:3}")
    private Integer deviceTypeId;

    public List<Device> getDeviceByDeviceType(Integer type){
        List<DeviceSubType> deviceSubTypes = deviceSubTypeDao.findAll();
        Map<Integer,List<DeviceSubType>> deviceSubTypeMap = deviceSubTypes.stream()
                .collect(Collectors.groupingBy(DeviceSubType::getDeviceType));
        List<DeviceSubType> selectDeviceSubTypes = deviceSubTypeMap.get(type);
        if(selectDeviceSubTypes==null){
            log.error("getDeviceByDeviceType出错啦！日志如下：设备大类type="+type);
            return  Lists.newArrayList();
        }
        List<Integer> types = Lists.newArrayList();
        selectDeviceSubTypes.forEach(a->{
            types.add(a.getId());
        });
        List<Device> devices = deviceDao.findByTypeIn(types);
        return devices;
    }

    // @TODO 确认是否是有用的代码，有用去掉魔术数字，没用则清除。
    public Object queryAllDevice(Long userId, Integer type) {
        Map<Integer, List<DeviceVo>> deviceAllMap = new HashMap<>();
        if (type == 0) {//获取全部的点
            deviceAllMap.put(1, queryDeviceByCollect(1));
            deviceAllMap.put(2, queryDeviceByCollect(2));
            deviceAllMap.put(3, queryDeviceByCollect(3));
            return deviceAllMap;
        } else {
            return queryDeviceByCollect(type);
        }
    }

    public List<DeviceVo> queryDeviceByCollect(Integer type) {
        long start = System.currentTimeMillis();
        List<Device> devices = deviceDao.findByType(type);
        log.info("redis-find-device耗时：" + (System.currentTimeMillis() - start) + "ms!");
        return getDeviceStatusInfo(devices);
    }

    public List<DeviceVo> queryByDeviceSubType(DeviceMapRequest request) {
        try {
            List<Integer> types = request.getTypes();
            List<Device> devices;
            if (CollectionUtils.isEmpty(types)) {
                devices = deviceDao.findAllValidDevices();
            } else {
                devices = deviceDao.findByTypeIn(types);
            }
            if (CollectionUtils.isEmpty(devices)) {
                return Collections.EMPTY_LIST;
            }
            List<DeviceVo> result = getDeviceStatusInfo(devices);
            if (CollectionUtils.isNotEmpty(request.getOnlineStatus()) && request.getOnlineStatus().size() == 1) {
                result = result.stream().filter(a -> a.getIsOnline() == request.getOnlineStatus().get(0)).collect(Collectors.toList());
            }
            if (request.getStartTime() != null && request.getEndTime() != null) {
                result = result.stream().filter(a -> a.getDataEndTime() >= request.getStartTime() && a.getDataEndTime() <= request.getEndTime()).collect(Collectors.toList());
            }
            return result;
        } catch (Exception e) {
            log.error("queryByDeviceSubType error", e);
            throw new RuntimeException("queryByDeviceSubType error", e);
        }
    }

    private List<DeviceVo> getDeviceStatusInfo(List<Device> devices) {
        long start = System.currentTimeMillis();
        List<DeviceTypeInfo> infos = deviceSubTypeDao.findAllSubNameAndMainName();
        Map<Integer, DeviceTypeInfo> typeMap = infos.stream().collect(Collectors.toMap(DeviceTypeInfo::getId, Function.identity()));
        List<DeviceVo> results = new ArrayList<>();
        Set<String> srcAndCodes = devices.stream().map(a -> a.getSrc() + "|" + a.getCode()).collect(Collectors.toSet());
        List<DeviceTimeStreamStatistic> deviceStatusInfos = deviceTimeStreamStatisticDao.findDeviceStatusInfo(srcAndCodes);//设备状态信息
        List<DeviceTimeStatistic> deviceTimeStatisticList = deviceTimeStatisticDao.findDeviceStatisticInfo(srcAndCodes);
        Map<String, DeviceTimeStreamStatistic> srcCodeMap = deviceStatusInfos.stream().collect(Collectors.toMap(DeviceTimeStreamStatistic::srcAndCode, Function.identity()));
        Map<String, DeviceTimeStatistic> deviceStatisticMap = deviceTimeStatisticList.stream().collect(Collectors.toMap(DeviceTimeStatistic::srcAndCode, Function.identity()));
        try {
            for (Device device : devices) {
                String key = device.getSrc() + "|" + device.getCode();
                DeviceTimeStreamStatistic deviceStatusInfo = srcCodeMap.get(key);
                DeviceTimeStatistic deviceTimeStatistic = deviceStatisticMap.get(key);
                Integer qualify;
                Long dataStartTime;
                Long dataEndTime;
                Long heartbeatTime;
                String earliest = null;
                String latest = null;
                if (deviceStatusInfo == null) {
                    qualify = 2;
                    dataStartTime = 0L;
                    dataEndTime = 0L;
                    heartbeatTime = 0L;
                } else {
                    if (null == deviceTimeStatistic || deviceTimeStatistic.getQualify() == null)
                        qualify = 2;
                    else
                        qualify = deviceTimeStatistic.getQualify();
                    dataStartTime = deviceStatusInfo.getDataStartTime() == null ? 0L : deviceStatusInfo.getDataStartTime();
                    dataEndTime = deviceStatusInfo.getDataEndTime() == null ? 0L : deviceStatusInfo.getDataEndTime();
                    heartbeatTime = deviceStatusInfo.getHeartbeatTime() == null ? 0L : deviceStatusInfo.getHeartbeatTime();
                }
                boolean isOnline = Math.abs(new Date().getTime() - heartbeatTime) < (onlineTime * 1000);
                if (dataStartTime != 0) {
                    earliest = MyDateUtil.getDateStr(dataStartTime);
                }
                if (dataEndTime != 0) {
                    latest = MyDateUtil.getDateStr(dataEndTime);
                }
//                String deviceCode = MacUtil.generateMac(device.getCode());
                DeviceVo deviceVo = new DeviceVo(device.getId(), device.getLongitude() / 1000000000000.0,
                        device.getLatitude() / 1000000000000.0, device.getPlaceId(), device.getName(),
                        isOnline ? 1 : 0, 0, qualify, earliest, latest, device.getCode(), device.getType());
                deviceVo.setAddress(device.getAddress());
                deviceVo.setDataEndTime(dataEndTime);
                deviceVo.setMac(device.getMac());
                DeviceTypeInfo info = typeMap.get(device.getType());
                if (info != null) {
                    deviceVo.setSubTypeName(info.getName());
                    deviceVo.setMainTypeName(info.getMainName());
                }
                results.add(deviceVo);
            }
            log.info("get devicestatusinfo cost:" + (System.currentTimeMillis() - start) + "ms!");
            return results;
        } catch (Exception e) {
            log.error("get devicestatusinfo error", e);
            throw new RuntimeException("get devicestatusinfo error", e);
        }
    }

    /**
     * 地图审计设备点
     */
    public List<DeviceVo> searchAuditDevices() {
        List<Device> devices = deviceDao.findByMainType(deviceTypeId);
        if(CollectionUtils.isEmpty(devices)){
            return Collections.EMPTY_LIST;
        }
        return getDeviceStatusInfo(devices);

//        List<DeviceVo> result = new ArrayList<>();
//        Set<String> srcAndCodes = devices.stream().map(a -> a.getSrc() + "|" + a.getCode()).collect(Collectors.toSet());
//        List<DeviceTimeStreamStatistic> deviceStatuses = deviceTimeStreamStatisticDao.findDeviceStatusInfo(srcAndCodes);
//        Map<String, DeviceTimeStreamStatistic> deviceStatusMap = deviceStatuses.stream().collect(Collectors.toMap(DeviceTimeStreamStatistic::srcAndCode, Function.identity()));
//        List<DeviceTimeStatistic> deviceTimeStatisticList = deviceTimeStatisticDao.findDeviceStatisticInfo(srcAndCodes);
//        Map<String, DeviceTimeStatistic> deviceStatisticMap = deviceTimeStatisticList.stream().collect(Collectors.toMap(DeviceTimeStatistic::srcAndCode, Function.identity()));
//        devices.forEach(a -> {
//            DeviceVo deviceVo = new DeviceVo();
//            deviceVo.setId(a.getId());
//            deviceVo.setPlaceId(a.getPlaceId());
//            deviceVo.setLongitude(a.getLongitude() == null ? 0 : a.getLongitude() / 1000000000000.0);
//            deviceVo.setLatitude(a.getLatitude() == null ? 0 : a.getLatitude() / 1000000000000.0);
//            deviceVo.setName(a.getName());
//            String key = a.getSrc() + "|" + a.getCode();
//            DeviceTimeStreamStatistic deviceStatusInfo = deviceStatusMap.get(key);
//            DeviceTimeStatistic deviceTimeStatistic = deviceStatisticMap.get(key);
//            int qualify;
//            long dataStartTime;
//            long dataEndTime;
//            long heartbeatTime;
//            String earliest = null;
//            String latest = null;
//            if (deviceStatusInfo == null) {
//                qualify = 2;
//                dataStartTime = 0L;
//                dataEndTime = 0L;
//                heartbeatTime = 0L;
//            } else {
//                if (null == deviceTimeStatistic || deviceTimeStatistic.getQualify() == null)
//                    qualify = 2;
//                else
//                    qualify = deviceTimeStatistic.getQualify();
//                dataStartTime = deviceStatusInfo.getDataStartTime() == null ? 0L : deviceStatusInfo.getDataStartTime();
//                dataEndTime = deviceStatusInfo.getDataEndTime() == null ? 0L : deviceStatusInfo.getDataEndTime();
//                heartbeatTime = deviceStatusInfo.getHeartbeatTime() == null ? 0L : deviceStatusInfo.getHeartbeatTime();
//            }
//            boolean isOnline = Math.abs(new Date().getTime() - heartbeatTime) < (onlineTime * 1000);
//            if (dataStartTime != 0) {
//                earliest = MyDateUtil.getDateStr(dataStartTime);
//            }
//            if (dataEndTime != 0) {
//                latest = MyDateUtil.getDateStr(dataEndTime);
//            }
//            deviceVo.setIsOnline(isOnline ? 1 : 0);
//            deviceVo.setIsActive(0);//TODO 没有活跃状态，暂时设置为0
//            deviceVo.setIsQulified(qualify);
//            deviceVo.setEarliest(earliest);
//            deviceVo.setLatest(latest);
//            deviceVo.setDeviceCode(a.getCode());//MacUtil.generateMac(a.getCode())
//            deviceVo.setMac(a.getMac());
//            result.add(deviceVo);
//        });
//        return result;
    }

}
