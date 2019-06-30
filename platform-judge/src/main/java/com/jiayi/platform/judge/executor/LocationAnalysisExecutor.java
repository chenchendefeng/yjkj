package com.jiayi.platform.judge.executor;

import com.jiayi.platform.basic.entity.Device;
import com.jiayi.platform.common.enums.CollectType;
import com.jiayi.platform.basic.service.DeviceService;
import com.jiayi.platform.common.exception.ArgumentException;
import com.jiayi.platform.common.util.LocationUtils;
import com.jiayi.platform.common.util.MyDateUtil;
import com.jiayi.platform.common.web.util.ExportUtil;
import com.jiayi.platform.judge.common.bean.PageInfo;
import com.jiayi.platform.judge.common.bean.PageRequest;
import com.jiayi.platform.judge.common.bean.PageResult;
import com.jiayi.platform.judge.dao.impala.LocationAnalysisDao;
import com.jiayi.platform.judge.dto.LocationAnalysisDto;
import com.jiayi.platform.judge.enums.AreaTypeEnum;
import com.jiayi.platform.judge.manage.ImpalaTableManager;
import com.jiayi.platform.judge.query.LocationAnalysisQuery;
import com.jiayi.platform.judge.request.Area;
import com.jiayi.platform.judge.request.JudgeRequest;
import com.jiayi.platform.judge.request.LocationAnalysisRequest;
import com.jiayi.platform.judge.response.LocationAnalysisResponse;
import com.jiayi.platform.judge.response.RuleAnalysisResult;
import com.jiayi.platform.judge.service.ExportService;
import com.jiayi.platform.judge.util.ImpalaDataUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class LocationAnalysisExecutor implements JudgeExecutor {

    @Autowired
    private LocationAnalysisDao locationAnalysisDao;

    @Autowired
    private ImpalaTableManager impalaTableManager;
    @Autowired
    private DeviceService deviceService;

    @Override
    public <T extends JudgeRequest> List<?> query(T request, PageRequest pageRequest) {
        LocationAnalysisQuery query = new LocationAnalysisQuery();
        buildLocationAnalysisQuery(query, (LocationAnalysisRequest) request);
        query.setLimit(pageRequest.getPageSize());
        query.setOffset(pageRequest.calOffset());
        List<LocationAnalysisDto> result = locationAnalysisDao.selectGlobalLocationAnalysis(query);
        result.addAll(locationAnalysisDao.selectLocationAnalysis(query));
        return result;
    }

    @Override
    public <T extends JudgeRequest> long count(T request) {
        LocationAnalysisQuery query = new LocationAnalysisQuery();
        buildLocationAnalysisQuery(query, (LocationAnalysisRequest) request);
        return locationAnalysisDao.countLocationAnalysis(query);
    }

    @Override
    public List<?> queryCache(Long queryHistoryId, PageRequest pageRequest) {
        // 规律分析暂无缓存
        return null;
    }

    @Override
    public long countCache(Long queryHistoryId) {
        // 规律分析暂无缓存
        return 0;
    }

    @Override
    public <T extends JudgeRequest> long cache(T request, long queryHistoryId) {
        // 规律分析暂无缓存
        return count(request);
    }

    @Override
    public <T extends JudgeRequest> PageResult convert2Response(List<?> dots, long count, T request, PageRequest pageRequest) {
        if (dots.isEmpty() || dots.get(0) == null) {
            RuleAnalysisResult result = new RuleAnalysisResult<>(new ArrayList<>(), new ArrayList<>());
            return new PageResult<>(result, new PageInfo(0, 0L, pageRequest));
        }
        List<LocationAnalysisResponse> resultList = new ArrayList<>();
        List<String> connections = new ArrayList<>();
        Map<String, Long> connectionMap = new HashMap<>();
        Map<Long, Integer> idGridCodeMap = new HashMap<>();
        for (int i = 1; i < dots.size(); i++) {
            idGridCodeMap.put(((LocationAnalysisDto) dots.get(i)).getResultGridCode(), i);
        }
        List<Device> devices = deviceService.findByCollectId(CollectType.valueOf(request.getObjectTypeName().toUpperCase()).code());
        int countTime = ((LocationAnalysisRequest) request).getCountTime(); // 统计方式（0:天 / 1:周 / 2:月）

        LocationAnalysisDto globalData = (LocationAnalysisDto) dots.get(0);
        LocationAnalysisResponse globalResponse = new LocationAnalysisResponse();
        globalResponse.setId(0);
        globalResponse.setAddress("全局设备");
        globalResponse.setGridCode(0L);
        Set<String> gridDeviceSet = new HashSet<>();
        String[] deviceList = globalData.getDevices().split(",");
        for (String gridDevice : deviceList) {
            if (!gridDevice.trim().isEmpty())
                gridDeviceSet.add(gridDevice.trim());
        }
        globalResponse.setNumberCount(globalData.getCountTotal());
        globalResponse.setDeviceCount(gridDeviceSet.size());
        switch (countTime) {
            case 0: globalResponse.setDailyTimeCount(globalData); break;
            case 1: globalResponse.setWeeklyTimeCount(globalData); break;
            case 2: globalResponse.setMonthlyTimeCount(globalData); break;
        }
        resultList.add(globalResponse); // 以上为全局统计

        // 分页数据
        for (int i = 1; i < dots.size(); i++) {
            LocationAnalysisResponse response = new LocationAnalysisResponse();
            LocationAnalysisDto item = (LocationAnalysisDto) dots.get(i);
            response.setId(i);
            response.setGridCode(item.getResultGridCode());
            // 找出离网格中心最近的设备
            double minDist = Double.MAX_VALUE;
            Device minDistDevice = null;
            for (Device device : devices) {
                if (device.getLongitude() == null || device.getLatitude() == null)
                    continue;
                double dLng = ImpalaDataUtil.convertLngAndLat2Double(device.getLongitude(), 1e12);
                double dLat = ImpalaDataUtil.convertLngAndLat2Double(device.getLatitude(), 1e12);
                if (item.getResultGridCode().equals(getDeviceGridByDensity(((LocationAnalysisRequest) request).getDensity(), dLng, dLat))) {
                    double dist = LocationUtils.distance(dLng, dLat, item.getAverageLongitude(), item.getAverageLatitude());
                    if (dist < minDist) {
                        minDistDevice = device;
                        minDist = dist;
                    }
                }
            }
            // 网格之间的关联关系
            String[] relations = item.getRelations().split(",");
            for (String relation : relations) {
                if (StringUtils.isBlank(relation))
                    continue;
                String[] gridCount = relation.split(":");
                long grid = convertGrid(((LocationAnalysisRequest) request).getDensity(), Long.parseLong(gridCount[0].trim()));
                long gCount = Long.parseLong(gridCount[1].trim());
                if (idGridCodeMap.containsKey(grid) && !idGridCodeMap.get(grid).equals(response.getId())) {
                    String key;
                    if (response.getId() < idGridCodeMap.get(grid))
                        key = response.getId().toString() + "," + idGridCodeMap.get(grid).toString();
                    else
                        key = idGridCodeMap.get(grid).toString() + "," + response.getId().toString();
                    if (connectionMap.containsKey(key))
                        connectionMap.put(key, connectionMap.get(key) + gCount);
                    else
                        connectionMap.put(key, gCount);
                }
            }
            String[] gridDevices = item.getDevices().split(",");
            Set<String> deviceSet = new HashSet<>();
            for (String gridDevice : gridDevices) {
                if (!gridDevice.trim().isEmpty())
                    deviceSet.add(gridDevice.trim());
            }

            if (minDistDevice != null)
                response.setAddress(minDistDevice.getAddress());
            response.setDeviceCount(deviceSet.size());
            response.setLongitude(item.getAverageLongitude());
            response.setLatitude(item.getAverageLatitude());
            response.setNumberCount(item.getCountTotal());
            switch (countTime) {
                case 0: response.setDailyTimeCount(item); break;
                case 1: response.setWeeklyTimeCount(item); break;
                case 2: response.setMonthlyTimeCount(item); break;
            }
            resultList.add(response);
        }
        for (Map.Entry<String, Long> entry : connectionMap.entrySet()) {
            connections.add(entry.getKey() + "," + entry.getValue().toString());
        }
        RuleAnalysisResult result = new RuleAnalysisResult<>(resultList, connections);
        PageInfo pageInfo = new PageInfo(dots.size() - 1, count, pageRequest);
        return new PageResult<>(result, pageInfo);
    }

    @Override
    public <T extends JudgeRequest> int exportResult(List<String> contents, T request, long offset, long queryId) {
        LocationAnalysisQuery query = new LocationAnalysisQuery();
        buildLocationAnalysisQuery(query, (LocationAnalysisRequest) request);
        query.setLimit(ExportService.LOAD_SIZE);
        query.setOffset(offset);

        int countTime = ((LocationAnalysisRequest) request).getCountTime(); // 统计方式（0:天 / 1:周 / 2:月）
        int colCount;
        switch (countTime) {
            case 0: colCount = 7; break;
            case 1: colCount = 10; break;
            case 2: colCount = 9; break;
            default:
                throw new ArgumentException("Unexpected value: " + countTime);
        }
        List<String[]> rowData = new ArrayList<>();
        if (offset == 0) {  // offset为0时，第一条为全局统计
            List<LocationAnalysisDto> globalList = locationAnalysisDao.selectGlobalLocationAnalysis(query);
            if (globalList.isEmpty() || globalList.get(0) == null) {
                return 0;
            }
            LocationAnalysisDto globalData = globalList.get(0);
            String[] rowValue = new String[colCount];
            int j = 0;
            rowValue[j++] = "0";
            Set<String> gridDeviceSet = new HashSet<>();
            String[] deviceList = globalData.getDevices().split(",");
            for (String gridDevice : deviceList) {
                if (!gridDevice.trim().isEmpty())
                    gridDeviceSet.add(gridDevice.trim());
            }
            rowValue[j++] = "全局设备(" + gridDeviceSet.size() + ")";
            rowValue[j++] = globalData.getCountTotal().toString();
            long countTotal = globalData.getCountTotal();
            switch (countTime) {
                case 0:
                    rowValue[j++] = globalData.countHour0To6() + "(" + Math.round(globalData.countHour0To6() / countTotal * 100) + "%)";
                    rowValue[j++] = globalData.countHour6To12() + "(" + Math.round(globalData.countHour6To12() / countTotal * 100) + "%)";
                    rowValue[j++] = globalData.countHour12To18() + "(" + Math.round(globalData.countHour12To18() / countTotal * 100) + "%)";
                    rowValue[j] = globalData.countHour18To24() + "(" + Math.round(globalData.countHour18To24() / countTotal * 100) + "%)";
                    break;
                case 1:
                    rowValue[j++] = globalData.getCount1() + "(" + Math.round(globalData.getCount1() / countTotal * 100) + "%)";
                    rowValue[j++] = globalData.getCount2() + "(" + Math.round(globalData.getCount2() / countTotal * 100) + "%)";
                    rowValue[j++] = globalData.getCount3() + "(" + Math.round(globalData.getCount3() / countTotal * 100) + "%)";
                    rowValue[j++] = globalData.getCount4() + "(" + Math.round(globalData.getCount4() / countTotal * 100) + "%)";
                    rowValue[j++] = globalData.getCount5() + "(" + Math.round(globalData.getCount5() / countTotal * 100) + "%)";
                    rowValue[j++] = globalData.getCount6() + "(" + Math.round(globalData.getCount6() / countTotal * 100) + "%)";
                    rowValue[j] = globalData.getCount0() + "(" + Math.round(globalData.getCount0() / countTotal * 100) + "%)";
                    break;
                case 2:
                    rowValue[j++] = globalData.countDay1To5() + "(" + Math.round(globalData.countDay1To5() / countTotal * 100) + "%)";
                    rowValue[j++] = globalData.countDay6To10() + "(" + Math.round(globalData.countDay6To10() / countTotal * 100) + "%)";
                    rowValue[j++] = globalData.countDay11To15() + "(" + Math.round(globalData.countDay11To15() / countTotal * 100) + "%)";
                    rowValue[j++] = globalData.countDay16To20() + "(" + Math.round(globalData.countDay16To20() / countTotal * 100) + "%)";
                    rowValue[j++] = globalData.countDay21To25() + "(" + Math.round(globalData.countDay21To25() / countTotal * 100) + "%)";
                    rowValue[j] = globalData.countDay26To31() + "(" + Math.round(globalData.countDay26To31() / countTotal * 100) + "%)";
                    break;
            }
            rowData.add(rowValue);
        }
        // 分页数据
        List<LocationAnalysisDto> data = locationAnalysisDao.selectLocationAnalysis(query);
        List<Device> devices = deviceService.findByCollectId(CollectType.valueOf(request.getObjectTypeName().toUpperCase()).code());
        for (int i = 0; i < data.size(); i++) {
            LocationAnalysisDto datum = data.get(i);
            String[] rowValue = new String[colCount];
            int j = 0;
            rowValue[j++] = String.valueOf(i + 1 + offset);
            // 找出离网格中心最近的设备
            double minDist = Double.MAX_VALUE;
            Device minDistDevice = null;
            for (Device device : devices) {
                if (device.getLongitude() == null || device.getLatitude() == null)
                    continue;
                double dLng = ImpalaDataUtil.convertLngAndLat2Double(device.getLongitude(), 1e12);
                double dLat = ImpalaDataUtil.convertLngAndLat2Double(device.getLatitude(), 1e12);
                if (datum.getResultGridCode().equals(getDeviceGridByDensity(((LocationAnalysisRequest) request).getDensity(), dLng, dLat))) {
                    double dist = LocationUtils.distance(dLng, dLat, datum.getAverageLongitude(), datum.getAverageLatitude());
                    if (dist < minDist) {
                        minDistDevice = device;
                        minDist = dist;
                    }
                }
            }
            String[] gridDevices = datum.getDevices().split(",");
            Set<String> deviceSet = new HashSet<>();
            for (String gridDevice : gridDevices) {
                if (!gridDevice.trim().isEmpty())
                    deviceSet.add(gridDevice.trim());
            }
            String address;
            if (minDistDevice != null)
                address = minDistDevice.getAddress();
            else
                address = "(无地址)";
            rowValue[j++] = address + "(" + deviceSet.size() + ")";
            rowValue[j++] = datum.getCountTotal().toString();
            long countTotal = datum.getCountTotal();
            switch (countTime) {
                case 0:
                    rowValue[j++] = datum.countHour0To6() + "(" + Math.round(datum.countHour0To6() / countTotal * 100) + "%)";
                    rowValue[j++] = datum.countHour6To12() + "(" + Math.round(datum.countHour6To12() / countTotal * 100) + "%)";
                    rowValue[j++] = datum.countHour12To18() + "(" + Math.round(datum.countHour12To18() / countTotal * 100) + "%)";
                    rowValue[j] = datum.countHour18To24() + "(" + Math.round(datum.countHour18To24() / countTotal * 100) + "%)";
                    break;
                case 1:
                    rowValue[j++] = datum.getCount1() + "(" + Math.round(datum.getCount1() / countTotal * 100) + "%)";
                    rowValue[j++] = datum.getCount2() + "(" + Math.round(datum.getCount2() / countTotal * 100) + "%)";
                    rowValue[j++] = datum.getCount3() + "(" + Math.round(datum.getCount3() / countTotal * 100) + "%)";
                    rowValue[j++] = datum.getCount4() + "(" + Math.round(datum.getCount4() / countTotal * 100) + "%)";
                    rowValue[j++] = datum.getCount5() + "(" + Math.round(datum.getCount5() / countTotal * 100) + "%)";
                    rowValue[j++] = datum.getCount6() + "(" + Math.round(datum.getCount6() / countTotal * 100) + "%)";
                    rowValue[j] = datum.getCount0() + "(" + Math.round(datum.getCount0() / countTotal * 100) + "%)";
                    break;
                case 2:
                    rowValue[j++] = datum.countDay1To5() + "(" + Math.round(datum.countDay1To5() / countTotal * 100) + "%)";
                    rowValue[j++] = datum.countDay6To10() + "(" + Math.round(datum.countDay6To10() / countTotal * 100) + "%)";
                    rowValue[j++] = datum.countDay11To15() + "(" + Math.round(datum.countDay11To15() / countTotal * 100) + "%)";
                    rowValue[j++] = datum.countDay16To20() + "(" + Math.round(datum.countDay16To20() / countTotal * 100) + "%)";
                    rowValue[j++] = datum.countDay21To25() + "(" + Math.round(datum.countDay21To25() / countTotal * 100) + "%)";
                    rowValue[j] = datum.countDay26To31() + "(" + Math.round(datum.countDay26To31() / countTotal * 100) + "%)";
                    break;
            }
            rowData.add(rowValue);
        }
        contents.add(ExportUtil.genContentByStringList(rowData));
        return data.size();
    }

    private void buildLocationAnalysisQuery(LocationAnalysisQuery query, LocationAnalysisRequest request) {
        try {
            if (request.getCountTime() == 1) {
                int dayWeek = MyDateUtil.getWeekday(request.getBeginDate());
                long sunday = request.getBeginDate() - (request.getBeginDate() + 60 * 60 * 8 * 1000) % (1000 * 60 * 60 * 24)
                        - 1000 * 60 * 60 * 24 * dayWeek;
                query.setOrigin(sunday); // 周日零点为一周的起点
            }
            query.setGridCodeList(getGridCodeList(request.getAreaList(), request.getDensity()));
//            query.setObjectTypeName(request.getObjectTypeName());
            query.setObjectType(CollectType.valueOf(request.getObjectTypeName().toUpperCase()).code());
            query.setObjectValue(request.getObjectValue().toUpperCase().replaceAll("[-:：\\s]", ""));
            query.setBeginDate(request.getBeginDate());
            query.setEndDate(request.getEndDate());
            query.setDensity(request.getDensity());
            query.setCountTime(request.getCountTime());
        } catch (Exception e) {
            log.error("location analysis request convert error, request is {}", request, e);
            throw new ArgumentException("location analysis request convert error", e);
        }
    }

    private Set<Long> getGridCodeList (List<Area> list, Integer density) {
        Set<Long> gridCodeList = new HashSet<>();
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        list.forEach(area -> {
            if (area.getUsed() && AreaTypeEnum.RECT.name().equalsIgnoreCase(area.getType())) {
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
                int minLatGrid = (int) (minLat * 1000);
                int minLngGrid = (int) (minLng * 1000);
                int maxLatGrid = (int) (maxLat * 1000);
                int maxLngGrid = (int) (maxLng * 1000);
                // 密度1
                if (density == 1) {
                    minLngGrid = minLngGrid / 5 * 5;
                    minLatGrid = minLatGrid / 5 * 5;
                    maxLngGrid = maxLngGrid / 5 * 5;
                    maxLatGrid = maxLatGrid / 5 * 5;
                }
                // 密度2
                if (density == 2) {
                    minLngGrid = minLngGrid / 10 * 10;
                    minLatGrid = minLatGrid / 10 * 10;
                    maxLngGrid = maxLngGrid / 10 * 10;
                    maxLatGrid = maxLatGrid / 10 * 10;
                }
                // 密度3
                if (density == 3) {
                    minLngGrid = minLngGrid / 20 * 20;
                    minLatGrid = minLatGrid / 20 * 20;
                    maxLngGrid = maxLngGrid / 20 * 20;
                    maxLatGrid = maxLatGrid / 20 * 20;
                }
                for (int lng = minLngGrid; lng <= maxLngGrid; lng = lng + 5) {
                    for (int lat = minLatGrid; lat <= maxLatGrid; lat = lat + 5) {
                        gridCodeList.add(lat / 5 * 100000L + lng / 5);
                    }
                }
            }
        });
        return gridCodeList;
    }

    private Long getDeviceGridByDensity(Integer density, double dLng, double dLat) {
        long deviceLng = 0L;
        long deviceLat = 0L;
        switch (density) {
            case 1:
                deviceLng = ((long) (dLng * 1000)) / 5 * 5;
                deviceLat = ((long) (dLat * 1000)) / 5 * 5;
                break;
            case 2:
                deviceLng = ((long) (dLng * 1000)) / 10 * 10;
                deviceLat = ((long) (dLat * 1000)) / 10 * 10;
                break;
            case 3:
                deviceLng = ((long) (dLng * 1000) / 20) * 20;
                deviceLat = ((long) (dLat * 1000) / 20) * 20;
                break;

            default:
                break;
        }
        return deviceLat / 5 * 100000L + deviceLng / 5;
    }

    private long convertGrid(Integer density, long gridCode) {
        if (1 == density)
            return gridCode;
        if (2 == density)
            return (gridCode / 100000 / 2 * 2) * 100000 + (gridCode % 100000) / 2 * 2;
        if (3 == density)
            return (gridCode / 100000 / 4 * 4) * 100000 + (gridCode % 100000) / 4 * 4;

        throw new ArgumentException("invalid density: " + density);
    }
}
