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
import com.jiayi.platform.judge.dao.impala.DeviceAnalysisDao;
import com.jiayi.platform.judge.dto.DeviceAnalysisDto;
import com.jiayi.platform.judge.manage.ImpalaTableManager;
import com.jiayi.platform.judge.query.DeviceAnalysisQuery;
import com.jiayi.platform.judge.request.DeviceAnalysisRequest;
import com.jiayi.platform.judge.request.JudgeRequest;
import com.jiayi.platform.judge.response.DeviceAnalysisResponse;
import com.jiayi.platform.judge.service.ExportService;
import com.jiayi.platform.judge.util.DeviceUtil;
import com.jiayi.platform.judge.util.ImpalaDataUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class DeviceAnalysisExecutor implements JudgeExecutor {
    @Autowired
    private DeviceAnalysisDao deviceAnalysisDao;

    @Autowired
    private ImpalaTableManager impalaTableManager;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private ObjectOrganizationManager objectOrganizationManager;

    @Override
    public <T extends JudgeRequest> List<?> query(T request, PageRequest pageRequest) {
        DeviceAnalysisQuery query = new DeviceAnalysisQuery();
        buildDeviceAnalysisQuery(query, (DeviceAnalysisRequest) request);
        // 有选择区域但区域内设备数为0，则直接返回空
        if (((DeviceAnalysisRequest) request).getAreaList() != null && ((DeviceAnalysisRequest) request).getAreaList().size() != 0
                && query.getDeviceIdList().size() == 0)
            return new ArrayList<DeviceAnalysisDto>();
        query.setLimit(pageRequest.getPageSize());
        query.setOffset(pageRequest.calOffset());
        return deviceAnalysisDao.selectDeviceAnalysis(query);
    }

    @Override
    public <T extends JudgeRequest> long count(T request) {
        DeviceAnalysisQuery query = new DeviceAnalysisQuery();
        buildDeviceAnalysisQuery(query, (DeviceAnalysisRequest) request);
        // 有选择区域但区域内设备数为0，则直接返回空
        if (((DeviceAnalysisRequest) request).getAreaList() != null && ((DeviceAnalysisRequest) request).getAreaList().size() != 0
                && query.getDeviceIdList().size() == 0)
            return 0;
        return deviceAnalysisDao.countDeviceAnalysis(query);
    }

    @Override
    public List<?> queryCache(Long queryHistoryId, PageRequest pageRequest) {
        return deviceAnalysisDao.selectDeviceAnalysisResult(queryHistoryId, pageRequest.getPageSize(), pageRequest.calOffset());
    }

    @Override
    public long countCache(Long queryHistoryId) {
        return deviceAnalysisDao.countDeviceAnalysisResult(queryHistoryId);
    }

    @Override
    public <T extends JudgeRequest> long cache(T request, long queryHistoryId) {
        DeviceAnalysisQuery query = new DeviceAnalysisQuery();
        buildDeviceAnalysisQuery(query, (DeviceAnalysisRequest) request);
        // 有选择区域但区域内设备数为0，则直接返回空
        if (((DeviceAnalysisRequest) request).getAreaList() != null && ((DeviceAnalysisRequest) request).getAreaList().size() != 0
                && query.getDeviceIdList().size() == 0)
            return 0;
        query.setUid(queryHistoryId);
        deviceAnalysisDao.insertDeviceAnalysisResult(query);
        return countCache(queryHistoryId);
    }

    @Override
    public <T extends JudgeRequest> PageResult convert2Response(List<?> dots, long count, T request, PageRequest pageRequest) {
        List<DeviceAnalysisResponse> responseList = new ArrayList<>();
        dots.forEach(dto -> {
            DeviceAnalysisDto item = (DeviceAnalysisDto) dto;
            DeviceAnalysisResponse response = new DeviceAnalysisResponse();
            response.setObjectId(item.getObjectValue());
            response.setObjectValue(ImpalaDataUtil.addMacCodeColons(item.getObjectValue(), request.getObjectTypeName()));
            response.setMatchCount(item.getMatchCount());
            response.setMinHappenDate(item.getMinHappenAt());
            response.setMaxHappenDate(item.getMaxHappenAt());
            response.setImsiImei(item.getImsiImei() == null ? "" : item.getImsiImei());
            ObjectOrganizationInfo organization = objectOrganizationManager.getObjectOrganization(request.getObjectTypeName(), item.getObjectValue());
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
        List<DeviceAnalysisDto> data = (deviceAnalysisDao.selectDeviceAnalysisResult(queryId, ExportService.LOAD_SIZE, offset));

        int colCount = 5;
        List<String[]> rowData = new ArrayList<>();
        for (DeviceAnalysisDto datum : data) {
            String[] rowValue = new String[colCount];
            int j = 0;
            rowValue[j++] = ImpalaDataUtil.addMacCodeColons(datum.getObjectValue(), request.getObjectTypeName()) + "\t";
            rowValue[j++] = datum.getMatchCount().toString();
            rowValue[j++] = ExportService.SDF.format(datum.getMinHappenAt());
            rowValue[j++] = ExportService.SDF.format(datum.getMaxHappenAt());
            ObjectOrganizationInfo organization = objectOrganizationManager
                    .getObjectOrganization(request.getObjectTypeName(), datum.getObjectValue());
            rowValue[j] = (organization != null) ? organization.getOrganizationName() : "";
            rowData.add(rowValue);
        }
        contents.add(ExportUtil.genContentByStringList(rowData));
        return data.size();
    }

    private void buildDeviceAnalysisQuery(DeviceAnalysisQuery query, DeviceAnalysisRequest request) {
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
        } catch (ArgumentException e) {
            throw new ArgumentException(e.getMessage(), e);
        } catch (Exception e) {
            log.error("device analysis request convert error, request is {}", request, e);
            throw new ArgumentException("device analysis request convert error", e);
        }
    }
}
