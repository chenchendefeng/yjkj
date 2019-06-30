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
import com.jiayi.platform.judge.dao.impala.LineCollisionDao;
import com.jiayi.platform.judge.dto.LineCollisionDto;
import com.jiayi.platform.judge.manage.ImpalaTableManager;
import com.jiayi.platform.judge.query.LineCollisionQuery;
import com.jiayi.platform.judge.request.JudgeRequest;
import com.jiayi.platform.judge.request.LineCollisionRequest;
import com.jiayi.platform.judge.response.LineCollisionResponse;
import com.jiayi.platform.judge.service.ExportService;
import com.jiayi.platform.judge.util.DeviceUtil;
import com.jiayi.platform.judge.util.ImpalaDataUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : weichengke
 * @date : 2019-04-20 15:34
 */
@Component
@Slf4j
public class LineCollisionExecutor implements JudgeExecutor {

    @Autowired
    private LineCollisionDao lineCollisionDao;

    @Autowired
    private ImpalaTableManager impalaTableManager;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private ObjectOrganizationManager objectOrganizationManager;

    @Override
    public <T extends JudgeRequest> List<?> query(T request, PageRequest pageRequest) {
        LineCollisionQuery query = new LineCollisionQuery();
        buildLineCollisionQuery(query, (LineCollisionRequest) request);
        // 有选择区域但区域内设备数为0，则直接返回空
        if (((LineCollisionRequest) request).getAreaList() != null && ((LineCollisionRequest) request).getAreaList().size() != 0
                && query.getDeviceIdList().size() == 0)
            return new ArrayList<LineCollisionDto>();
        query.setLimit(pageRequest.getPageSize());
        query.setOffset(pageRequest.calOffset());
        return lineCollisionDao.selectLineCollision(query);
    }

    @Override
    public <T extends JudgeRequest> long count(T request) {
        LineCollisionQuery query = new LineCollisionQuery();
        buildLineCollisionQuery(query, (LineCollisionRequest) request);
        // 有选择区域但区域内设备数为0，则直接返回空
        if (((LineCollisionRequest) request).getAreaList() != null && ((LineCollisionRequest) request).getAreaList().size() != 0
                && query.getDeviceIdList().size() == 0)
            return 0;
        return lineCollisionDao.countLineCollision(query);
    }

    @Override
    public List<?> queryCache(Long queryHistoryId, PageRequest pageRequest) {
        return lineCollisionDao.selectLineResult(queryHistoryId, pageRequest.getPageSize(), pageRequest.calOffset());
    }

    @Override
    public long countCache(Long queryHistoryId) {
        return lineCollisionDao.countLineResult(queryHistoryId);
    }

    @Override
    public <T extends JudgeRequest> long cache(T request, long queryHistoryId) {
        LineCollisionQuery query = new LineCollisionQuery();
        buildLineCollisionQuery(query, (LineCollisionRequest) request);
        // 有选择区域但区域内设备数为0，则直接返回空
        if (((LineCollisionRequest) request).getAreaList() != null && ((LineCollisionRequest) request).getAreaList().size() != 0
                && query.getDeviceIdList().size() == 0)
            return 0;
        query.setUid(queryHistoryId);
        lineCollisionDao.insertLineResult(query);
        return countCache(queryHistoryId);
    }

    @Override
    public <T extends JudgeRequest> PageResult convert2Response(List<?> dots, long count, T request, PageRequest pageRequest) {
        List<LineCollisionResponse> responseList = new ArrayList<>();
        List<String> objectValues = new ArrayList<>();
        Map<Long, Device> deviceMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(dots)) {
            Set<Long> deviceIds = new HashSet<>();
            deviceIds.addAll(dots.stream().map(dto -> ((LineCollisionDto) dto).getFromDeviceId()).filter(Objects::nonNull)
                    .collect(Collectors.toSet()));
            deviceIds.addAll(dots.stream().map(dto -> ((LineCollisionDto) dto).getToDeviceId()).filter(Objects::nonNull)
                    .collect(Collectors.toSet()));
            deviceMap.putAll(deviceService.findByIds(deviceIds));
        }
        dots.forEach(dto -> {
            LineCollisionDto item = (LineCollisionDto) dto;
            LineCollisionResponse response = new LineCollisionResponse();
            response.setObjectId(item.getObjectValue());
            response.setObjectValue(ImpalaDataUtil.addMacCodeColons(item.getObjectValue(), request.getObjectTypeName()));
            response.setMatchCount(item.getMatchCount());
            response.setUniqueDevCount(item.getUniqueDevCount());
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
        List<LineCollisionDto> data = (lineCollisionDao.selectLineResult(queryId, ExportService.LOAD_SIZE, offset));

        Map<Long, Device> deviceMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(data)) {
            Set<Long> deviceIds = new HashSet<>();
            deviceIds.addAll(data.stream().map(LineCollisionDto::getFromDeviceId).filter(Objects::nonNull).collect(Collectors.toSet()));
            deviceIds.addAll(data.stream().map(LineCollisionDto::getToDeviceId).filter(Objects::nonNull).collect(Collectors.toSet()));
            deviceMap.putAll(deviceService.findByIds(deviceIds));
        }
        int colCount = 8;
        List<String[]> rowData = new ArrayList<>();
        for (LineCollisionDto datum : data) {
            String[] rowValue = new String[colCount];
            int j = 0;
            rowValue[j++] = ImpalaDataUtil.addMacCodeColons(datum.getObjectValue(), request.getObjectTypeName()) + "\t";
            rowValue[j++] = datum.getUniqueDevCount().toString();
            rowValue[j++] = datum.getMatchCount().toString();
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

    private void buildLineCollisionQuery(LineCollisionQuery query, LineCollisionRequest request) {
        try {
            List<Device> devices = deviceService.findByCollectId(CollectType.valueOf(request.getObjectTypeName().toUpperCase()).code());
            Set<Long> deviceSet = DeviceUtil.selectDeviceIdInAreasByType(request.getAreaList(), devices);
            query.setDeviceIdList(deviceSet);
            Map<String, Pair<Long, Long>> tableNamesAndTime =
                    impalaTableManager.getValidTableList("collision", request.getBeginDate(), request.getEndDate());
            query.setTableNameList(tableNamesAndTime.keySet());
            // 设置临时表和碰撞表的查询时间及网格
            for (Map.Entry<String, Pair<Long, Long>> namesAndTime : tableNamesAndTime.entrySet()) {
                long beginDate = namesAndTime.getValue().getLeft();
                long endDate = namesAndTime.getValue().getRight();
                if (namesAndTime.getKey().contains("recent")) {
                    query.setRecentBeginDate(beginDate);
                    query.setRecentEndDate(endDate);
                    query.setRecentBeginHours(beginDate / (ImpalaTableManager.SPLIT_HOURS * 60 * 60 * 1000L));
                    query.setRecentEndHours(endDate / (ImpalaTableManager.SPLIT_HOURS * 60 * 60 * 1000L));
                } else {
                    query.setBeginDate(beginDate);
                    query.setEndDate(endDate);
                    query.setBeginHours(beginDate / (ImpalaTableManager.SPLIT_HOURS * 60 * 60 * 1000L));
                    query.setEndHours(endDate / (ImpalaTableManager.SPLIT_HOURS * 60 * 60 * 1000L));
//                    query.setGridList(DeviceUtil.getGridSetFromDevices(deviceSet, devices));
                }
            }
            query.setTrackType(CollectType.valueOf(request.getObjectTypeName().toUpperCase()).code());
            query.setMatchCount(request.getMatchCount());
            if (request.getObjectValueList() != null) {
                query.setObjectValueList(request.getObjectValueList().stream()
                        .map(objVal -> objVal.toUpperCase().replaceAll("[-:：\\s]", ""))
                        .filter(StringUtils::isNotBlank).collect(Collectors.toSet()));
            }
        } catch (ArgumentException e) {
            throw new ArgumentException(e.getMessage(), e);
        } catch (Exception e) {
            log.error("line collision request convert error, request is {}", request, e);
            throw new ArgumentException("line collision request convert error", e);
        }
    }
}
