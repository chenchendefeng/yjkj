package com.jiayi.platform.judge.executor;

import com.jiayi.platform.basic.entity.Device;
import com.jiayi.platform.basic.service.DeviceService;
import com.jiayi.platform.common.enums.CollectType;
import com.jiayi.platform.common.exception.ArgumentException;
import com.jiayi.platform.common.web.util.ExportUtil;
import com.jiayi.platform.judge.common.bean.PageRequest;
import com.jiayi.platform.judge.dao.impala.DeviceAnalysisDao;
import com.jiayi.platform.judge.dto.DeviceAnalysisDetailDto;
import com.jiayi.platform.judge.dto.TrackDetailDto;
import com.jiayi.platform.judge.manage.ImpalaTableManager;
import com.jiayi.platform.judge.query.DeviceAnalysisDetailQuery;
import com.jiayi.platform.judge.request.DeviceAnalysisDetailRequest;
import com.jiayi.platform.judge.request.JudgeDetailRequest;
import com.jiayi.platform.judge.response.DeviceAnalysisDetailResponse;
import com.jiayi.platform.judge.service.ExportService;
import com.jiayi.platform.judge.util.DeviceUtil;
import com.jiayi.platform.judge.util.ImpalaDataUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DeviceAnalysisDetailExecutor implements JudgeDetailExecutor {
    @Autowired
    private DeviceAnalysisDao deviceAnalysisDao;

    @Autowired
    private ImpalaTableManager impalaTableManager;
    @Autowired
    private DeviceService deviceService;

    @Override
    public <T> List<?> query(T request, PageRequest pageRequest) {
        DeviceAnalysisDetailQuery query = new DeviceAnalysisDetailQuery();
        buildDeviceAnalysisDetailQuery(query, (DeviceAnalysisDetailRequest) request);
        query.setLimit(pageRequest.getPageSize());
        query.setOffset(pageRequest.calOffset());
        return deviceAnalysisDao.selectDeviceAnalysisDetail(query);
    }

    @Override
    public <T> long count(T request) {
        DeviceAnalysisDetailQuery query = new DeviceAnalysisDetailQuery();
        buildDeviceAnalysisDetailQuery(query, (DeviceAnalysisDetailRequest) request);
        return deviceAnalysisDao.countDeviceAnalysisDetail(query);
    }

    @Override
    public List<?> convert2Response(List<?> dots, String objectType) {
        List<DeviceAnalysisDetailResponse> responseList = new ArrayList<>();
        Map<Long, Device> deviceMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(dots)) {
            Set<Long> deviceIds = dots.stream().map(dto -> ((DeviceAnalysisDetailDto) dto).getDeviceId()).filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            deviceMap.putAll(deviceService.findByIds(deviceIds));
        }
        dots.forEach(dto -> {
            DeviceAnalysisDetailDto item = (DeviceAnalysisDetailDto) dto;
            DeviceAnalysisDetailResponse response = new DeviceAnalysisDetailResponse();
            response.setRecordAt(item.getRecordAt());
            if (item.getDeviceId() != null && deviceMap.get(item.getDeviceId()) != null) {
                response.setAddress(deviceMap.get(item.getDeviceId()).getAddress());
            }

            responseList.add(response);
        });
        return responseList;
    }

    @Override
    public <T extends JudgeDetailRequest> int exportResult(List<String> contents, T request, long offset) {
        DeviceAnalysisDetailQuery query = new DeviceAnalysisDetailQuery();
        buildDeviceAnalysisDetailQuery(query, (DeviceAnalysisDetailRequest) request);
        query.setLimit(ExportService.LOAD_SIZE);
        query.setOffset(offset);
        List<DeviceAnalysisDetailDto> data = deviceAnalysisDao.selectDeviceAnalysisDetail(query);

        Map<Long, Device> deviceMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(data)) {
            Set<Long> deviceIds = data.stream().map(DeviceAnalysisDetailDto::getDeviceId).filter(Objects::nonNull).collect(Collectors.toSet());
            deviceMap.putAll(deviceService.findByIds(deviceIds));
        }
        int colCount = 2;
        List<String[]> rowData = new ArrayList<>();
        for (DeviceAnalysisDetailDto datum : data) {
            String[] rowValue = new String[colCount];
            int j = 0;
            rowValue[j++] = ExportService.SDF.format(datum.getRecordAt());
            rowValue[j] = (datum.getDeviceId() != null && deviceMap.get(datum.getDeviceId()) != null)
                    ? deviceMap.get(datum.getDeviceId()).getAddress()
                    : "";
            rowData.add(rowValue);
        }
        contents.add(ExportUtil.genContentByStringList(rowData));
        return data.size();
    }

    private void buildDeviceAnalysisDetailQuery(DeviceAnalysisDetailQuery query, DeviceAnalysisDetailRequest request) {
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
        } catch (ArgumentException e) {
            throw new ArgumentException(e.getMessage(), e);
        } catch (Exception e) {
            log.error("device analysis detail request convert error, request is {}", request, e);
            throw new ArgumentException("device analysis detail request convert error", e);
        }
    }
}
