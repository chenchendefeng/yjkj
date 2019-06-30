package com.jiayi.platform.judge.executor;

import com.jiayi.platform.basic.entity.ObjectOrganizationInfo;
import com.jiayi.platform.basic.manager.ObjectOrganizationManager;
import com.jiayi.platform.basic.service.DeviceService;
import com.jiayi.platform.common.enums.CollectType;
import com.jiayi.platform.common.exception.ArgumentException;
import com.jiayi.platform.common.web.util.ExportUtil;
import com.jiayi.platform.judge.common.bean.PageInfo;
import com.jiayi.platform.judge.common.bean.PageRequest;
import com.jiayi.platform.judge.common.bean.PageResult;
import com.jiayi.platform.judge.dao.impala.MultiFeatureAnalysisDao;
import com.jiayi.platform.judge.dto.MultiFeatureAnalysisDto;
import com.jiayi.platform.judge.manage.ImpalaTableManager;
import com.jiayi.platform.judge.query.MultiFeatureAnalysisQuery;
import com.jiayi.platform.judge.request.JudgeRequest;
import com.jiayi.platform.judge.request.MultiFeatureAnalysisRequest;
import com.jiayi.platform.judge.response.MultiFeatureAnalysisResponse;
import com.jiayi.platform.judge.service.ExportService;
import com.jiayi.platform.judge.util.ImpalaDataUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class MultiFeatureAnalysisExecutor implements JudgeExecutor {
    @Autowired
    private MultiFeatureAnalysisDao multiFeatureAnalysisDao;

    @Autowired
    private ImpalaTableManager impalaTableManager;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private ObjectOrganizationManager objectOrganizationManager;

    @Override
    public <T extends JudgeRequest> List<?> query(T request, PageRequest pageRequest) {
        MultiFeatureAnalysisQuery query = new MultiFeatureAnalysisQuery();
        buildMultiFeatureQuery(query, (MultiFeatureAnalysisRequest) request);
        query.setLimit(pageRequest.getPageSize());
        query.setOffset(pageRequest.calOffset());
        return multiFeatureAnalysisDao.selectMultiFeatureAnalysis(query);
    }

    @Override
    public <T extends JudgeRequest> long count(T request) {
        MultiFeatureAnalysisQuery query = new MultiFeatureAnalysisQuery();
        buildMultiFeatureQuery(query, (MultiFeatureAnalysisRequest) request);
        return multiFeatureAnalysisDao.countMultiFeatureAnalysis(query);
    }

    @Override
    public List<?> queryCache(Long queryHistoryId, PageRequest pageRequest) {
        return multiFeatureAnalysisDao.selectMultiFeatureResult(queryHistoryId, pageRequest.getPageSize(), pageRequest.calOffset());
    }

    @Override
    public long countCache(Long queryHistoryId) {
        return multiFeatureAnalysisDao.countMultiFeatureResult(queryHistoryId);
    }

    @Override
    public <T extends JudgeRequest> long cache(T request, long queryHistoryId) {
        MultiFeatureAnalysisQuery query = new MultiFeatureAnalysisQuery();
        buildMultiFeatureQuery(query, (MultiFeatureAnalysisRequest) request);
        query.setUid(queryHistoryId);
        multiFeatureAnalysisDao.insertMultiFeatureResult(query);
        return countCache(queryHistoryId);
    }

    @Override
    public <T extends JudgeRequest> PageResult convert2Response(List<?> dots, long count, T request, PageRequest pageRequest) {
        List<MultiFeatureAnalysisResponse> responseList = new ArrayList<>();
        dots.forEach(dto -> {
            MultiFeatureAnalysisDto item = (MultiFeatureAnalysisDto) dto;
            MultiFeatureAnalysisResponse response = new MultiFeatureAnalysisResponse();
            response.setObjectTypeName(CollectType.getByCode(item.getObjectType()).name().toLowerCase());
            response.setObjectValue(ImpalaDataUtil.addMacCodeColons(item.getObjectValue(),
                    CollectType.getByCode(item.getObjectType()).name().toLowerCase()));
            response.setMatchDays(item.getMatchDays());
            response.setMatchCount(item.getMatchCount() / 100.0);
            response.setMatchTracks(item.getMatchTracks());
            ObjectOrganizationInfo organization = objectOrganizationManager
                    .getObjectOrganization(CollectType.getByCode(item.getObjectType()).name().toLowerCase(), item.getObjectValue());
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
        List<MultiFeatureAnalysisDto> data = (multiFeatureAnalysisDao.selectMultiFeatureResult(queryId, ExportService.LOAD_SIZE, offset));

        int colCount = 6;
        List<String[]> rowData = new ArrayList<>();
        for (MultiFeatureAnalysisDto datum : data) {
            String[] rowValue = new String[colCount];
            int j = 0;
            rowValue[j++] = CollectType.getByCode(datum.getObjectType()).desc();
            rowValue[j++] = ImpalaDataUtil.addMacCodeColons(datum.getObjectValue(),
                    CollectType.getByCode(datum.getObjectType()).name().toLowerCase()) + "\t";
            rowValue[j++] = datum.getMatchDays().toString();
            rowValue[j++] = String.valueOf(datum.getMatchCount() / 100.0);
            rowValue[j++] = datum.getMatchTracks().toString();
            ObjectOrganizationInfo organization = objectOrganizationManager
                    .getObjectOrganization(request.getObjectTypeName(), datum.getObjectValue());
            rowValue[j] = (organization != null) ? organization.getOrganizationName() : "";
            rowData.add(rowValue);
        }
        contents.add(ExportUtil.genContentByStringList(rowData));
        return data.size();
    }

    private void buildMultiFeatureQuery(MultiFeatureAnalysisQuery query, MultiFeatureAnalysisRequest request) {
        try {
            query.setObjectType(CollectType.valueOf(request.getObjectTypeName().toUpperCase()).code());
            query.setObjectValue(request.getObjectValue().toUpperCase().replaceAll("[-:：\\s]", ""));
            query.setBeginDate(request.getBeginDate());
            query.setEndDate(request.getEndDate());
                query.setMatchCoefficient((int) (request.getMatchCount() * 100));
            query.setMatchDays(request.getMatchDays());
            query.setResultTypeList(request.getObjectTypeNameList().stream().map(type -> CollectType.valueOf(type.toUpperCase()).code())
                    .collect(Collectors.toList()));
        } catch (Exception e) {
            log.error("multi-feature analysis request convert error, request is {}", request, e);
            throw new ArgumentException("multi-feature analysis request convert error", e);
        }
    }
}
