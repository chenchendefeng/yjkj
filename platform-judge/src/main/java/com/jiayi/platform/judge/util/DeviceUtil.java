package com.jiayi.platform.judge.util;

import com.jiayi.platform.basic.entity.Device;
import com.jiayi.platform.common.exception.ArgumentException;
import com.jiayi.platform.common.exception.DBException;
import com.jiayi.platform.common.util.LocationUtils;
import com.jiayi.platform.judge.enums.AreaTypeEnum;
import com.jiayi.platform.judge.query.BaseAreaQuery;
import com.jiayi.platform.judge.query.LineQuery;
import com.jiayi.platform.judge.query.RectQuery;
import com.jiayi.platform.judge.request.Area;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @author : weichengke
 * @date : 2019-04-20 17:23
 */
@Slf4j
public class DeviceUtil {

    public static Set<Long> selectDeviceIdInAreasByType(List<Area> areaList, List<Device> devices) {
        if (areaList != null && areaList.size() > 0) {
            Map<String, BaseAreaQuery> areaQueryMap = bulidAreaQueryMap(areaList);
            long start = System.currentTimeMillis();
            try {
                log.debug("fetch all device location using: " + (System.currentTimeMillis() - start) + "ms.");
                Set<Long> deviceIdSet = new HashSet<>();
                for (BaseAreaQuery query : areaQueryMap.values()) {
                    if (query.getType().equals("RECT")) {
                        Set<Long> result = deviceIdsInRect((RectQuery) query, devices);
                        deviceIdSet.addAll(result);
                    }
                    if (query.getType().equals("LINE")) {
                        Set<Long> result = deviceIdsInLine((LineQuery) query, devices);
                        deviceIdSet.addAll(result);
                    }
                }
                log.debug("selected device num: " + deviceIdSet.size());
                return deviceIdSet;
            } catch (Exception e) {
                log.error("device impala search error", e);
                throw new DBException("device impala search error", e);
            }
        }
        return null;
    }

    private static Map<String, BaseAreaQuery> bulidAreaQueryMap(List<Area> areaList) {
        Map<String, BaseAreaQuery> areaQueryMap = new HashMap<>();
        areaList.forEach(area -> {
            if (area.getUsed()) {
                if (AreaTypeEnum.RECT.name().equalsIgnoreCase(area.getType())) {
                    if (area.getPoints().size() != 4) {
                        throw new ArgumentException("rectangle point arguments number error");
                    }
                    double minLat = area.getPoints().get(0).getLatitude();
                    double minLng = area.getPoints().get(0).getLongitude();
                    double maxLat = area.getPoints().get(0).getLatitude();
                    double maxLng = area.getPoints().get(0).getLongitude();
                    for (int i = 1; i < 4; i++) {
                        minLat = Math.min(minLat, area.getPoints().get(i).getLatitude());
                        minLng = Math.min(minLng, area.getPoints().get(i).getLongitude());
                        maxLat = Math.max(maxLat, area.getPoints().get(i).getLatitude());
                        maxLng = Math.max(maxLng, area.getPoints().get(i).getLongitude());
                    }
                    RectQuery rectQuery = new RectQuery(minLat, maxLat, minLng, maxLng);
                    areaQueryMap.put(area.getName(), rectQuery);
                } else if (AreaTypeEnum.LINE.name().equalsIgnoreCase(area.getType())) {
                    LineQuery lineQuery = new LineQuery(area.getPoints());
                    areaQueryMap.put(area.getName(), lineQuery);
                } else {
                    log.warn("area type: " + area.getType() + " not supported");
                }
            }
        });

        return areaQueryMap;
    }

    private static Set<Long> deviceIdsInRect(RectQuery query, List<Device> deviceLocations) {
        Set<Long> deviceIdSet = new HashSet<>();
        for (Device location : deviceLocations) {
            if (location.getLatitude() != null && location.getLongitude() != null) {
                double lat = ImpalaDataUtil.convertLngAndLat2Double(location.getLatitude(), 1e12);
                double lng = ImpalaDataUtil.convertLngAndLat2Double(location.getLongitude(), 1e12);
                if (lat >= query.getMinLat() && lat <= query.getMaxLat() && lng >= query.getMinLng()
                        && lng <= query.getMaxLng()) {
                    deviceIdSet.add(location.getId());
                }
            }
        }
        return deviceIdSet;
    }

    private static Set<Long> deviceIdsInLine(LineQuery query, List<Device> deviceLocations) {
        Set<Long> deviceIdSet = new HashSet<>();
        for (Device location : deviceLocations) {
            if (location.getLatitude() != null && location.getLongitude() != null) {
                double lat = ImpalaDataUtil.convertLngAndLat2Double(location.getLatitude(), 1e12);
                double lng = ImpalaDataUtil.convertLngAndLat2Double(location.getLongitude(), 1e12);
                for (int i = 0; i < query.getPoints().size() - 1; i++) {
                    double lat1 = query.getPoints().get(i).getLatitude();
                    double lng1 = query.getPoints().get(i).getLongitude();
                    double lat2 = query.getPoints().get(i + 1).getLatitude();
                    double lng2 = query.getPoints().get(i + 1).getLongitude();
                    double cross = (lng2 - lng1) * (lng - lng1) + (lat2 - lat1) * (lat - lat1);
                    double d2 = (lng2 - lng1) * (lng2 - lng1) + (lat2 - lat1) * (lat2 - lat1);
                    double distance;
                    if (cross <= 0) {
                        distance = LocationUtils.distance(lng, lat, lng1, lat1);
                    } else if (cross >= d2) {
                        distance = LocationUtils.distance(lng, lat, lng2, lat2);
                    } else {
                        double r = cross / d2;
                        double pLng = lng1 + (lng2 - lng1) * r;
                        double pLat = lat1 + (lat2 - lat1) * r;
                        distance = LocationUtils.distance(lng, lat, pLng, pLat);
                    }
                    if (distance <= LineQuery.DISTANCE) {
                        deviceIdSet.add(location.getId());
                        break;
                    }
                }
            }
        }
        return deviceIdSet;
    }

    public static Set<Long> getGridSetFromDevices(Set<Long> deviceIds, List<Device> devices) {
        if (deviceIds != null && deviceIds.size() > 0) {
            Set<Long> gridSet = new HashSet<>();
            for (Long deviceId : deviceIds) {
                for (Device device : devices) {
                    if (device.getId().equals(deviceId)) {
                        gridSet.add(genGrid(device.getLongitude(), device.getLatitude()));
                        break;
                    }
                }
            }
            return gridSet;
        }
        return null;
    }

    private static Long genGrid(long longitude, long latitude) {
        int splitDeviceGrid = 1000;
        return longitude / splitDeviceGrid / 10000000 * 100000 + latitude / splitDeviceGrid / 10000000;
    }

}
