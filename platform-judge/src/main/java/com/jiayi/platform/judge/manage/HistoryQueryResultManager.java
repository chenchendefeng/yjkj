package com.jiayi.platform.judge.manage;

import com.jiayi.platform.common.exception.ArgumentException;
import com.jiayi.platform.common.exception.DBException;
import com.jiayi.platform.common.util.JsonUtils;
import com.jiayi.platform.common.util.MyDateUtil;
import com.jiayi.platform.common.web.util.ExportUtil;
import com.jiayi.platform.judge.common.bean.PageInfo;
import com.jiayi.platform.judge.common.bean.PageRequest;
import com.jiayi.platform.judge.common.bean.PageResult;
import com.jiayi.platform.judge.dao.impala.FileImportDao;
import com.jiayi.platform.judge.dao.mysql.CollisionResultFieldDao;
import com.jiayi.platform.judge.entity.mysql.CollisionResultField;
import com.jiayi.platform.judge.entity.mysql.QueryHistory;
import com.jiayi.platform.judge.enums.FileImportTypeEnum;
import com.jiayi.platform.judge.enums.RequestType;
import com.jiayi.platform.judge.enums.ResultFieldEnum;
import com.jiayi.platform.judge.request.*;
import com.jiayi.platform.judge.response.QueryResultFieldInfo;
import com.jiayi.platform.judge.response.QueryResultResponse;
import com.jiayi.platform.judge.service.ExportService;
import com.jiayi.platform.judge.util.ImpalaDataUtil;
import com.jiayi.platform.library.minerepo.dto.MiningRepoDetailDto;
import com.jiayi.platform.library.minerepo.service.MiningRepoService;
import com.jiayi.platform.library.minerepo.vo.MiningRepoDetailSearchVO;
import com.jiayi.platform.library.minerepo.vo.MiningRepoTableDesc;
import com.jiayi.platform.library.monrepo.dto.MonitorObjectDto;
import com.jiayi.platform.library.monrepo.service.MonRepoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class HistoryQueryResultManager {

    @Autowired
    private MiningRepoService miningRepoService;
    @Autowired
    private MonRepoService monRepoService;
    @Autowired
    private CollisionResultFieldDao collisionResultFieldDao;
    @Autowired
    private FileImportDao fileImportDao;

    /**
     * 挖掘库导入的数据
     */
    public PageResult<QueryResultResponse> getMiningRepoResult(QueryHistory queryHistory, PageRequest pageRequest) {
        MiningQueryHistoryParam request;
        try {
            request = JsonUtils.parse(queryHistory.getRequestParameter(), MiningQueryHistoryParam.class);
        } catch (IOException e) {
            throw new ArgumentException("invalid request parameter");
        }
        MiningRepoDetailSearchVO searchVO = new MiningRepoDetailSearchVO();
        searchVO.setPage(pageRequest.getPageIndex().intValue());
        searchVO.setSize(pageRequest.getPageSize());
        searchVO.setRepoId(request.getRepoId());
        // todo: use config
        Map<String, String> params = new HashMap<>();
        if (request.getRepoType() == null){
            params.put("start_time", request.getStartTime().toString());
            params.put("end_time", request.getEndTime().toString());
        }
        String objTypesParam = getObjTypesSqlParam(request.getObjTypes());
        params.put("obj_type", objTypesParam);
        searchVO.setParams(params);
        MiningRepoDetailDto detail = miningRepoService.findMineRepoDetail(searchVO);

        QueryResultResponse<Map<String, Object>> resultResponse = new QueryResultResponse<>();

        List<QueryResultFieldInfo> resultFieldInfoList = new ArrayList<>();
        for (MiningRepoTableDesc.FieldDesc title : detail.getTitles()) {
            if (StringUtils.isNotBlank(title.getDesc())) {
                QueryResultFieldInfo resultField = new QueryResultFieldInfo(title.getName(), title.getDesc(), title.getType());
                resultFieldInfoList.add(resultField);
            }
        }
        resultResponse.setFieldNames(resultFieldInfoList);

        List<Map<String, Object>> data = detail.getData();
        resultResponse.setResponseList(data);

        PageInfo pageInfo = new PageInfo(data.size(), detail.getTotal(), pageRequest);

        return new PageResult<>(resultResponse, pageInfo);
    }

    /**
     * 导出挖掘库导入的数据
     */
    public int exportMiningRepoResult(List<String> contents, MiningQueryHistoryParam request, long index) {
        MiningRepoDetailSearchVO searchVO = new MiningRepoDetailSearchVO();
        searchVO.setPage((int) index);
        searchVO.setSize(ExportService.LOAD_SIZE);
        searchVO.setRepoId(request.getRepoId());
        // todo: use config
        Map<String, String> params = new HashMap<>();
        if (request.getRepoType() == null){
            params.put("start_time", request.getStartTime().toString());
            params.put("end_time", request.getEndTime().toString());
        }
        String objTypesParam = getObjTypesSqlParam(request.getObjTypes());
        params.put("obj_type", objTypesParam);
        searchVO.setParams(params);
        MiningRepoDetailDto detail = miningRepoService.findMineRepoDetail(searchVO);
        List<Map<String, Object>> data = detail.getData();
        int colCount = detail.getTitles().size();
        List<String[]> rowData = new ArrayList<>();
        for (Map<String, Object> datum : data) {
            String[] rowValue = new String[colCount];
            for (int j = 0; j < colCount; j++) {
                rowValue[j] = datum.getOrDefault(detail.getTitles().get(j).getName(), "").toString();
            }
            rowData.add(rowValue);
        }
        contents.add(ExportUtil.genContentByStringList(rowData));
        return data.size();
    }

    /**
     * 常用库导入的数据
     */
    public PageResult<QueryResultResponse> getRepoImportResult(QueryHistory queryHistory, PageRequest pageRequest) {
        PageResult<QueryResultResponse> result = new PageResult<>();
        MonitorRepoParam obj;
        try {
            obj = JsonUtils.parse(queryHistory.getRequestParameter(), MonitorRepoParam.class);
        } catch (IOException e) {
            throw new ArgumentException("invalid request parameter");
        }
        QueryResultResponse<MonitorObjectDto> resultResponse = new QueryResultResponse<>();

        List<QueryResultFieldInfo> resultFieldInfoList = getRepoResultFieldInfo(obj.getObjTypes());
        resultResponse.setFieldNames(resultFieldInfoList);

        Pair<Long, List<MonitorObjectDto>> queryResult = monRepoService.selectByRepoId(obj.getRepoId(),
                new com.jiayi.platform.library.minerepo.vo.PageRequest(pageRequest.getPageIndex(), pageRequest.getPageSize())); // TODO 统一两个模块的PageRequest

        List<MonitorObjectDto> data = queryResult.getRight();
        Long count = queryResult.getLeft();

        resultResponse.setResponseList(data);
        result.setPayload(resultResponse);

        PageInfo pageInfo = new PageInfo(data.size(), count, pageRequest);
        result.setPageInfo(pageInfo);
        return result;
    }

    /**
     * 导出常用库导入的数据
     */
    public int exportRepoImportResult(List<String> contents, MonitorRepoParam request, long index) {
        Pair<Long, List<MonitorObjectDto>> queryResult = monRepoService.selectByRepoId(request.getRepoId(),
                new com.jiayi.platform.library.minerepo.vo.PageRequest(index, ExportService.LOAD_SIZE));
        List<MonitorObjectDto> data = queryResult.getRight();
        int colCount = request.getObjTypes().size();
        List<String[]> rowData = new ArrayList<>();
        for (MonitorObjectDto datum : data) {
            String[] rowValue = new String[colCount];
            for (int j = 0; j < colCount; j++) {
                String objType = request.getObjTypes().get(j);
                ResultFieldEnum resultField = ResultFieldEnum.getResultFieldByDesc(objType, RequestType.REPO_IMPORT.typeName(), null);
                switch (resultField) {
                    case RI_OBJECT_NAME:
                        rowValue[j] = datum.getObjectName();
                        break;
                    case RI_OBJECT_TYPE:
                        rowValue[j] = datum.getObjectType();
                        break;
                    case RI_OBJECT_VALUE:
                        rowValue[j] = datum.getObjectValue();
                        break;
                    case USER_NAME:
                        rowValue[j] = datum.getName();
                        break;
                    case CERT_CODE:
                        rowValue[j] = datum.getCertCode();
                        break;
                    case PHONE:
                        rowValue[j] = datum.getPhone();
                        break;
                    case ADDRESS:
                        rowValue[j] = datum.getAddress();
                        break;
                }
            }
            rowData.add(rowValue);
        }
        contents.add(ExportUtil.genContentByStringList(rowData));
        return data.size();
    }

    /**
     * 文件导入的数据
     */
    public PageResult<QueryResultResponse> getFileImportResult(QueryHistory queryHistory, PageRequest pageRequest) {
        QueryResultResponse<Map<String, Object>> resultResponse = new QueryResultResponse<>();
        List<QueryResultFieldInfo> resultFieldInfoList = collisionResultFieldDao.getResultFieldInfoByTmplId(queryHistory.getId());
        resultResponse.setFieldNames(resultFieldInfoList);
        List<String> fields = resultFieldInfoList.stream().map(QueryResultFieldInfo::getId).collect(Collectors.toList());
        try {
            List<Map<String, Object>> data =
                    fileImportDao.selectFileImport(queryHistory.getId(), fields, pageRequest.getPageSize(), pageRequest.calOffset());
            Long count = fileImportDao.countFileImport(queryHistory.getId());

            List<String> macTypes = resultFieldInfoList.stream().filter(a -> FileImportTypeEnum.MAC.getName().equalsIgnoreCase(a.getType()))
                    .map(QueryResultFieldInfo::getId).collect(Collectors.toList());
            List<String> dateTypes = resultFieldInfoList.stream().filter(a -> FileImportTypeEnum.DATE.getName().equalsIgnoreCase(a.getType()))
                    .map(QueryResultFieldInfo::getId).collect(Collectors.toList());

            data.forEach(item -> {
                if (CollectionUtils.isNotEmpty(macTypes)) {
                    for (String type : macTypes) {
                        if (item.get(type) != null && StringUtils.isNotBlank(item.get(type).toString()))
                            item.put(type, ImpalaDataUtil.addMacCodeColons(item.get(type).toString(), FileImportTypeEnum.MAC.getName()));
                    }
                }
                if (CollectionUtils.isNotEmpty(dateTypes)) {
                    for (String type : dateTypes) {
                        if (item.get(type) != null && StringUtils.isNotBlank(item.get(type).toString())) {
                            item.put(type, MyDateUtil.getDateStr(Long.parseLong(item.get(type).toString())));
                        }
                    }
                }
            });
            resultResponse.setResponseList(data);

            PageInfo pageInfo = new PageInfo(data.size(), count, pageRequest);
            return new PageResult<>(resultResponse, pageInfo);
        } catch (Exception e) {
            throw new DBException("get file import result error", e);
        }
    }

    /**
     * 导出文件导入的数据
     */
    public int exportFileImportResult(List<String> contents, long offset, long queryId) {
        List<QueryResultFieldInfo> resultFieldInfoList = collisionResultFieldDao.getResultFieldInfoByTmplId(queryId);
        List<String> fields = resultFieldInfoList.stream().map(QueryResultFieldInfo::getId).collect(Collectors.toList());
        List<Map<String, Object>> data = fileImportDao.selectFileImport(queryId, fields, ExportService.LOAD_SIZE, offset);

        List<String> macTypes = resultFieldInfoList.stream().filter(a -> FileImportTypeEnum.MAC.getName().equalsIgnoreCase(a.getType()))
                .map(QueryResultFieldInfo::getId).collect(Collectors.toList());
        List<String> dateTypes = resultFieldInfoList.stream().filter(a -> FileImportTypeEnum.DATE.getName().equalsIgnoreCase(a.getType()))
                .map(QueryResultFieldInfo::getId).collect(Collectors.toList());
        data.forEach(item -> {
            if (CollectionUtils.isNotEmpty(macTypes)) {
                for (String type : macTypes) {
                    if (item.get(type) != null && StringUtils.isNotBlank(item.get(type).toString()))
                        item.put(type, ImpalaDataUtil.addMacCodeColons(item.get(type).toString(), FileImportTypeEnum.MAC.getName()));
                }
            }
            if (CollectionUtils.isNotEmpty(dateTypes)) {
                for (String type : dateTypes) {
                    if (item.get(type) != null && StringUtils.isNotBlank(item.get(type).toString())) {
                        item.put(type, MyDateUtil.getDateStr(Long.parseLong(item.get(type).toString())));
                    }
                }
            }
        });
        int colCount = fields.size();
        List<String[]> rowData = new ArrayList<>();
        for (Map<String, Object> datum : data) {
            String[] rowValue = new String[colCount];
            for (int j = 0; j < colCount; j++) {
                rowValue[j] = datum.getOrDefault(fields.get(j), "").toString();
            }
            rowData.add(rowValue);
        }
        contents.add(ExportUtil.genContentByStringList(rowData));
        return data.size();
    }

    /**
     * 除二次碰撞外的碰撞分析工具结果字段信息
     * @param requestType 碰撞分析类型
     * @param objectType 对象类型（MAC、IMSI等）
     * @return 结果字段信息列表
     */
    public List<QueryResultFieldInfo> getResultFieldInfo(String requestType, String objectType) {
        List<QueryResultFieldInfo> resultFieldInfo = new ArrayList<>();
        List<ResultFieldEnum> resultFields = ResultFieldEnum.getResultInfoList(requestType, objectType);
        for (ResultFieldEnum resultField : resultFields) {
            QueryResultFieldInfo fieldInfo = new QueryResultFieldInfo(resultField);
            resultFieldInfo.add(fieldInfo);
        }
        return resultFieldInfo;
    }

    /**
     * 二次碰撞的结果字段信息
     * @param queryId 查询记录id
     * @return 结果字段信息列表
     */
    public List<QueryResultFieldInfo> getAggregateResultFieldInfo(Long queryId) {
        List<CollisionResultField> resultFields = collisionResultFieldDao.findByRequestTmplId(queryId);
        return resultFields.stream().map(field -> {
            String fieldName = field.getFieldName();
            if (field.getFieldName().equals("result_value"))
                fieldName = "resultValue";
            return new QueryResultFieldInfo(fieldName, field.getFieldDesc(), field.getFieldType());
        }).collect(Collectors.toList());
    }

    /**
     * 常用库物品字段信息
     * @param objTypes 库导入的字段名
     * @return 结果字段信息列表
     */
    private List<QueryResultFieldInfo> getRepoResultFieldInfo(List<String> objTypes) {
        List<QueryResultFieldInfo> resultFieldInfo = new ArrayList<>();
        for (String fieldDesc : objTypes) {
            ResultFieldEnum value = ResultFieldEnum.getResultFieldByDesc(fieldDesc, RequestType.REPO_IMPORT.typeName(), null);
            if (value != null) {
                QueryResultFieldInfo fieldInfo = new QueryResultFieldInfo(value);
                resultFieldInfo.add(fieldInfo);
            }
        }
        return resultFieldInfo;
    }

    private String getObjTypesSqlParam(List<String> objTypes) {
        if (CollectionUtils.isEmpty(objTypes)) {
            return "";
        }
        return objTypes.stream().map(str -> "'" + str + "'").collect(Collectors.joining(","));
    }
}
