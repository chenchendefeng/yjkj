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
import com.jiayi.platform.judge.dao.impala.MultiTrackCollisionDao;
import com.jiayi.platform.judge.dto.MultiTrackCollisionDto;
import com.jiayi.platform.judge.manage.ImpalaTableManager;
import com.jiayi.platform.judge.query.MultiTrackCollisionQuery;
import com.jiayi.platform.judge.request.JudgeRequest;
import com.jiayi.platform.judge.request.MultiTrackCollisionRequest;
import com.jiayi.platform.judge.response.MultiTrackCollisionResponse;
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
public class MultiTrackCollisionExecutor implements JudgeExecutor {

    @Autowired
    private MultiTrackCollisionDao multiTrackCollisionDao;

    @Autowired
    private ImpalaTableManager impalaTableManager;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private ObjectOrganizationManager objectOrganizationManager;

    @Override
    public <T extends JudgeRequest> List<?> query(T request, PageRequest pageRequest) {
        MultiTrackCollisionQuery query = new MultiTrackCollisionQuery();
        buildMultiTrackQuery(query, (MultiTrackCollisionRequest) request);
        query.setLimit(pageRequest.getPageSize());
        query.setOffset(pageRequest.calOffset());
        return multiTrackCollisionDao.selectMultiTrack(query);
    }

    @Override
    public <T extends JudgeRequest> long count(T request) {
        MultiTrackCollisionQuery query = new MultiTrackCollisionQuery();
        buildMultiTrackQuery(query, (MultiTrackCollisionRequest) request);
        return multiTrackCollisionDao.countMultiTrack(query);
    }

    @Override
    public List<?> queryCache(Long queryHistoryId, PageRequest pageRequest) {
        return multiTrackCollisionDao.selectMultiTrackResult(queryHistoryId, pageRequest.getPageSize(), pageRequest.calOffset());
    }

    @Override
    public long countCache(Long queryHistoryId) {
        return multiTrackCollisionDao.countMultiTrackResult(queryHistoryId);
    }

    @Override
    public <T extends JudgeRequest> long cache(T request, long queryHistoryId) {
        MultiTrackCollisionQuery query = new MultiTrackCollisionQuery();
        buildMultiTrackQuery(query, (MultiTrackCollisionRequest) request);
        query.setUid(queryHistoryId);
        multiTrackCollisionDao.insertMultiTrackResult(query);
        return countCache(queryHistoryId);
    }

    @Override
    public <T extends JudgeRequest> PageResult convert2Response(List<?> dots, long count, T request, PageRequest pageRequest) {
        List<MultiTrackCollisionResponse> responseList = new ArrayList<>();
        List<String> objectValues = new ArrayList<>();
        Map<Long, Device> deviceMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(dots)) {
            Set<Long> deviceIds = new HashSet<>();
            deviceIds.addAll(dots.stream().map(dto -> ((MultiTrackCollisionDto) dto).getFromDeviceId()).filter(Objects::nonNull)
                    .collect(Collectors.toSet()));
            deviceIds.addAll(dots.stream().map(dto -> ((MultiTrackCollisionDto) dto).getToDeviceId()).filter(Objects::nonNull)
                    .collect(Collectors.toSet()));
            deviceMap.putAll(deviceService.findByIds(deviceIds));
        }
        dots.forEach(dto -> {
            MultiTrackCollisionDto item = (MultiTrackCollisionDto) dto;
            MultiTrackCollisionResponse response = new MultiTrackCollisionResponse();
            response.setObjectId(item.getObjectValue());
            response.setObjectValue(ImpalaDataUtil.addMacCodeColons(item.getObjectValue(),
                    ((MultiTrackCollisionRequest) request).getFollowObjectTypeName()));
            response.setMatchCount(item.getMatchCount());
            response.setUniqueDevCount(item.getUniqueDevCount());
            response.setTrackCount(item.getTrackCount());
            response.setBeginDate(item.getMinHappenAt());
            response.setEndDate(item.getMaxHappenAt());
            if (item.getFromDeviceId() != null && deviceMap.get(item.getFromDeviceId()) != null)
                response.setBeginAddress(deviceMap.get(item.getFromDeviceId()).getAddress());
            if (item.getToDeviceId() != null && deviceMap.get(item.getToDeviceId()) != null)
                response.setEndAddress(deviceMap.get(item.getToDeviceId()).getAddress());
            response.setImsiImei(item.getImsiImei() == null ? "" : item.getImsiImei());
            ObjectOrganizationInfo organization = objectOrganizationManager.
                    getObjectOrganization(((MultiTrackCollisionRequest) request).getFollowObjectTypeName(), item.getObjectValue());
            if (organization != null) {
                response.setDesc(organization.getOrganizationName());
            }

            responseList.add(response);
            objectValues.add(response.getObjectValue());
        });
        // TODO 这里还差全文检索的接口调用，另外考虑是否需要抽象基类，方便代码重用

        PageInfo pageInfo = new PageInfo(dots.size(), count, pageRequest);
        return new PageResult<>(responseList, pageInfo);
    }

    @Override
    public <T extends JudgeRequest> int exportResult(List<String> contents, T request, long offset, long queryId) {
        List<MultiTrackCollisionDto> data = (multiTrackCollisionDao.selectMultiTrackResult(queryId, ExportService.LOAD_SIZE, offset));

        Map<Long, Device> deviceMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(data)) {
            Set<Long> deviceIds = new HashSet<>();
            deviceIds.addAll(data.stream().map(MultiTrackCollisionDto::getFromDeviceId).filter(Objects::nonNull).collect(Collectors.toSet()));
            deviceIds.addAll(data.stream().map(MultiTrackCollisionDto::getToDeviceId).filter(Objects::nonNull).collect(Collectors.toSet()));
            deviceMap.putAll(deviceService.findByIds(deviceIds));
        }
        int colCount = 9;
        List<String[]> rowData = new ArrayList<>();
        for (MultiTrackCollisionDto datum : data) {
            String[] rowValue = new String[colCount];
            int j = 0;
            rowValue[j++] = ImpalaDataUtil.addMacCodeColons(datum.getObjectValue(),
                    ((MultiTrackCollisionRequest) request).getFollowObjectTypeName()) + "\t";
            rowValue[j++] = datum.getUniqueDevCount().toString();
            rowValue[j++] = datum.getMatchCount().toString();
            rowValue[j++] = datum.getTrackCount().toString();
            rowValue[j++] = ExportService.SDF.format(datum.getMinHappenAt());
            rowValue[j++] = ExportService.SDF.format(datum.getMaxHappenAt());
            rowValue[j++] = (datum.getFromDeviceId() != null && deviceMap.get(datum.getFromDeviceId()) != null)
                    ? deviceMap.get(datum.getFromDeviceId()).getAddress()
                    : "";
            rowValue[j++] = (datum.getToDeviceId() != null && deviceMap.get(datum.getToDeviceId()) != null)
                    ? deviceMap.get(datum.getToDeviceId()).getAddress()
                    : "";
            ObjectOrganizationInfo organization = objectOrganizationManager
                    .getObjectOrganization(((MultiTrackCollisionRequest) request).getFollowObjectTypeName(), datum.getObjectValue());
            rowValue[j] = (organization != null) ? organization.getOrganizationName() : "";
            rowData.add(rowValue);
        }
        contents.add(ExportUtil.genContentByStringList(rowData));
        return data.size();
    }

    private void buildMultiTrackQuery(MultiTrackCollisionQuery query, MultiTrackCollisionRequest request) {
        try {
            Map<String, Pair<Long, Long>> objTableNamesAndTime =
                    impalaTableManager.getValidTableList("collision", request.getBeginDate(), request.getEndDate(), request.getTimeOffset());
            query.setObjTableList(objTableNamesAndTime.keySet());
            // 设置临时表和碰撞表的查询时间及网格
            for (Map.Entry<String, Pair<Long, Long>> namesAndTime : objTableNamesAndTime.entrySet()) {
                long beginDate = namesAndTime.getValue().getLeft();
                long endDate = namesAndTime.getValue().getRight();
                if (namesAndTime.getKey().contains("recent")) {
                    query.setObjRecentBeginDate(beginDate);
                    query.setObjRecentEndDate(endDate);
                    query.setObjRecentBeginHours(beginDate / (ImpalaTableManager.SPLIT_HOURS * 60 * 60 * 1000L));
                    query.setObjRecentEndHours(endDate / (ImpalaTableManager.SPLIT_HOURS * 60 * 60 * 1000L));
                } else {
                    query.setObjBeginDate(beginDate);
                    query.setObjEndDate(endDate);
//                    query.setObjBeginHours(beginDate / (ImpalaTableManager.SPLIT_HOURS * 60 * 60 * 1000L));
//                    query.setObjEndHours(endDate / (ImpalaTableManager.SPLIT_HOURS * 60 * 60 * 1000L));
                }
            }
            Map<String, Pair<Long, Long>> refTableNamesAndTime =
                    impalaTableManager.getValidTableList("collision", request.getBeginDate(), request.getEndDate());
            query.setRefTableList(refTableNamesAndTime.keySet());
            // 设置临时表和碰撞表的查询时间及网格
            for (Map.Entry<String, Pair<Long, Long>> namesAndTime : refTableNamesAndTime.entrySet()) {
                long beginDate = namesAndTime.getValue().getLeft();
                long endDate = namesAndTime.getValue().getRight();
                if (namesAndTime.getKey().contains("recent")) {
                    query.setRefRecentBeginDate(beginDate);
                    query.setRefRecentEndDate(endDate);
                    query.setRefRecentBeginHours(beginDate / (ImpalaTableManager.SPLIT_HOURS * 60 * 60 * 1000L));
                    query.setRefRecentEndHours(endDate / (ImpalaTableManager.SPLIT_HOURS * 60 * 60 * 1000L));
                } else {
                    query.setRefBeginDate(beginDate);
                    query.setRefEndDate(endDate);
                    query.setRefBeginHours(beginDate / (ImpalaTableManager.SPLIT_HOURS * 60 * 60 * 1000L));
                    query.setRefEndHours(endDate / (ImpalaTableManager.SPLIT_HOURS * 60 * 60 * 1000L));
                }
            }
            query.setObjTrackType(CollectType.valueOf(request.getFollowObjectTypeName().toUpperCase()).code());
            query.setRefTrackType(CollectType.valueOf(request.getObjectTypeName().toUpperCase()).code());
            query.setMatchCount(request.getMatchCount());
            query.setRefTimeOffset(request.getTimeOffset() * 1000L);
            query.setDistance(request.getDistance());
            query.setRefObjectValue(request.getObjectValue().toUpperCase().replaceAll("[-:：\\s]", ""));
            if (request.getObjectValueList() != null) {
                query.setObjectValueList(request.getObjectValueList().stream()
                        .map(objVal -> objVal.toUpperCase().replaceAll("[-:：\\s]", ""))
                        .filter(objVal -> StringUtils.isNotBlank(objVal) && !query.getRefObjectValue().equals(objVal))
                        .collect(Collectors.toSet()));
            }
            query.setSplitHours(ImpalaTableManager.SPLIT_HOURS);
        } catch (ArgumentException e) {
            throw new ArgumentException(e.getMessage(), e);
        } catch (Exception e) {
            log.error("multi-track collision request convert error, request is {}", request, e);
            throw new ArgumentException("multi-track collision request convert error", e);
        }
    }
}
