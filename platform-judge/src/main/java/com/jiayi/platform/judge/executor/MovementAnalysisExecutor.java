package com.jiayi.platform.judge.executor;

import com.jiayi.platform.basic.entity.Device;
import com.jiayi.platform.basic.service.DeviceService;
import com.jiayi.platform.common.enums.CollectType;
import com.jiayi.platform.common.exception.ArgumentException;
import com.jiayi.platform.common.util.LocationUtils;
import com.jiayi.platform.common.web.util.ExportUtil;
import com.jiayi.platform.judge.common.bean.PageInfo;
import com.jiayi.platform.judge.common.bean.PageRequest;
import com.jiayi.platform.judge.common.bean.PageResult;
import com.jiayi.platform.judge.dao.impala.MovementAnalysisDao;
import com.jiayi.platform.judge.dto.MovementAnalysisDto;
import com.jiayi.platform.judge.enums.AreaTypeEnum;
import com.jiayi.platform.judge.manage.ImpalaTableManager;
import com.jiayi.platform.judge.query.MovementAnalysisQuery;
import com.jiayi.platform.judge.request.Area;
import com.jiayi.platform.judge.request.JudgeRequest;
import com.jiayi.platform.judge.request.MovementAnalysisRequest;
import com.jiayi.platform.judge.response.MovementAnalysisResponse;
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
public class MovementAnalysisExecutor implements JudgeExecutor {

    @Autowired
    private MovementAnalysisDao movementAnalysisDao;

    @Autowired
    private ImpalaTableManager impalaTableManager;
    @Autowired
    private DeviceService deviceService;

    @Override
    public <T extends JudgeRequest> List<?> query(T request, PageRequest pageRequest) {
        MovementAnalysisQuery query = new MovementAnalysisQuery();
        buildMovementAnalysisQuery(query, (MovementAnalysisRequest) request);
        query.setLimit(pageRequest.getPageSize());
        query.setOffset(pageRequest.calOffset());
        return movementAnalysisDao.selectMovementAnalysis(query);
    }

    @Override
    public <T extends JudgeRequest> long count(T request) {
        MovementAnalysisQuery query = new MovementAnalysisQuery();
        buildMovementAnalysisQuery(query, (MovementAnalysisRequest) request);
        return movementAnalysisDao.countMovementAnalysis(query);
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
        List<MovementAnalysisResponse> resultList = new ArrayList<>();
        List<String> connections = new ArrayList<>();
        Map<String, Long> connectionMap = new HashMap<>();
        Map<Long, Integer> idGridCodeMap = new HashMap<>();
        for (int i = 0; i < dots.size(); i++) {
            idGridCodeMap.put(((MovementAnalysisDto) dots.get(i)).getGridCode(), i + 1);
        }
        List<Device> devices = deviceService.findByCollectId(CollectType.valueOf(request.getObjectTypeName().toUpperCase()).code());
        dots.forEach(dto -> {
            MovementAnalysisDto item = (MovementAnalysisDto) dto;
            MovementAnalysisResponse response = new MovementAnalysisResponse();
            response.setId(idGridCodeMap.get(item.getGridCode()));
            response.setGridCode(item.getGridCode());
            // 找出离网格中心最近的设备
            double minDist = Double.MAX_VALUE;
            Device minDistDevice = null;
            for (Device device : devices) {
                if (device.getLongitude() == null || device.getLatitude() == null)
                    continue;
                double dLng = ImpalaDataUtil.convertLngAndLat2Double(device.getLongitude(), 1e12);
                double dLat = ImpalaDataUtil.convertLngAndLat2Double(device.getLatitude(), 1e12);
                if ((long) (dLat * 100L) * 100000L + (long) (dLng * 100L) == item.getGridCode()) {
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
                long grid = Long.parseLong(gridCount[0].trim());
                long gCount = Long.parseLong(gridCount[1].trim());
                if (idGridCodeMap.containsKey(grid)) {
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
            Set<String> gridDeviceSet = new HashSet<>();
            for (String gridDevice : gridDevices) {
                if (!gridDevice.trim().isEmpty())
                    gridDeviceSet.add(gridDevice.trim());
            }

            if (minDistDevice != null)
                response.setAddress(minDistDevice.getAddress());
            response.setDeviceCount(gridDeviceSet.size());
            response.setAvgStayTime(item.getAverageStayTime().longValue());
            response.setBeginDate(item.getMinHappenAt());
            response.setEndDate(item.getMaxHappenAt());
            response.setLongitude(item.getAverageLongitude());
            response.setLatitude(item.getAverageLatitude());
            response.setEnterAndLeaveCount(item);
            response.setStayTime(item);
            resultList.add(response);
        });
        for (Map.Entry<String, Long> entry : connectionMap.entrySet()) {
            connections.add(entry.getKey() + "," + entry.getValue().toString());
        }
        RuleAnalysisResult result = new RuleAnalysisResult<>(resultList, connections);
        PageInfo pageInfo = new PageInfo(dots.size(), count, pageRequest);
        return new PageResult<>(result, pageInfo);
    }

    @Override
    public <T extends JudgeRequest> int exportResult(List<String> contents, T request, long offset, long queryId) {
        MovementAnalysisQuery query = new MovementAnalysisQuery();
        buildMovementAnalysisQuery(query, (MovementAnalysisRequest) request);
        query.setLimit(ExportService.LOAD_SIZE);
        query.setOffset(offset);
        List<MovementAnalysisDto> data = (movementAnalysisDao.selectMovementAnalysis(query));
        List<Device> devices = deviceService.findByCollectId(CollectType.valueOf(request.getObjectTypeName().toUpperCase()).code());
        int colCount = 8;
        List<String[]> rowData = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            MovementAnalysisDto datum = data.get(i);
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
                if ((long) (dLat * 100L) * 100000L + (long) (dLng * 100L) == datum.getGridCode()) {
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
            rowValue[j++] = String.valueOf(Math.round(datum.getAverageStayTime() / 3600000 * 100) / 100.0);
            rowValue[j++] = ExportService.SDF.format(datum.getMinHappenAt());
            rowValue[j++] = ExportService.SDF.format(datum.getMaxHappenAt());
            rowValue[j++] = datum.getTop3EnterTime();
            rowValue[j++] = datum.getTop3LeaveTime();
            rowValue[j] = datum.getAllStayTime();
            rowData.add(rowValue);
        }
        contents.add(ExportUtil.genContentByStringList(rowData));
        return data.size();
    }

    private void buildMovementAnalysisQuery(MovementAnalysisQuery query, MovementAnalysisRequest request) {
        try {
//            query.setObjectTypeName(request.getObjectTypeName());
            query.setObjectType(CollectType.valueOf(request.getObjectTypeName().toUpperCase()).code());
            query.setObjectValue(request.getObjectValue().toUpperCase().replaceAll("[-:：\\s]", ""));
            query.setBeginDate(request.getBeginDate());
            query.setEndDate(request.getEndDate());
            long days = (request.getEndDate() - request.getBeginDate()) / (1000 * 60 * 60 * 24);
            query.setTotalDays(days);
            query.setGridCodeList(getGridCodeList(request.getAreaList()));
        } catch (Exception e) {
            log.error("movement analysis request convert error, request is {}", request, e);
            throw new ArgumentException("movement analysis request convert error", e);
        }
    }

    private Set<Long> getGridCodeList (List<Area> list) {
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
                int minLatGrid = (int) (minLat * 100);
                int minLngGrid = (int) (minLng * 100);
                int maxLatGrid = (int) (maxLat * 100);
                int maxLngGrid = (int) (maxLng * 100);
                for (int lng = minLngGrid; lng <= maxLngGrid; lng++) {
                    for (int lat = minLatGrid; lat <= maxLatGrid; lat++) {
                        gridCodeList.add(lat * 100000L + lng);
                    }
                }
            }
        });
        return gridCodeList;
    }
}
