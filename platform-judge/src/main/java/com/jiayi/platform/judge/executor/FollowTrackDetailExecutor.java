package com.jiayi.platform.judge.executor;

import com.jiayi.platform.basic.entity.Device;
import com.jiayi.platform.basic.service.DeviceService;
import com.jiayi.platform.common.enums.CollectType;
import com.jiayi.platform.common.exception.ArgumentException;
import com.jiayi.platform.common.web.util.ExportUtil;
import com.jiayi.platform.judge.common.bean.PageRequest;
import com.jiayi.platform.judge.dao.impala.FollowCollisionDao;
import com.jiayi.platform.judge.dto.FollowTrackDetailDto;
import com.jiayi.platform.judge.manage.ImpalaTableManager;
import com.jiayi.platform.judge.query.FollowTrackDetailQuery;
import com.jiayi.platform.judge.request.FollowTrackDetailRequest;
import com.jiayi.platform.judge.request.JudgeDetailRequest;
import com.jiayi.platform.judge.response.FollowTrackDetailResponse;
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
public class FollowTrackDetailExecutor implements JudgeDetailExecutor {
    @Autowired
    private FollowCollisionDao followCollisionDao;

    @Autowired
    private ImpalaTableManager impalaTableManager;
    @Autowired
    private DeviceService deviceService;

    @Override
    public <T> List<?> query(T request, PageRequest pageRequest) {
        FollowTrackDetailQuery query = new FollowTrackDetailQuery();
        buildFollowTrackDetailQuery(query, (FollowTrackDetailRequest) request);
        query.setLimit(pageRequest.getPageSize());
        query.setOffset(pageRequest.calOffset());
        return followCollisionDao.selectFollowTrackDetail(query);
    }

    @Override
    public <T> long count(T request) {
        FollowTrackDetailQuery query = new FollowTrackDetailQuery();
        buildFollowTrackDetailQuery(query, (FollowTrackDetailRequest) request);
        return followCollisionDao.countFollowTrackDetail(query);
    }

    @Override
    public List<?> convert2Response(List<?> dots, String objectType) {
        List<FollowTrackDetailResponse> responseList = new ArrayList<>();
        Map<Long, Device> deviceMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(dots)) {
            Set<Long> deviceIds = dots.stream().map(dto -> ((FollowTrackDetailDto) dto).getDeviceId()).filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            deviceMap.putAll(deviceService.findByIds(deviceIds));
        }
        dots.forEach(dto -> {
            FollowTrackDetailDto item = (FollowTrackDetailDto) dto;
            FollowTrackDetailResponse response = new FollowTrackDetailResponse();
            response.setObjectId(item.getObjectValue());
            response.setObjectValue(ImpalaDataUtil.addMacCodeColons(item.getObjectValue(), objectType));
            response.setOriginRecordAt(item.getOriginRecordAt());
            response.setMatchRecordAt(item.getMatchRecordAt());
            response.setDeviceId("" + item.getDeviceId());
            response.setLatitude(ImpalaDataUtil.convertLngAndLat2Double(item.getLatitude()));
            response.setLongitude(ImpalaDataUtil.convertLngAndLat2Double(item.getLongitude()));
            if (item.getDeviceId() != null && deviceMap.get(item.getDeviceId()) != null) {
                response.setAddress(deviceMap.get(item.getDeviceId()).getAddress());
                response.setDeviceName(deviceMap.get(item.getDeviceId()).getName());
            }
            response.setImsiImei(item.getImsiImei() == null ? "" : item.getImsiImei());

            responseList.add(response);
        });
        return responseList;
    }

    @Override
    public <T extends JudgeDetailRequest> int exportResult(List<String> contents, T request, long offset) {
        FollowTrackDetailQuery query = new FollowTrackDetailQuery();
        buildFollowTrackDetailQuery(query, (FollowTrackDetailRequest) request);
        query.setLimit(ExportService.LOAD_SIZE);
        query.setOffset(offset);
        List<FollowTrackDetailDto> data = followCollisionDao.selectFollowTrackDetail(query);

        Map<Long, Device> deviceMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(data)) {
            Set<Long> deviceIds = data.stream().map(FollowTrackDetailDto::getDeviceId).filter(Objects::nonNull).collect(Collectors.toSet());
            deviceMap.putAll(deviceService.findByIds(deviceIds));
        }
        int colCount = 4;
        if ("imei".equals(request.getObjectTypeName().toLowerCase()) || "imsi".equals(request.getObjectTypeName().toLowerCase())) {
            colCount = 5;
        }
        List<String[]> rowData = new ArrayList<>();
        for (FollowTrackDetailDto datum : data) {
            String[] rowValue = new String[colCount];
            int j = 0;
            if (colCount == 5) {
                rowValue[j++] = datum.getImsiImei() + "\t";
            }
            rowValue[j++] = (datum.getDeviceId() != null && deviceMap.get(datum.getDeviceId()) != null)
                    ? deviceMap.get(datum.getDeviceId()).getAddress()
                    : "";
            rowValue[j++] = ExportService.SDF.format(datum.getOriginRecordAt());
            rowValue[j++] = ExportService.SDF.format(datum.getMatchRecordAt());
            rowValue[j] = String.valueOf(Math.abs(datum.getOriginRecordAt() - datum.getMatchRecordAt()) / 1000);
            rowData.add(rowValue);
        }
        contents.add(ExportUtil.genContentByStringList(rowData));
        return data.size();
    }

    private void buildFollowTrackDetailQuery(FollowTrackDetailQuery query, FollowTrackDetailRequest request) {
        try {
            List<Device> devices = deviceService.findByCollectId(CollectType.valueOf(request.getObjectTypeName().toUpperCase()).code());
            Set<Long> deviceSet = DeviceUtil.selectDeviceIdInAreasByType(request.getAreaList(), devices);
            query.setDeviceIdList(deviceSet);
            Map<String, Pair<Long, Long>> tableNamesAndTime =
                    impalaTableManager.getValidTableList("query", request.getBeginDate(), request.getEndDate());
            query.setObjTableList(tableNamesAndTime.keySet());
            // 设置临时表和查询表的查询时间及网格
            for (Map.Entry<String, Pair<Long, Long>> namesAndTime : tableNamesAndTime.entrySet()) {
                long beginDate = namesAndTime.getValue().getLeft();
                long endDate = namesAndTime.getValue().getRight();
                if (namesAndTime.getKey().contains("recent")) {
                    query.setObjRecentBeginDate(beginDate);
                    query.setObjRecentEndDate(endDate);
                    query.setObjRecentBeginHours(beginDate / (ImpalaTableManager.QUERY_SPLIT_HOURS * 60 * 60 * 1000L));
                    query.setObjRecentEndHours(endDate / (ImpalaTableManager.QUERY_SPLIT_HOURS * 60 * 60 * 1000L));
                } else {
                    query.setObjBeginDate(beginDate);
                    query.setObjEndDate(endDate);
                    query.setObjBeginHours(beginDate / (ImpalaTableManager.QUERY_SPLIT_HOURS * 24 * 60 * 60 * 1000L));
                    query.setObjEndHours(endDate / (ImpalaTableManager.QUERY_SPLIT_HOURS * 24 * 60 * 60 * 1000L));
                }
            }
            Map<String, Pair<Long, Long>> refTableNamesAndTime =
                    impalaTableManager.getValidTableList("query", request.getBeginDate(), request.getEndDate(), request.getTimeOffset());
            query.setRefTableList(refTableNamesAndTime.keySet());
            // 设置临时表和查询表的查询时间及网格
            for (Map.Entry<String, Pair<Long, Long>> namesAndTime : refTableNamesAndTime.entrySet()) {
                long beginDate = namesAndTime.getValue().getLeft();
                long endDate = namesAndTime.getValue().getRight();
                if (namesAndTime.getKey().contains("recent")) {
                    query.setRefRecentBeginDate(beginDate);
                    query.setRefRecentEndDate(endDate);
                    query.setRefRecentBeginHours(beginDate / (ImpalaTableManager.QUERY_SPLIT_HOURS * 60 * 60 * 1000L));
                    query.setRefRecentEndHours(endDate / (ImpalaTableManager.QUERY_SPLIT_HOURS * 60 * 60 * 1000L));
                } else {
                    query.setRefBeginDate(beginDate);
                    query.setRefEndDate(endDate);
                    query.setRefBeginHours(beginDate / (ImpalaTableManager.QUERY_SPLIT_HOURS * 60 * 60 * 1000L));
                    query.setRefEndHours(endDate / (ImpalaTableManager.QUERY_SPLIT_HOURS * 60 * 60 * 1000L));
                }
            }
            query.setTrackType(CollectType.valueOf(request.getObjectTypeName().toUpperCase()).code());
            query.setObjectValue(request.getObjectId().toUpperCase().replaceAll("[-:：\\s]", ""));
            query.setObjectHash(Math.abs(ImpalaDataUtil.getObjectHash(query.getObjectValue())));
            query.setRefObjectValue(request.getRefObjectValue().toUpperCase().replaceAll("[-:：\\s]", ""));
            query.setRefObjectHash(ImpalaDataUtil.getObjectHash(query.getRefObjectValue()));
            query.setRefTimeOffset(request.getTimeOffset() * 1000L);
        } catch (ArgumentException e) {
            throw new ArgumentException(e.getMessage(), e);
        } catch (Exception e) {
            log.error("follow track detail request convert error, request is {}", request, e);
            throw new ArgumentException("follow track detail request convert error", e);
        }
    }
}
