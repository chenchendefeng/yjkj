package com.jiayi.platform.judge.executor;

import com.jiayi.platform.basic.entity.Device;
import com.jiayi.platform.basic.service.DeviceService;
import com.jiayi.platform.common.enums.CollectType;
import com.jiayi.platform.common.exception.ArgumentException;
import com.jiayi.platform.common.util.LocationUtils;
import com.jiayi.platform.common.web.util.ExportUtil;
import com.jiayi.platform.judge.common.bean.PageRequest;
import com.jiayi.platform.judge.dao.impala.MultiTrackCollisionDao;
import com.jiayi.platform.judge.dto.MultiTrackDetailDto;
import com.jiayi.platform.judge.manage.ImpalaTableManager;
import com.jiayi.platform.judge.query.MultiTrackDetailQuery;
import com.jiayi.platform.judge.request.JudgeDetailRequest;
import com.jiayi.platform.judge.request.MultiTrackDetailRequest;
import com.jiayi.platform.judge.response.MultiTrackDetailResponse;
import com.jiayi.platform.judge.service.ExportService;
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
public class MultiTrackDetailExecutor implements JudgeDetailExecutor {
    @Autowired
    private MultiTrackCollisionDao multiTrackCollisionDao;

    @Autowired
    private ImpalaTableManager impalaTableManager;
    @Autowired
    private DeviceService deviceService;

    @Override
    public <T> List<?> query(T request, PageRequest pageRequest) {
        MultiTrackDetailQuery query = new MultiTrackDetailQuery();
        buildMultiTrackDetailQuery(query, (MultiTrackDetailRequest) request);
        query.setLimit(pageRequest.getPageSize());
        query.setOffset(pageRequest.calOffset());
        return multiTrackCollisionDao.selectMultiTrackDetail(query);
    }

    @Override
    public <T> long count(T request) {
        MultiTrackDetailQuery query = new MultiTrackDetailQuery();
        buildMultiTrackDetailQuery(query, (MultiTrackDetailRequest) request);
        return multiTrackCollisionDao.countMultiTrackDetail(query);
    }

    @Override
    public List<?> convert2Response(List<?> dots, String objectType) {
        List<MultiTrackDetailResponse> responseList = new ArrayList<>();
        Map<Long, Device> deviceMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(dots)) {
            Set<Long> deviceIds = new HashSet<>();
            deviceIds.addAll(dots.stream().map(dto -> ((MultiTrackDetailDto) dto).getOriginDeviceId()).filter(Objects::nonNull)
                    .collect(Collectors.toSet()));
            deviceIds.addAll(dots.stream().map(dto -> ((MultiTrackDetailDto) dto).getDeviceId()).filter(Objects::nonNull)
                    .collect(Collectors.toSet()));
            deviceMap.putAll(deviceService.findByIds(deviceIds));
        }
        dots.forEach(dto -> {
            MultiTrackDetailDto item = (MultiTrackDetailDto) dto;
            MultiTrackDetailResponse response = new MultiTrackDetailResponse();
            response.setObjectId(item.getObjectValue());
            response.setOriginRecordAt(item.getOriginRecordAt());
            response.setMatchRecordAt(item.getMatchRecordAt());
            response.setDeviceId("" + item.getDeviceId());
            double lat = ImpalaDataUtil.convertLngAndLat2Double(item.getLatitude());
            double lng = ImpalaDataUtil.convertLngAndLat2Double(item.getLongitude());
            double refLat = ImpalaDataUtil.convertLngAndLat2Double(item.getRefLatitude());
            double refLng = ImpalaDataUtil.convertLngAndLat2Double(item.getRefLongitude());
            response.setLatitude(lat);
            response.setLongitude(lng);
            response.setOriginLatitude(refLat);
            response.setOriginLongitude(refLng);
            response.setMatchDistance((int) LocationUtils.distance(lng, lat, refLng, refLat));
            if (item.getOriginDeviceId() != null && deviceMap.get(item.getOriginDeviceId()) != null) {
                response.setOriginAddress(deviceMap.get(item.getOriginDeviceId()).getAddress());
            }
            if (item.getDeviceId() != null && deviceMap.get(item.getDeviceId()) != null) {
                response.setMatchAddress(deviceMap.get(item.getDeviceId()).getAddress());
                response.setDeviceName(deviceMap.get(item.getDeviceId()).getName());
            }
            response.setImsiImei(item.getImsiImei() == null ? "" : item.getImsiImei());

            responseList.add(response);
        });
        return responseList;
    }

    @Override
    public <T extends JudgeDetailRequest> int exportResult(List<String> contents, T request, long offset) {
        MultiTrackDetailQuery query = new MultiTrackDetailQuery();
        buildMultiTrackDetailQuery(query, (MultiTrackDetailRequest) request);
        query.setLimit(ExportService.LOAD_SIZE);
        query.setOffset(offset);
        List<MultiTrackDetailDto> data = multiTrackCollisionDao.selectMultiTrackDetail(query);
        Map<Long, Device> deviceMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(data)) {
            Set<Long> deviceIds = data.stream().map(MultiTrackDetailDto::getDeviceId).filter(Objects::nonNull).collect(Collectors.toSet());
            deviceMap.putAll(deviceService.findByIds(deviceIds));
        }
        int colCount = 6;
        if ("imei".equals(request.getObjectTypeName().toLowerCase()) || "imsi".equals(request.getObjectTypeName().toLowerCase())) {
            colCount = 7;
        }
        List<String[]> rowData = new ArrayList<>();
        for (MultiTrackDetailDto datum : data) {
            String[] rowValue = new String[colCount];
            int j = 0;
            if (colCount == 7) {
                rowValue[j++] = datum.getImsiImei() + "\t";
            }
            rowValue[j++] = ExportService.SDF.format(datum.getOriginRecordAt());
            rowValue[j++] = ExportService.SDF.format(datum.getMatchRecordAt());
            rowValue[j++] = String.valueOf(Math.abs(datum.getOriginRecordAt() - datum.getMatchRecordAt()) / 1000);
            rowValue[j++] = (datum.getOriginDeviceId() != null && deviceMap.get(datum.getOriginDeviceId()) != null)
                    ? deviceMap.get(datum.getOriginDeviceId()).getAddress()
                    : "";
            rowValue[j++] = (datum.getDeviceId() != null && deviceMap.get(datum.getDeviceId()) != null)
                    ? deviceMap.get(datum.getDeviceId()).getAddress()
                    : "";
            double lat = ImpalaDataUtil.convertLngAndLat2Double(datum.getLatitude());
            double lng = ImpalaDataUtil.convertLngAndLat2Double(datum.getLongitude());
            double refLat = ImpalaDataUtil.convertLngAndLat2Double(datum.getRefLatitude());
            double refLng = ImpalaDataUtil.convertLngAndLat2Double(datum.getRefLongitude());
            rowValue[j] = String.valueOf((int) LocationUtils.distance(lng, lat, refLng, refLat));
            rowData.add(rowValue);
        }
        contents.add(ExportUtil.genContentByStringList(rowData));
        return data.size();
    }

    private void buildMultiTrackDetailQuery(MultiTrackDetailQuery query, MultiTrackDetailRequest request) {
        try {
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
                    query.setObjBeginHours(beginDate / (ImpalaTableManager.QUERY_SPLIT_HOURS * 60 * 60 * 1000L));
                    query.setObjEndHours(endDate / (ImpalaTableManager.QUERY_SPLIT_HOURS * 60 * 60 * 1000L));
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
            query.setObjTrackType(CollectType.valueOf(request.getObjectTypeName().toUpperCase()).code());
            query.setRefTrackType(CollectType.valueOf(request.getRefObjectTypeName().toUpperCase()).code());
            query.setObjectValue(request.getObjectId().toUpperCase().replaceAll("[-:：\\s]", ""));
            query.setObjectHash(Math.abs(ImpalaDataUtil.getObjectHash(query.getObjectValue())));
            query.setRefObjectValue(request.getRefObjectValue().toUpperCase().replaceAll("[-:：\\s]", ""));
            query.setRefObjectHash(Math.abs(ImpalaDataUtil.getObjectHash(query.getRefObjectValue())));
            query.setRefTimeOffset(request.getTimeOffset() * 1000L);
            query.setDistance(request.getDistance());
        } catch (ArgumentException e) {
            throw new ArgumentException(e.getMessage(), e);
        } catch (Exception e) {
            log.error("multi-track detail request convert error, request is {}", request, e);
            throw new ArgumentException("multi-track detail request convert error", e);
        }
    }
}
