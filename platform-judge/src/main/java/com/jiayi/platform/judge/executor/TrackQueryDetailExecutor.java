package com.jiayi.platform.judge.executor;

import com.jiayi.platform.basic.entity.Device;
import com.jiayi.platform.basic.entity.ObjectOrganizationInfo;
import com.jiayi.platform.basic.manager.ObjectOrganizationManager;
import com.jiayi.platform.basic.service.DeviceService;
import com.jiayi.platform.common.enums.CollectType;
import com.jiayi.platform.common.exception.ArgumentException;
import com.jiayi.platform.common.web.util.ExportUtil;
import com.jiayi.platform.judge.common.bean.PageRequest;
import com.jiayi.platform.judge.dao.impala.TrackQueryDao;
import com.jiayi.platform.judge.dto.TrackQueryDetailDto;
import com.jiayi.platform.judge.manage.ImpalaTableManager;
import com.jiayi.platform.judge.query.TrackDetailQuery;
import com.jiayi.platform.judge.request.JudgeDetailRequest;
import com.jiayi.platform.judge.request.TrackDetailRequest;
import com.jiayi.platform.judge.response.TrackQueryDetailResponse;
import com.jiayi.platform.judge.service.ExportService;
import com.jiayi.platform.judge.util.ImpalaDataUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TrackQueryDetailExecutor implements JudgeDetailExecutor {
    @Autowired
    private TrackQueryDao trackQueryDao;

    @Autowired
    private ImpalaTableManager impalaTableManager;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private ObjectOrganizationManager objectOrganizationManager;

    @Override
    public <T> List<?> query(T request, PageRequest pageRequest) {
        TrackDetailQuery query = new TrackDetailQuery();
        buildTrackQueryDetailQuery(query, (TrackDetailRequest) request);
        query.setLimit(pageRequest.getPageSize());
        query.setOffset(pageRequest.calOffset());
        return trackQueryDao.selectTrackQueryDetail(query);
    }

    @Override
    public <T> long count(T request) {
        TrackDetailQuery query = new TrackDetailQuery();
        buildTrackQueryDetailQuery(query, (TrackDetailRequest) request);
        return trackQueryDao.countTrackQueryDetail(query);
    }

    @Override
    public List<?> convert2Response(List<?> dots, String objectType) {
        List<TrackQueryDetailResponse> responseList = new ArrayList<>();
        Map<Long, Device> deviceMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(dots)) {
            Set<Long> deviceIds = dots.stream().map(dto -> ((TrackQueryDetailDto) dto).getDeviceId()).filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            deviceMap.putAll(deviceService.findByIds(deviceIds));
        }
        dots.forEach(dto -> {
            TrackQueryDetailDto item = (TrackQueryDetailDto) dto;
            TrackQueryDetailResponse response = new TrackQueryDetailResponse();
            response.setObjectTypeName(item.getObjectTypeName());
            response.setObjectValue(ImpalaDataUtil.addMacCodeColons(item.getObjectValue(), item.getObjectTypeName()));
            response.setRecordAt(item.getRecordAt());
            response.setLatitude(ImpalaDataUtil.convertLngAndLat2Double(item.getLatitude()));
            response.setLongitude(ImpalaDataUtil.convertLngAndLat2Double(item.getLongitude()));
            response.setDeviceId(item.getDeviceId().toString());
            if (item.getDeviceId() != null && deviceMap.get(item.getDeviceId()) != null) {
                response.setDeviceAddress(deviceMap.get(item.getDeviceId()).getAddress());
            }
            response.setApMac(item.getApMac() == null ? "" : ImpalaDataUtil.addMacCodeColons(item.getApMac(), "mac"));
            response.setApName(item.getApName() == null ? "" : item.getApName());
            response.setPower(item.getPower() == null ? "" : item.getPower().toString());
            response.setChannel(item.getChannel() == null ? "" : item.getChannel().toString());
            response.setImeiCode(item.getImeiCode() == null ? "" : item.getImeiCode());
            response.setImsiCode(item.getImsiCode() == null ? "" : item.getImsiCode());
            response.setImsiImei(item.getImsiImei() == null ? "" : item.getImsiImei());
            ObjectOrganizationInfo organization = objectOrganizationManager.getObjectOrganization(item.getObjectTypeName(), item.getObjectValue());
            if (organization != null) {
                switch (item.getObjectTypeName().toLowerCase()) {
                    case "mac":
                        response.setFactory(organization.getOrganizationName().replace(',', '，'));
                        break;
                    case "carno":
                        response.setDistrict(organization.getOrganizationName());
                        break;
                    case "imsi":
                        response.setOperator(organization.getOrganizationName());
                        break;
                    case "imei":
                        response.setFactory(organization.getOrganizationName());
                        break;
                }
            }

            responseList.add(response);
        });
        return responseList;
    }

    @Override
    public <T extends JudgeDetailRequest> int exportResult(List<String> contents, T request, long offset) {
        TrackDetailQuery query = new TrackDetailQuery();
        buildTrackQueryDetailQuery(query, (TrackDetailRequest) request);
        query.setLimit(ExportService.LOAD_SIZE);
        query.setOffset(offset);
        List<TrackQueryDetailDto> data = trackQueryDao.selectTrackQueryDetail(query);

        Map<Long, Device> deviceMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(data)) {
            Set<Long> deviceIds = data.stream().map(TrackQueryDetailDto::getDeviceId).filter(Objects::nonNull).collect(Collectors.toSet());
            deviceMap.putAll(deviceService.findByIds(deviceIds));
        }
        int colCount;
        switch(request.getObjectTypeName().toLowerCase()) {
            case "mac": colCount = 6; break;
            case "carno": colCount = 2; break;
            case "imsi":
            case "imei": colCount = 3; break;
            default:
                throw new ArgumentException("Unexpected value: " + request.getObjectTypeName());
        }
        List<String[]> rowData = new ArrayList<>();
        for (TrackQueryDetailDto datum : data) {
            String[] rowValue = new String[colCount];
            int j = 0;
            rowValue[j++] = ExportService.SDF.format(datum.getRecordAt());
            rowValue[j++] = (datum.getDeviceId() != null && deviceMap.get(datum.getDeviceId()) != null)
                    ? deviceMap.get(datum.getDeviceId()).getAddress()
                    : "";
            switch(request.getObjectTypeName().toLowerCase()) {
                case "mac":
                    rowValue[j++] = (datum.getApMac() != null) ? ImpalaDataUtil.addMacCodeColons(datum.getApMac(), "mac") : "";
                    rowValue[j++] = (datum.getApName() != null) ? datum.getApName() : "";
                    rowValue[j++] = (datum.getChannel() != null) ? datum.getChannel().toString() : "";
                    rowValue[j] = (datum.getPower() != null) ? datum.getPower().toString() : "";
                    break;
                case "imei":
                case "imsi":
                    rowValue[j] = (datum.getImsiImei() != null) ? datum.getImsiImei() : "";
            }
            rowData.add(rowValue);
        }
        contents.add(ExportUtil.genContentByStringList(rowData));
        return data.size();
    }

    private void buildTrackQueryDetailQuery(TrackDetailQuery query, TrackDetailRequest request) {
        try {
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
            log.error("track query detail request convert error, request is {}", request, e);
            throw new ArgumentException("track query detail request convert error", e);
        }
    }
}
