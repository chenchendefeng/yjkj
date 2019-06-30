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
import com.jiayi.platform.judge.dao.impala.TrackQueryDao;
import com.jiayi.platform.judge.dto.TrackQueryDto;
import com.jiayi.platform.judge.manage.ImpalaTableManager;
import com.jiayi.platform.judge.query.TrackQuery;
import com.jiayi.platform.judge.query.TrackQueryQuery;
import com.jiayi.platform.judge.request.JudgeRequest;
import com.jiayi.platform.judge.request.TrackQueryRequest;
import com.jiayi.platform.judge.request.TrackRequest;
import com.jiayi.platform.judge.response.TrackQueryResponse;
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
public class TrackQueryExecutor implements JudgeExecutor {
    @Autowired
    private TrackQueryDao trackQueryDao;

    @Autowired
    private ImpalaTableManager impalaTableManager;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private ObjectOrganizationManager objectOrganizationManager;

    @Override
    public <T extends JudgeRequest> List<?> query(T request, PageRequest pageRequest) {
        TrackQueryQuery query = new TrackQueryQuery();
        buildTrackQueryQuery(query, (TrackQueryRequest) request);
        query.setLimit(pageRequest.getPageSize());
        query.setOffset(pageRequest.calOffset());
        return trackQueryDao.selectTrackQuery(query);
    }

    @Override
    public <T extends JudgeRequest> long count(T request) {
        TrackQueryQuery query = new TrackQueryQuery();
        buildTrackQueryQuery(query, (TrackQueryRequest) request);
        return trackQueryDao.countTrackQuery(query);
    }

    @Override
    public List<?> queryCache(Long queryHistoryId, PageRequest pageRequest) {
        return trackQueryDao.selectTrackQueryResult(queryHistoryId, pageRequest.getPageSize(), pageRequest.calOffset());
    }

    @Override
    public long countCache(Long queryHistoryId) {
        return trackQueryDao.countTrackQueryResult(queryHistoryId);
    }

    @Override
    public <T extends JudgeRequest> long cache(T request, long queryHistoryId) {
        TrackQueryQuery query = new TrackQueryQuery();
        buildTrackQueryQuery(query, (TrackQueryRequest) request);
        query.setUid(queryHistoryId);
        trackQueryDao.insertTrackQueryResult(query);
        return countCache(queryHistoryId);
    }

    @Override
    public <T extends JudgeRequest> PageResult convert2Response(List<?> dots, long count, T request, PageRequest pageRequest) {
        List<TrackQueryResponse> responseList = new ArrayList<>();
        Map<Long, Device> deviceMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(dots)) {
            Set<Long> deviceIds = new HashSet<>();
            deviceIds.addAll(dots.stream().map(dto -> ((TrackQueryDto) dto).getFromDeviceId()).filter(Objects::nonNull)
                    .collect(Collectors.toSet()));
            deviceIds.addAll(dots.stream().map(dto -> ((TrackQueryDto) dto).getToDeviceId()).filter(Objects::nonNull)
                    .collect(Collectors.toSet()));
            deviceMap.putAll(deviceService.findByIds(deviceIds));
        }
        dots.forEach(dto -> {
            TrackQueryDto item = (TrackQueryDto) dto;
            TrackQueryResponse response = new TrackQueryResponse();
            response.setObjectTypeName(item.getObjectTypeName());
            response.setObjectId(item.getObjectValue());
            response.setObjectValue(ImpalaDataUtil.addMacCodeColons(item.getObjectValue(), item.getObjectTypeName()));
            response.setTrackCount(item.getMatchCount());
            response.setDeviceCount(item.getUniqueDevCount());
            response.setMinHappenAt(item.getMinHappenAt());
            response.setMaxHappenAt(item.getMaxHappenAt());
            if (item.getFromDeviceId() != null && deviceMap.get(item.getFromDeviceId()) != null) {
                response.setBeginDeviceAddress(deviceMap.get(item.getFromDeviceId()).getAddress());
                response.setBeginPlaceAddress(deviceMap.get(item.getFromDeviceId()).getAddress());
            }
            if (item.getToDeviceId() != null && deviceMap.get(item.getToDeviceId()) != null) {
                response.setEndDeviceAddress(deviceMap.get(item.getToDeviceId()).getAddress());
                response.setEndPlaceAddress(deviceMap.get(item.getToDeviceId()).getAddress());
            }
            response.setImsiImei(item.getImsiImei() == null ? "" : item.getImsiImei());
            ObjectOrganizationInfo organization = objectOrganizationManager.getObjectOrganization(item.getObjectTypeName(), item.getObjectValue());
            if (organization != null) {
                response.setDesc(organization.getOrganizationName());
            }

            responseList.add(response);
        });
        PageInfo pageInfo = new PageInfo(dots.size(), count, pageRequest);
        return new PageResult<>(responseList, pageInfo);
    }

    @Override
    public <T extends JudgeRequest> int exportResult(List<String> contents, T request, long offset, long queryId) {
        List<TrackQueryDto> data = (trackQueryDao.selectTrackQueryResult(queryId, ExportService.LOAD_SIZE, offset));

        Map<Long, Device> deviceMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(data)) {
            Set<Long> deviceIds = new HashSet<>();
            deviceIds.addAll(data.stream().map(TrackQueryDto::getFromDeviceId).filter(Objects::nonNull).collect(Collectors.toSet()));
            deviceIds.addAll(data.stream().map(TrackQueryDto::getToDeviceId).filter(Objects::nonNull).collect(Collectors.toSet()));
            deviceMap.putAll(deviceService.findByIds(deviceIds));
        }
        int colCount = 10;
        List<String[]> rowData = new ArrayList<>();
        for (TrackQueryDto datum : data) {
            String[] rowValue = new String[colCount];
            int j = 0;
            rowValue[j++] = CollectType.valueOf(datum.getObjectTypeName().toUpperCase()).desc();
            rowValue[j++] = ImpalaDataUtil.addMacCodeColons(datum.getObjectValue(), datum.getObjectTypeName()) + "\t";
            rowValue[j++] = datum.getImsiImei() + "\t";
            rowValue[j++] = ExportService.SDF.format(datum.getMinHappenAt());
            rowValue[j++] = ExportService.SDF.format(datum.getMaxHappenAt());
            rowValue[j++] = (datum.getFromDeviceId() != null && deviceMap.get(datum.getFromDeviceId()) != null)
                    ? deviceMap.get(datum.getFromDeviceId()).getAddress()
                    : "";
            rowValue[j++] = (datum.getToDeviceId() != null && deviceMap.get(datum.getToDeviceId()) != null)
                    ? deviceMap.get(datum.getToDeviceId()).getAddress()
                    : "";
            rowValue[j++] = datum.getMatchCount().toString();
            rowValue[j++] = datum.getUniqueDevCount().toString();
            ObjectOrganizationInfo organization = objectOrganizationManager
                    .getObjectOrganization(datum.getObjectTypeName(), datum.getObjectValue());
            rowValue[j] = (organization != null) ? organization.getOrganizationName() : "";
            rowData.add(rowValue);
        }
        contents.add(ExportUtil.genContentByStringList(rowData));
        return data.size();
    }

    private void buildTrackQueryQuery(TrackQueryQuery query, TrackQueryRequest request) {
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
                log.error("track query request convert error, request is {}", request, e);
                throw new ArgumentException("track query request convert error", e);
            }
        });
        query.setQueryList(queryList);
    }
}
