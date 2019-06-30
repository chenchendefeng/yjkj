package com.jiayi.platform.judge.executor;

import com.jiayi.platform.basic.entity.Device;
import com.jiayi.platform.basic.service.DeviceService;
import com.jiayi.platform.common.enums.CollectType;
import com.jiayi.platform.common.exception.ArgumentException;
import com.jiayi.platform.common.web.util.ExportUtil;
import com.jiayi.platform.judge.common.bean.PageRequest;
import com.jiayi.platform.judge.dao.impala.AreaCollisionDao;
import com.jiayi.platform.judge.dto.AreaConditionDto;
import com.jiayi.platform.judge.manage.ImpalaTableManager;
import com.jiayi.platform.judge.query.AreaConditionBean;
import com.jiayi.platform.judge.query.AreaConditionQuery;
import com.jiayi.platform.judge.request.AreaCondition;
import com.jiayi.platform.judge.request.AreaConditionRequest;
import com.jiayi.platform.judge.request.JudgeDetailRequest;
import com.jiayi.platform.judge.response.AreaConditionResponse;
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
public class AreaConditionExecutor implements JudgeDetailExecutor {
    @Autowired
    private AreaCollisionDao areaCollisionDao;

    @Autowired
    private ImpalaTableManager impalaTableManager;
    @Autowired
    private DeviceService deviceService;

    @Override
    public <T> List<?> query(T request, PageRequest pageRequest) {
        AreaConditionQuery query = new AreaConditionQuery();
        buildAreaConditionQuery(query, (AreaConditionRequest) request);
        query.setLimit(pageRequest.getPageSize());
        query.setOffset(pageRequest.calOffset());
        return areaCollisionDao.selectAreaCondition(query);
    }

    @Override
    public <T> long count(T request) {
        AreaConditionQuery query = new AreaConditionQuery();
        buildAreaConditionQuery(query, (AreaConditionRequest) request);
        return areaCollisionDao.countAreaCondition(query);
    }

    @Override
    public List<?> convert2Response(List<?> dots, String objectType) {
        List<AreaConditionResponse> responseList = new ArrayList<>();
        dots.forEach(dto -> {
            AreaConditionDto item = (AreaConditionDto) dto;
            AreaConditionResponse response = new AreaConditionResponse();
            response.setObjectId(item.getObjectValue());
            response.setObjectValue(ImpalaDataUtil.addMacCodeColons(item.getObjectValue(), objectType));
            response.setMatchCondition(item.getMatchCondition());
            response.setBeginDate(item.getMinHappenAt());
            response.setEndDate(item.getMaxHappenAt());

            responseList.add(response);
        });
        return responseList;
    }

    @Override
    public <T extends JudgeDetailRequest> int exportResult(List<String> contents, T request, long offset) {
        AreaConditionQuery query = new AreaConditionQuery();
        buildAreaConditionQuery(query, (AreaConditionRequest) request);
        query.setLimit(ExportService.LOAD_SIZE);
        query.setOffset(offset);
        List<AreaConditionDto> data = areaCollisionDao.selectAreaCondition(query);

        int colCount = 3;
        List<String[]> rowData = new ArrayList<>();
        for (AreaConditionDto datum : data) {
            String[] rowValue = new String[colCount];
            int j = 0;
            rowValue[j++] = datum.getMatchCondition();
            rowValue[j++] = ExportService.SDF.format(datum.getMinHappenAt());
            rowValue[j] = ExportService.SDF.format(datum.getMaxHappenAt());
            rowData.add(rowValue);
        }
        contents.add(ExportUtil.genContentByStringList(rowData));
        return data.size();
    }

    private void buildAreaConditionQuery(AreaConditionQuery query, AreaConditionRequest request) {
        try {
            List<Device> devices = deviceService.findByCollectId(CollectType.valueOf(request.getObjectTypeName().toUpperCase()).code());
            query.setObjectValue(request.getObjectId().toUpperCase().replaceAll("[-:：\\s]", ""));
            query.setObjectHash(ImpalaDataUtil.getObjectHash(query.getObjectValue()));
            List<AreaConditionBean> conditionList = new ArrayList<>();
            for (AreaCondition cond : request.getConditionList()) {
                AreaConditionBean condBean = new AreaConditionBean();
                Set<Long> deviceSet = DeviceUtil.selectDeviceIdInAreasByType(cond.getAreaList(), devices);
                condBean.setDeviceIdList(deviceSet);
                Map<String, Pair<Long, Long>> tableNamesAndTime =
                        impalaTableManager.getValidTableList("query", cond.getBeginDate(), cond.getEndDate());
                condBean.setTableNameList(tableNamesAndTime.keySet());
                // 设置临时表和查询表的查询时间及网格
                for (Map.Entry<String, Pair<Long, Long>> namesAndTime : tableNamesAndTime.entrySet()) {
                    long beginDate = namesAndTime.getValue().getLeft();
                    long endDate = namesAndTime.getValue().getRight();
                    if (namesAndTime.getKey().contains("recent")) {
                        condBean.setRecentBeginDate(beginDate);
                        condBean.setRecentEndDate(endDate);
                        condBean.setRecentBeginHours(beginDate / (ImpalaTableManager.QUERY_SPLIT_HOURS * 60 * 60 * 1000L));
                        condBean.setRecentEndHours(endDate / (ImpalaTableManager.QUERY_SPLIT_HOURS * 60 * 60 * 1000L));
                    } else {
                        condBean.setBeginDate(beginDate);
                        condBean.setEndDate(endDate);
                        condBean.setBeginHours(beginDate / (ImpalaTableManager.QUERY_SPLIT_HOURS * 60 * 60 * 1000L));
                        condBean.setEndHours(endDate / (ImpalaTableManager.QUERY_SPLIT_HOURS * 60 * 60 * 1000L));
                    }
                }
                condBean.setCondName(cond.getConditionName());
                conditionList.add(condBean);
            }
            query.setConditionList(conditionList);
            query.setTrackType(CollectType.valueOf(request.getObjectTypeName().toUpperCase()).code());
        } catch (ArgumentException e) {
            throw new ArgumentException(e.getMessage(), e);
        }
        catch (Exception e) {
            log.error("area condition request convert error, request is {}", request, e);
            throw new ArgumentException("area condition request convert error", e);
        }
    }
}
