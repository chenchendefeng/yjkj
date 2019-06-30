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
import com.jiayi.platform.judge.dao.impala.AppearCollisionDao;
import com.jiayi.platform.judge.dto.AppearCollisionDto;
import com.jiayi.platform.judge.manage.ImpalaTableManager;
import com.jiayi.platform.judge.query.AppearCollisionQuery;
import com.jiayi.platform.judge.request.AppearCollisionRequest;
import com.jiayi.platform.judge.request.JudgeRequest;
import com.jiayi.platform.judge.response.AppearCollisionResponse;
import com.jiayi.platform.judge.service.ExportService;
import com.jiayi.platform.judge.util.DeviceUtil;
import com.jiayi.platform.judge.util.ImpalaDataUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Primary
@Slf4j
public class AppearCollisionExecutor implements JudgeExecutor {

    @Autowired
    private AppearCollisionDao appearCollisionDao;

    @Autowired
    private ImpalaTableManager impalaTableManager;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private ObjectOrganizationManager objectOrganizationManager;

    @Override
    public <T extends JudgeRequest> List<?> query(T request, PageRequest pageRequest) {
        AppearCollisionQuery query = new AppearCollisionQuery();
        buildAppearQuery(query, (AppearCollisionRequest) request);
        // 有选择区域但区域内设备数为0，则直接返回空
        if (((AppearCollisionRequest) request).getAnalyzeAreaList() != null && ((AppearCollisionRequest) request).getAnalyzeAreaList().size() != 0
                && query.getAnalyzeDeviceIdList().size() == 0)
            return new ArrayList<AppearCollisionDto>();
        query.setLimit(pageRequest.getPageSize());
        query.setOffset(pageRequest.calOffset());
        return appearCollisionDao.selectAppear(query);
    }

    @Override
    public <T extends JudgeRequest> long count(T request) {
        AppearCollisionQuery query = new AppearCollisionQuery();
        buildAppearQuery(query, (AppearCollisionRequest) request);
        // 有选择区域但区域内设备数为0，则直接返回空
        if (((AppearCollisionRequest) request).getAnalyzeAreaList() != null && ((AppearCollisionRequest) request).getAnalyzeAreaList().size() != 0
                && query.getAnalyzeDeviceIdList().size() == 0)
            return 0;
        return appearCollisionDao.countAppear(query);
    }

    @Override
    public List<?> queryCache(Long queryHistoryId, PageRequest pageRequest) {
        return appearCollisionDao.selectAppearResult(queryHistoryId, pageRequest.getPageSize(), pageRequest.calOffset());
    }

    @Override
    public long countCache(Long queryHistoryId) {
        return appearCollisionDao.countAppearResult(queryHistoryId);
    }

    @Override
    public <T extends JudgeRequest> long cache(T request, long queryHistoryId) {
        AppearCollisionQuery query = new AppearCollisionQuery();
        buildAppearQuery(query, (AppearCollisionRequest) request);
        // 有选择区域但区域内设备数为0，则直接返回空
        if (((AppearCollisionRequest) request).getAnalyzeAreaList() != null && ((AppearCollisionRequest) request).getAnalyzeAreaList().size() != 0
                && query.getAnalyzeDeviceIdList().size() == 0)
            return 0;
        query.setUid(queryHistoryId);
        appearCollisionDao.insertAppearResult(query);
        return countCache(queryHistoryId);
    }

    @Override
    public <T extends JudgeRequest> PageResult convert2Response(List<?> dots, long count, T request, PageRequest pageRequest) {
        List<AppearCollisionResponse> responseList = new ArrayList<>();
        List<String> objectValues = new ArrayList<>();
        Map<Long, Device> deviceMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(dots)) {
            Set<Long> deviceIds = new HashSet<>();
            deviceIds.addAll(dots.stream().map(dto -> ((AppearCollisionDto) dto).getFromDeviceId()).filter(Objects::nonNull)
                    .collect(Collectors.toSet()));
            deviceIds.addAll(dots.stream().map(dto -> ((AppearCollisionDto) dto).getToDeviceId()).filter(Objects::nonNull)
                    .collect(Collectors.toSet()));
            deviceMap.putAll(deviceService.findByIds(deviceIds));
        }
        dots.forEach(dto -> {
            AppearCollisionDto item = (AppearCollisionDto) dto;
            AppearCollisionResponse response = new AppearCollisionResponse();
            response.setObjectId(item.getObjectValue());
            response.setObjectValue(ImpalaDataUtil.addMacCodeColons(item.getObjectValue(), request.getObjectTypeName()));
            response.setBeginDate(item.getMinHappenAt());
            response.setEndDate(item.getMaxHappenAt());
            if (item.getFromDeviceId() != null && deviceMap.get(item.getFromDeviceId()) != null)
                response.setBeginAddress(deviceMap.get(item.getFromDeviceId()).getAddress());
            if (item.getToDeviceId() != null && deviceMap.get(item.getToDeviceId()) != null)
                response.setEndAddress(deviceMap.get(item.getToDeviceId()).getAddress());
            response.setImsiImei(item.getImsiImei() == null ? "" : item.getImsiImei());
            ObjectOrganizationInfo organization = objectOrganizationManager.getObjectOrganization(request.getObjectTypeName(), item.getObjectValue());
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
        List<AppearCollisionDto> data = (appearCollisionDao.selectAppearResult(queryId, ExportService.LOAD_SIZE, offset));

        Map<Long, Device> deviceMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(data)) {
            Set<Long> deviceIds = new HashSet<>();
            deviceIds.addAll(data.stream().map(AppearCollisionDto::getFromDeviceId).filter(Objects::nonNull).collect(Collectors.toSet()));
            deviceIds.addAll(data.stream().map(AppearCollisionDto::getToDeviceId).filter(Objects::nonNull).collect(Collectors.toSet()));
            deviceMap.putAll(deviceService.findByIds(deviceIds));
        }
        int colCount = 6;
        List<String[]> rowData = new ArrayList<>();
        for (AppearCollisionDto datum : data) {
            String[] rowValue = new String[colCount];
            int j = 0;
            rowValue[j++] = ImpalaDataUtil.addMacCodeColons(datum.getObjectValue(), request.getObjectTypeName()) + "\t";
            rowValue[j++] = ExportService.SDF.format(datum.getMinHappenAt());
            rowValue[j++] = ExportService.SDF.format(datum.getMaxHappenAt());
            rowValue[j++] = (datum.getFromDeviceId() != null && deviceMap.get(datum.getFromDeviceId()) != null)
                    ? deviceMap.get(datum.getFromDeviceId()).getAddress()
                    : "";
            rowValue[j++] = (datum.getToDeviceId() != null && deviceMap.get(datum.getToDeviceId()) != null)
                    ? deviceMap.get(datum.getToDeviceId()).getAddress()
                    : "";
            ObjectOrganizationInfo organization = objectOrganizationManager
                    .getObjectOrganization(request.getObjectTypeName(), datum.getObjectValue());
            rowValue[j] = (organization != null) ? organization.getOrganizationName() : "";
            rowData.add(rowValue);
        }
        contents.add(ExportUtil.genContentByStringList(rowData));
        return data.size();
    }

    private void buildAppearQuery(AppearCollisionQuery query, AppearCollisionRequest request) {
        try {
            Pair<Long, Long> refBeginAndEndDate = getRefBeginAndEndDate(request);
            List<Device> devices = deviceService.findByCollectId(CollectType.valueOf(request.getObjectTypeName().toUpperCase()).code());
            Set<Long> analyzeDeviceSet = DeviceUtil.selectDeviceIdInAreasByType(request.getAnalyzeAreaList(), devices);
            Set<Long> refDeviceSet = DeviceUtil.selectDeviceIdInAreasByType(request.getRefAreaList(), devices);
            query.setAnalyzeDeviceIdList(analyzeDeviceSet);
            query.setRefDeviceIdList(refDeviceSet);
            Map<String, Pair<Long, Long>> analyzeTableNamesAndTime =
                    impalaTableManager.getValidTableList("collision", request.getAnalyzeBeginDate(), request.getAnalyzeEndDate());
            query.setAnalyzeTableList(analyzeTableNamesAndTime.keySet());
            // 设置临时表和碰撞表的查询时间及网格
            for (Map.Entry<String, Pair<Long, Long>> namesAndTime : analyzeTableNamesAndTime.entrySet()) {
                long beginDate = namesAndTime.getValue().getLeft();
                long endDate = namesAndTime.getValue().getRight();
                if (namesAndTime.getKey().contains("recent")) {
                    query.setAnalyzeRecentBeginDate(beginDate);
                    query.setAnalyzeRecentEndDate(endDate);
                    query.setAnalyzeRecentBeginHours(beginDate / (ImpalaTableManager.SPLIT_HOURS * 60 * 60 * 1000L));
                    query.setAnalyzeRecentEndHours(endDate / (ImpalaTableManager.SPLIT_HOURS * 60 * 60 * 1000L));
                } else {
                    query.setAnalyzeBeginDate(beginDate);
                    query.setAnalyzeEndDate(endDate);
                    query.setAnalyzeBeginHours(beginDate / (ImpalaTableManager.SPLIT_HOURS * 60 * 60 * 1000L));
                    query.setAnalyzeEndHours(endDate / (ImpalaTableManager.SPLIT_HOURS * 60 * 60 * 1000L));
//                    query.setAnalyzeGridList(DeviceUtil.getGridSetFromDevices(analyzeDeviceSet, devices));
                }
            }
            Map<String, Pair<Long, Long>> refTableNamesAndTime =
                    impalaTableManager.getValidTableList("collision", refBeginAndEndDate.getLeft(), refBeginAndEndDate.getRight());
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
//                    query.setRefGridList(DeviceUtil.getGridSetFromDevices(analyzeDeviceSet, devices));
                }
            }
            query.setTrackType(CollectType.valueOf(request.getObjectTypeName().toUpperCase()).code());
            query.setSelectRefArea(request.getRefAreaList() != null && request.getRefAreaList().size() > 0);
            if (request.getObjectValueList() != null) {
                query.setObjectValueList(request.getObjectValueList().stream()
                        .map(objVal -> objVal.toUpperCase().replaceAll("[-:：\\s]", ""))
                        .filter(StringUtils::isNotBlank).collect(Collectors.toSet()));
            }
        } catch (ArgumentException e) {
            throw new ArgumentException(e.getMessage(), e);
        } catch (Exception e) {
                log.error("appear/disappear collision request convert error, request is {}", request, e);
                throw new ArgumentException("appear/disappear collision request convert error", e);
        }
    }

    protected Pair<Long, Long> getRefBeginAndEndDate(AppearCollisionRequest request) {
        long refBeginDate = request.getAnalyzeBeginDate() - ((long) request.getRefDuration() * 24L + (long) request.getBufferTime()) * 3600L * 1000L;
        long refEndDate = request.getAnalyzeBeginDate() - (long) request.getBufferTime() * 3600L * 1000L;
        return Pair.of(refBeginDate, refEndDate);
    }
}
