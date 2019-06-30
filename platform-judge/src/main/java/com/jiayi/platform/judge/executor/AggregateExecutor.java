package com.jiayi.platform.judge.executor;

import com.jiayi.platform.basic.entity.Device;
import com.jiayi.platform.basic.service.DeviceService;
import com.jiayi.platform.common.exception.ArgumentException;
import com.jiayi.platform.common.exception.DBException;
import com.jiayi.platform.common.exception.ServiceException;
import com.jiayi.platform.common.util.JsonUtils;
import com.jiayi.platform.common.web.util.ExportUtil;
import com.jiayi.platform.judge.common.bean.PageInfo;
import com.jiayi.platform.judge.common.bean.PageRequest;
import com.jiayi.platform.judge.common.bean.PageResult;
import com.jiayi.platform.judge.dao.impala.AggregateDao;
import com.jiayi.platform.judge.dao.mysql.CollisionResultFieldDao;
import com.jiayi.platform.judge.dto.AggregateDto;
import com.jiayi.platform.judge.entity.mysql.CollisionResultField;
import com.jiayi.platform.judge.entity.mysql.QueryHistory;
import com.jiayi.platform.judge.enums.JudgeStatus;
import com.jiayi.platform.judge.enums.RequestType;
import com.jiayi.platform.judge.manage.HistoryQueryResultManager;
import com.jiayi.platform.judge.manage.RequestHistoryManager;
import com.jiayi.platform.judge.manage.SaveResultFieldManager;
import com.jiayi.platform.judge.query.AggregateMiningParam;
import com.jiayi.platform.judge.query.AggregateQuery;
import com.jiayi.platform.judge.query.AggregateQueryBean;
import com.jiayi.platform.judge.request.*;
import com.jiayi.platform.judge.response.QueryResultFieldInfo;
import com.jiayi.platform.judge.service.ExportService;
import com.jiayi.platform.judge.util.ImpalaDataUtil;
import com.jiayi.platform.library.minerepo.entity.MiningRepo;
import com.jiayi.platform.library.minerepo.manager.MiningRepoCacheManager;
import com.jiayi.platform.library.minerepo.vo.MiningRepoTableDesc;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class AggregateExecutor implements JudgeExecutor {

    @Autowired
    private AggregateDao aggregateDao;

    @Autowired
    private RequestHistoryManager requestHistoryManager;
    @Autowired
    private SaveResultFieldManager saveResultFieldManager;
    @Autowired
    private MiningRepoCacheManager miningRepoCacheManager;
    @Autowired
    private HistoryQueryResultManager historyQueryResultManager;
    @Autowired
    private CollisionResultFieldDao collisionResultFieldDao;
    @Autowired
    private DeviceService deviceService;

    @Override
    public <T extends JudgeRequest> List<?> query(T request, PageRequest pageRequest) {
        AggregateQuery query = new AggregateQuery();
        buildAggregateQuery(query, (AggregateRequest) request);
        query.setLimit(pageRequest.getPageSize());
        query.setOffset(pageRequest.calOffset());
        return aggregateDao.selectAggregate(query);
    }

    @Override
    public <T extends JudgeRequest> long count(T request) {
        AggregateQuery query = new AggregateQuery();
        buildAggregateQuery(query, (AggregateRequest) request);
        return aggregateDao.countAggregate(query);
    }

    @Override
    public List<?> queryCache(Long queryHistoryId, PageRequest pageRequest) {
        return aggregateDao.selectAggregateResult(queryHistoryId, pageRequest.getPageSize(), pageRequest.calOffset());
    }

    @Override
    public long countCache(Long queryHistoryId) {
        return aggregateDao.countAggregateResult(queryHistoryId);
    }

    @Override
    public <T extends JudgeRequest> long cache(T request, long queryHistoryId) {
        AggregateQuery query = new AggregateQuery();
        buildAggregateQuery(query, (AggregateRequest) request);
        query.setUid(queryHistoryId);
        aggregateDao.insertAggregateResult(query);
        // 保存二次碰撞结果集字段
        List<CollisionResultField> resultFields = new ArrayList<>();
        if (query.getReferIndex() == -1) {
            CollisionResultField resultFieldInfo = new CollisionResultField();
            resultFieldInfo.setFieldName("result_value");
            resultFieldInfo.setFieldDesc(((AggregateRequest) request).getResultName());
            resultFieldInfo.setFieldType(((AggregateRequest) request).getRequestTmpls().get(0).getFieldType());
            resultFieldInfo.setRequestTmplId(queryHistoryId);
            resultFieldInfo.setRequestType(RequestType.AGGREGATE_COLLISION.typeName());
            resultFields.add(resultFieldInfo);
        } else {
            for (AggregateRequestFieldInfo fieldInfo : ((AggregateRequest) request).getRequestTmpls()) {
                if (fieldInfo.getReferTo() == 1) {
                    long requestId = fieldInfo.getUid();
                    resultFields.addAll(saveResultFieldManager.getResultFieldList(requestId));
                    for (int i = 0; i < resultFields.size(); i++) {
                        resultFields.get(i).setId(null);
                        resultFields.get(i).setFieldName("v" + (i + 1));
                        resultFields.get(i).setRequestTmplId(queryHistoryId);
                        resultFields.get(i).setRequestType(RequestType.AGGREGATE_COLLISION.typeName());
                    }
                }
            }
        }
        try {
            collisionResultFieldDao.saveAll(resultFields);
        } catch (Exception e) {
            throw new DBException("aggregate collision result field info mysql insert error", e);
        }

        return countCache(queryHistoryId);
    }

    // NOTE: 查看二次碰撞结果时调用convert2AggregateResponse方法
    @Override
    public <T extends JudgeRequest> PageResult convert2Response(List<?> dots, long count, T request, PageRequest pageRequest) {
        AggregateQuery query = new AggregateQuery();
        buildAggregateQuery(query, (AggregateRequest) request);
        List<Map<String, String>> responseList = new ArrayList<>();
        dots.forEach(dto -> {
            AggregateDto item = (AggregateDto) dto;
            Map<String, String> resultMap = new TreeMap<>();
            if (query.getReferIndex() == -1)
                resultMap.put("resultValue", item.getResultValue());
            else {
                List<String> resultValues = Arrays.asList(item.getV1(), item.getV2(), item.getV3(), item.getV4(), item.getV5(), item.getV6(),
                        item.getV7(), item.getV8(), item.getV9(), item.getV10(), item.getV11(), item.getV12(), item.getV13(), item.getV14(),
                        item.getV15(), item.getV16(), item.getV17(), item.getV18(), item.getV19(), item.getV20(), item.getV21(), item.getV22(),
                        item.getV23(), item.getV24(), item.getV25(), item.getV26(), item.getV27(), item.getV28(), item.getV29(), item.getV30());
                for (int i = 0; i < query.getResultColumns().size(); i++) {
                    resultMap.put("v" + (i + 1), resultValues.get(i));
                }
            }
            responseList.add(resultMap);
        });

        PageInfo pageInfo = new PageInfo(responseList.size(), count, pageRequest);
        return new PageResult<>(responseList, pageInfo);
    }

    public <T extends JudgeRequest> PageResult convert2AggregateResponse(List<?> dots, long count, T request, PageRequest pageRequest,
                                                                         List<QueryResultFieldInfo> resultFieldInfo) {
        Map<String, String> fieldMap = resultFieldInfo.stream().collect(Collectors.toMap(QueryResultFieldInfo::getId, QueryResultFieldInfo::getText));

        Map<Long, Device> deviceMap = new HashMap<>();
        if (fieldMap.values().stream().anyMatch(field -> field.contains("地点"))) {
            deviceMap.putAll(deviceService.findAll().stream().collect(Collectors.toMap(Device::getId, Function.identity(), (a, b) -> a)));
        }

        AggregateQuery query = new AggregateQuery();
        buildAggregateQuery(query, (AggregateRequest) request);
        List<Map<String, String>> responseList = new ArrayList<>();
        dots.forEach(dto -> {
            AggregateDto item = (AggregateDto) dto;
            Map<String, String> resultMap = new TreeMap<>();
            if (query.getReferIndex() == -1) {
                String resultValue = item.getResultValue();
                if (fieldMap.getOrDefault("resultValue", "").contains("时间") && StringUtils.isNumeric(resultValue)) {
                    resultValue = ExportService.SDF.format(resultValue);
                } else if (fieldMap.getOrDefault("resultValue", "").contains("MAC")) {
                    resultValue = ImpalaDataUtil.addMacCodeColons(resultValue, "mac");
                } else if (fieldMap.getOrDefault("resultValue", "").contains("地点")) {
                    try {
                        long devId = Long.parseLong(resultValue);
                        resultValue = deviceMap.get(devId).getAddress();
                    } catch (Exception e) {
                        log.info(resultValue + " is not device id");
                    }
                }
                resultMap.put("resultValue", resultValue);
            } else {
                List<String> resultValues = Arrays.asList(item.getV1(), item.getV2(), item.getV3(), item.getV4(), item.getV5(), item.getV6(),
                        item.getV7(), item.getV8(), item.getV9(), item.getV10(), item.getV11(), item.getV12(), item.getV13(), item.getV14(),
                        item.getV15(), item.getV16(), item.getV17(), item.getV18(), item.getV19(), item.getV20(), item.getV21(), item.getV22(),
                        item.getV23(), item.getV24(), item.getV25(), item.getV26(), item.getV27(), item.getV28(), item.getV29(), item.getV30());
                for (int i = 0; i < query.getResultColumns().size(); i++) {
                    String resultValue = resultValues.get(i);
                    if (fieldMap.getOrDefault("v" + (i + 1), "").contains("时间") && StringUtils.isNumeric(resultValue)) {
                        resultValue = ExportService.SDF.format(resultValue);
                    } else if (fieldMap.getOrDefault("v" + (i + 1), "").contains("MAC")) {
                        resultValue = ImpalaDataUtil.addMacCodeColons(resultValue, "mac");
                    } else if (fieldMap.getOrDefault("v" + (i + 1), "").contains("地点")) {
                        try {
                            long devId = Long.parseLong(resultValue);
                            resultValue = deviceMap.get(devId).getAddress();
                        } catch (Exception e) {
                            log.info(resultValue + " is not device id");
                        }
                    }
                    resultMap.put("v" + (i + 1), resultValue);
                }
            }
            responseList.add(resultMap);
        });

        PageInfo pageInfo = new PageInfo(responseList.size(), count, pageRequest);
        return new PageResult<>(responseList, pageInfo);
    }

    @Override
    public <T extends JudgeRequest> int exportResult(List<String> contents, T request, long offset, long queryId) {
        List<AggregateDto> data = (aggregateDao.selectAggregateResult(queryId, ExportService.LOAD_SIZE, offset));

        AggregateQuery query = new AggregateQuery();
        buildAggregateQuery(query, (AggregateRequest) request);

        List<QueryResultFieldInfo> resultFieldInfo = historyQueryResultManager.getAggregateResultFieldInfo(queryId);
        Map<String, String> fieldMap = resultFieldInfo.stream().collect(Collectors.toMap(QueryResultFieldInfo::getId, QueryResultFieldInfo::getText));

        Map<Long, Device> deviceMap = new HashMap<>();
        if (fieldMap.values().stream().anyMatch(field -> field.contains("地点"))) {
            deviceMap.putAll(deviceService.findAll().stream().collect(Collectors.toMap(Device::getId, Function.identity(), (a, b) -> a)));
        }

        int colCount = (query.getReferIndex() == -1) ? 1 : query.getResultColumns().size();
        List<String[]> rowData = new ArrayList<>();
        for (AggregateDto datum : data) {
            String[] rowValue = new String[colCount];
            int j = 0;
            if (query.getReferIndex() == -1) {
                String resultValue = datum.getResultValue();
                if (fieldMap.getOrDefault("resultValue", "").contains("时间") && StringUtils.isNumeric(resultValue)) {
                    resultValue = ExportService.SDF.format(resultValue);
                } else if (fieldMap.getOrDefault("resultValue", "").contains("MAC")) {
                    resultValue = ImpalaDataUtil.addMacCodeColons(resultValue, "mac");
                } else if (fieldMap.getOrDefault("resultValue", "").contains("地点")) {
                    try {
                        long devId = Long.parseLong(resultValue);
                        resultValue = deviceMap.get(devId).getAddress();
                    } catch (Exception e) {
                        log.info(resultValue + " is not device id");
                    }
                }
                rowValue[j] = resultValue;
            } else {
                List<String> resultValues = Arrays.asList(datum.getV1(), datum.getV2(), datum.getV3(), datum.getV4(), datum.getV5(),
                        datum.getV6(), datum.getV7(), datum.getV8(), datum.getV9(), datum.getV10(), datum.getV11(), datum.getV12(),
                        datum.getV13(), datum.getV14(), datum.getV15(), datum.getV16(), datum.getV17(), datum.getV18(), datum.getV19(),
                        datum.getV20(), datum.getV21(), datum.getV22(), datum.getV23(), datum.getV24(), datum.getV25(), datum.getV26(),
                        datum.getV27(), datum.getV28(), datum.getV29(), datum.getV30());
                for (int i = 0; i < query.getResultColumns().size(); i++) {
                    String resultValue = resultValues.get(i);
                    if (fieldMap.getOrDefault("v" + (i + 1), "").contains("时间") && StringUtils.isNumeric(resultValue)) {
                        resultValue = ExportService.SDF.format(resultValue);
                    } else if (fieldMap.getOrDefault("v" + (i + 1), "").contains("MAC")) {
                        resultValue = ImpalaDataUtil.addMacCodeColons(resultValue, "mac");
                    } else if (fieldMap.getOrDefault("v" + (i + 1), "").contains("地点")) {
                        try {
                            long devId = Long.parseLong(resultValue);
                            resultValue = deviceMap.get(devId).getAddress();
                        } catch (Exception e) {
                            log.info(resultValue + " is not device id");
                        }
                    }
                    rowValue[j++] = resultValue;
                }
            }
            rowData.add(rowValue);
        }
        contents.add(ExportUtil.genContentByStringList(rowData));
        return data.size();
    }

    private void buildAggregateQuery(AggregateQuery query, AggregateRequest request) {
        if (request.getAggregateType().equalsIgnoreCase("subtract") || request.getAggregateType().equalsIgnoreCase("xor")) {
            if (request.getRequestTmpls().size() != 2) {
                throw new ArgumentException("subtract or XOR operation must have two queries only");
            }
        }
        query.setReferIndex(-1);
        int index = 0;
        for (AggregateRequestFieldInfo fieldInfo : request.getRequestTmpls()) {
            AggregateQueryBean queryBean = new AggregateQueryBean();
            long requestId = fieldInfo.getUid();
            QueryHistory aggrQueryHistory = requestHistoryManager.getQueryHistoryByRequestId(requestId);
            if (aggrQueryHistory == null) {
                throw new ArgumentException("invalid request id: " + requestId);
            }
            if (aggrQueryHistory.getStatus() != JudgeStatus.SUCCEED.code()) {
                throw new ServiceException("this query cannot perform aggregate collision! request type: "
                        + aggrQueryHistory.getRequestType() + ", request id: " + requestId);
            }
            String resultField = fieldInfo.getFieldName();
            AggregateMiningParam miningParam = new AggregateMiningParam();
            queryBean.setUid(aggrQueryHistory.getId());
            if (fieldInfo.getRequestType().equalsIgnoreCase(RequestType.MINING_REPO.typeName())) {
                MiningQueryHistoryParam param;
                try {
                    param = JsonUtils.parse(aggrQueryHistory.getRequestParameter(), MiningQueryHistoryParam.class);
                } catch (IOException e) {
                    throw new ArgumentException("invalid request parameter");
                }
                MiningRepo repo = miningRepoCacheManager.getMineReopById(param.getRepoId());
                MiningRepoTableDesc tableDesc = repo.getDetailTableDescObj();
                miningParam.setEndTime(param.getEndTime());
                miningParam.setStartTime(param.getStartTime());
                miningParam.setObjTypes(param.getObjTypes());
                miningParam.setTableName(tableDesc.getName());
                for (MiningRepoTableDesc.FieldDesc f : tableDesc.getFields()) {
                    if ("objType".equalsIgnoreCase(f.getUiType())) {
                        miningParam.setObjTypeFieldName(f.getName());
                    } else if ("startTime".equalsIgnoreCase(f.getUiType())) {
                        miningParam.setStartTimeFieldName(f.getName());
                    } else if ("endTime".equalsIgnoreCase(f.getUiType())) {
                        miningParam.setEndTimeFieldName(f.getName());
                    }
                }
                queryBean.setMiningParam(miningParam);
            } else if (fieldInfo.getRequestType().equalsIgnoreCase(RequestType.REPO_IMPORT.typeName())) {
                MonitorRepoParam param;
                try {
                    param = JsonUtils.parse(aggrQueryHistory.getRequestParameter(), MonitorRepoParam.class);
                } catch (IOException e) {
                    throw new ArgumentException("invalid request parameter");
                }
                if(param != null && param.getRepoId() != null) {
                    queryBean.setUid(param.getRepoId());
                }
            }
            queryBean.setCollisionType(fieldInfo.getRequestType());
            queryBean.setReferTo(fieldInfo.getReferTo() == 1);
            queryBean.setQueryValue(resultField);
            if (fieldInfo.getReferTo() == 1) { // 保留该数据源所有字段
                List<String> resultCols = saveResultFieldManager.getResultFieldList(requestId).stream()
                        .map(CollisionResultField::getFieldName).collect(Collectors.toList());
                query.setResultColumns(resultCols); // 保留的所有字段名
                query.setReferQueryColumn(resultField); // 该数据源的碰撞字段名
                query.setReferIndex(index); // 该数据源的index
                query.setReferCollisionType(queryBean.getCollisionType()); // 该数据源的来源类型
            }
            query.getQueryList().add(queryBean);
            index++;
        }
        query.setAggregateType(request.getAggregateType().toLowerCase());
        if (request.getRequestTmpls().size() > 0 && request.getRequestTmpls().get(0) != null)
            query.setResultType(request.getRequestTmpls().get(0).getFieldType());
    }
}
