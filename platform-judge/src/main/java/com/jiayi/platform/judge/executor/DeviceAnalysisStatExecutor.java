package com.jiayi.platform.judge.executor;

import com.jiayi.platform.basic.entity.Device;
import com.jiayi.platform.basic.service.DeviceService;
import com.jiayi.platform.common.enums.CollectType;
import com.jiayi.platform.common.exception.ArgumentException;
import com.jiayi.platform.judge.dao.impala.DeviceAnalysisDao;
import com.jiayi.platform.judge.dto.DeviceAnalysisStatDto;
import com.jiayi.platform.judge.manage.ImpalaTableManager;
import com.jiayi.platform.judge.query.DeviceAnalysisStatQuery;
import com.jiayi.platform.judge.request.DeviceAnalysisStatRequest;
import com.jiayi.platform.judge.response.DeviceAnalysisStatlResponse;
import com.jiayi.platform.judge.util.DeviceUtil;
import com.jiayi.platform.judge.util.ImpalaDataUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class DeviceAnalysisStatExecutor {
    @Autowired
    private DeviceAnalysisDao deviceAnalysisDao;

    @Autowired
    private ImpalaTableManager impalaTableManager;
    @Autowired
    private DeviceService deviceService;

    public List<DeviceAnalysisStatDto> query(DeviceAnalysisStatRequest request) {
        DeviceAnalysisStatQuery query = new DeviceAnalysisStatQuery();
        buildDeviceAnalysisDetailQuery(query, request);
        return deviceAnalysisDao.selectDeviceAnalysisStat(query);
    }

    public DeviceAnalysisStatlResponse convert2Response(List<DeviceAnalysisStatDto> dots, String statType) {
        DeviceAnalysisStatlResponse response = new DeviceAnalysisStatlResponse();
        switch (statType) {
            case "HOUR":
                response.setMinKey(0);
                response.setMaxKey(23);
                break;
            case "DAY":
                // 适配前端组件，将key值都减1
                dots.forEach(dto -> dto.setKey(dto.getKey() - 1));
                response.setMinKey(0);
                response.setMaxKey(dots.get(dots.size() - 1).getKey());
                break;
            case "WEEK":
                response.setMinKey(0);
                response.setMaxKey(6);
                break;
        }
        response.setData(dots);
        response.setType(statType);

        return response;
    }

    private void buildDeviceAnalysisDetailQuery(DeviceAnalysisStatQuery query, DeviceAnalysisStatRequest request) {
        try {
            List<Device> devices = deviceService.findByCollectId(CollectType.valueOf(request.getObjectTypeName().toUpperCase()).code());
            Set<Long> deviceSet = DeviceUtil.selectDeviceIdInAreasByType(request.getAreaList(), devices);
            query.setDeviceIdList(deviceSet);
            query.setObjectValue(request.getObjectId().toUpperCase().replaceAll("[-:：\\s]", ""));
            query.setObjectHash(ImpalaDataUtil.getObjectHash(query.getObjectValue()));
            Map<String, Pair<Long, Long>> tableNamesAndTime =
                    impalaTableManager.getValidTableList("query", request.getBeginDate(), request.getEndDate());
            query.setTableNameList(tableNamesAndTime.keySet());
            // 设置临时表和查询表的查询时间及网格
            for (Map.Entry<String, Pair<Long, Long>> namesAndTime : tableNamesAndTime.entrySet()) {
                long beginDate = namesAndTime.getValue().getLeft();
                long endDate = namesAndTime.getValue().getRight();
                if (namesAndTime.getKey().contains("recent")) {
                    query.setRecentBeginDate(beginDate);
                    query.setRecentEndDate(endDate);
                    query.setRecentBeginHours(beginDate / (ImpalaTableManager.QUERY_SPLIT_HOURS * 60 * 60 * 1000L));
                    query.setRecentEndHours(endDate / (ImpalaTableManager.QUERY_SPLIT_HOURS * 60 * 60 * 1000L));
                } else {
                    query.setBeginDate(beginDate);
                    query.setEndDate(endDate);
                    query.setBeginHours(beginDate / (ImpalaTableManager.QUERY_SPLIT_HOURS * 60 * 60 * 1000L));
                    query.setEndHours(endDate / (ImpalaTableManager.QUERY_SPLIT_HOURS * 60 * 60 * 1000L));
                }
            }
            query.setTrackType(CollectType.valueOf(request.getObjectTypeName().toUpperCase()).code());
            query.setStatType(request.getStatType().toUpperCase());
        } catch (ArgumentException e) {
            throw new ArgumentException(e.getMessage(), e);
        } catch (Exception e) {
            log.error("device analysis stat request convert error, request is {}", request, e);
            throw new ArgumentException("device analysis stat request convert error", e);
        }
    }
}
