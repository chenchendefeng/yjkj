package com.jiayi.platform.judge.executor;

import com.jiayi.platform.basic.entity.Device;
import com.jiayi.platform.basic.entity.ObjectOrganizationInfo;
import com.jiayi.platform.basic.manager.ObjectOrganizationManager;
import com.jiayi.platform.basic.service.DeviceService;
import com.jiayi.platform.common.enums.CollectType;
import com.jiayi.platform.common.exception.ArgumentException;
import com.jiayi.platform.common.web.util.ExportUtil;
import com.jiayi.platform.judge.common.bean.PageInfo;
import com.jiayi.platform.judge.common.bean.PageRequest;
import com.jiayi.platform.judge.common.bean.PageResult;
import com.jiayi.platform.judge.dao.impala.TrackMergeDao;
import com.jiayi.platform.judge.dto.TrackMergeDto;
import com.jiayi.platform.judge.manage.ImpalaTableManager;
import com.jiayi.platform.judge.query.TrackQuery;
import com.jiayi.platform.judge.query.TrackQueryQuery;
import com.jiayi.platform.judge.request.JudgeRequest;
import com.jiayi.platform.judge.request.TrackQueryRequest;
import com.jiayi.platform.judge.request.TrackRequest;
import com.jiayi.platform.judge.response.TrackMergeResponse;
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
public class TrackMergeExecutor implements JudgeExecutor {
    @Autowired
    private TrackMergeDao trackMergeDao;

    @Autowired
    private ImpalaTableManager impalaTableManager;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private ObjectOrganizationManager objectOrganizationManager;

    @Override
    public <T extends JudgeRequest> List<?> query(T request, PageRequest pageRequest) {
        TrackQueryQuery query = new TrackQueryQuery();
        buildTrackMergeQuery(query, (TrackQueryRequest) request);
        query.setLimit(pageRequest.getPageSize());
        query.setOffset(pageRequest.calOffset());
        return trackMergeDao.selectTrackMerge(query);
    }

    @Override
    public <T extends JudgeRequest> long count(T request) {
        TrackQueryQuery query = new TrackQueryQuery();
        buildTrackMergeQuery(query, (TrackQueryRequest) request);
        return trackMergeDao.countTrackMerge(query);
    }

    @Override
    public List<?> queryCache(Long queryHistoryId, PageRequest pageRequest) {
        return trackMergeDao.selectTrackMergeResult(queryHistoryId, pageRequest.getPageSize(), pageRequest.calOffset());
    }

    @Override
    public long countCache(Long queryHistoryId) {
        return trackMergeDao.countTrackMergeResult(queryHistoryId);
    }

    @Override
    public <T extends JudgeRequest> long cache(T request, long queryHistoryId) {
        TrackQueryQuery query = new TrackQueryQuery();
        buildTrackMergeQuery(query, (TrackQueryRequest) request);
        query.setUid(queryHistoryId);
        trackMergeDao.insertTrackMergeResult(query);
        return countCache(queryHistoryId);
    }

    @Override
    public <T extends JudgeRequest> PageResult convert2Response(List<?> dots, long count, T request, PageRequest pageRequest) {
        List<TrackMergeResponse> responseList = new ArrayList<>();
        Map<Long, Device> deviceMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(dots)) {
            Set<Long> deviceIds = dots.stream().map(dto -> ((TrackMergeDto) dto).getDeviceId()).filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            deviceMap.putAll(deviceService.findByIds(deviceIds));
        }
        dots.forEach(dto -> {
            TrackMergeDto item = (TrackMergeDto) dto;
            TrackMergeResponse response = new TrackMergeResponse();
            response.setObjectTypeName(item.getObjectTypeName());
            response.setObjectId(item.getObjectValue());
            response.setObjectValue(ImpalaDataUtil.addMacCodeColons(item.getObjectValue(), item.getObjectTypeName()));
            response.setRecordAt(item.getRecordAt());
            response.setLatitude(ImpalaDataUtil.convertLngAndLat2Double(item.getLatitude()));
            response.setLongitude(ImpalaDataUtil.convertLngAndLat2Double(item.getLongitude()));
            response.setDeviceId(item.getDeviceId().toString());
            if (item.getDeviceId() != null && deviceMap.get(item.getDeviceId()) != null) {
                response.setDeviceAddress(deviceMap.get(item.getDeviceId()).getAddress());
            }
            response.setImsiImei(item.getImsiImei() == null ? "" : item.getImsiImei());
            ObjectOrganizationInfo organization = objectOrganizationManager.getObjectOrganization(item.getObjectTypeName(), item.getObjectValue());
            if (organization != null) {
                response.setObjDesc(organization.getOrganizationName());
            }

            responseList.add(response);
        });
        PageInfo pageInfo = new PageInfo(dots.size(), count, pageRequest);
        return new PageResult<>(responseList, pageInfo);
    }

    @Override
    public <T extends JudgeRequest> int exportResult(List<String> contents, T request, long offset, long queryId) {
        List<TrackMergeDto> data = (trackMergeDao.selectTrackMergeResult(queryId, ExportService.LOAD_SIZE, offset));

        Map<Long, Device> deviceMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(data)) {
            Set<Long> deviceIds = data.stream().map(TrackMergeDto::getDeviceId).filter(Objects::nonNull).collect(Collectors.toSet());
            deviceMap.putAll(deviceService.findByIds(deviceIds));
        }
        int colCount = 6;
        List<String[]> rowData = new ArrayList<>();
        for (TrackMergeDto datum : data) {
            String[] rowValue = new String[colCount];
            int j = 0;
            rowValue[j++] = CollectType.valueOf(datum.getObjectTypeName().toUpperCase()).desc();
            rowValue[j++] = ImpalaDataUtil.addMacCodeColons(datum.getObjectValue(),
                    datum.getObjectTypeName()) + "\t";
            rowValue[j++] = datum.getImsiImei() + "\t";
            rowValue[j++] = ExportService.SDF.format(datum.getRecordAt());
            rowValue[j++] = (datum.getDeviceId() != null && deviceMap.get(datum.getDeviceId()) != null)
                    ? deviceMap.get(datum.getDeviceId()).getAddress()
                    : "";
            ObjectOrganizationInfo organization = objectOrganizationManager
                    .getObjectOrganization(datum.getObjectTypeName(), datum.getObjectValue());
            rowValue[j] = (organization != null) ? organization.getOrganizationName() : "";
            rowData.add(rowValue);
        }
        contents.add(ExportUtil.genContentByStringList(rowData));
        return data.size();
    }

    private void buildTrackMergeQuery(TrackQueryQuery query, TrackQueryRequest request) {
        List<TrackQuery> queryList = new ArrayList<>();
        List<TrackRequest> trackRequest = request.getTrackRequests();
        trackRequest.forEach(obj -> {
            try {
                TrackQuery singleQuery = new TrackQuery();
                singleQuery.setObjectTypeName(obj.getObjectTypeName());
                singleQuery.setObjectValue(obj.getObjectValue().toUpperCase().replaceAll("[-:：\\s]", ""));
                singleQuery.setObjectHash(ImpalaDataUtil.getObjectHash(singleQuery.getObjectValue()));
                Map<String, Pair<Long, Long>> tableNamesAndTime =
                        impalaTableManager.getValidTableList("query", obj.getBeginDate(), obj.getEndDate());
                singleQuery.setTableNameList(tableNamesAndTime.keySet());
                // 设置临时表和查询表的查询时间及网格
                for (Map.Entry<String, Pair<Long, Long>> namesAndTime : tableNamesAndTime.entrySet()) {
                    long beginDate = namesAndTime.getValue().getLeft();
                    long endDate = namesAndTime.getValue().getRight();
                    if (namesAndTime.getKey().contains("recent")) {
                        singleQuery.setRecentBeginDate(beginDate);
                        singleQuery.setRecentEndDate(endDate);
                        singleQuery.setRecentBeginHours(beginDate / (ImpalaTableManager.QUERY_SPLIT_HOURS * 60 * 60 * 1000L));
                        singleQuery.setRecentEndHours(endDate / (ImpalaTableManager.QUERY_SPLIT_HOURS * 60 * 60 * 1000L));
                    } else {
                        singleQuery.setBeginDate(beginDate);
                        singleQuery.setEndDate(endDate);
                        singleQuery.setBeginHours(beginDate / (ImpalaTableManager.QUERY_SPLIT_HOURS * 60 * 60 * 1000L));
                        singleQuery.setEndHours(endDate / (ImpalaTableManager.QUERY_SPLIT_HOURS * 60 * 60 * 1000L));
                    }
                }
                singleQuery.setTrackType(CollectType.valueOf(obj.getObjectTypeName().toUpperCase()).code());
                queryList.add(singleQuery);
            } catch (Exception e) {
                log.error("track merge request convert error, request is {}", request, e);
                throw new ArgumentException("track merge request convert error", e);
            }
        });
        query.setQueryList(queryList);
    }
}
