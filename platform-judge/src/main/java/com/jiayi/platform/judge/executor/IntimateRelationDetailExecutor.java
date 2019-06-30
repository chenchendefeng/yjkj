package com.jiayi.platform.judge.executor;

import com.jiayi.platform.basic.entity.Device;
import com.jiayi.platform.basic.service.DeviceService;
import com.jiayi.platform.common.enums.CollectType;
import com.jiayi.platform.common.exception.ArgumentException;
import com.jiayi.platform.common.web.util.ExportUtil;
import com.jiayi.platform.judge.common.bean.PageRequest;
import com.jiayi.platform.judge.dao.impala.IntimateRelationAnalysisDao;
import com.jiayi.platform.judge.dto.MultiFeatureDetailDto;
import com.jiayi.platform.judge.manage.ImpalaTableManager;
import com.jiayi.platform.judge.query.MultiFeatureDetailQuery;
import com.jiayi.platform.judge.request.IntimateRelationDetailRequest;
import com.jiayi.platform.judge.request.JudgeDetailRequest;
import com.jiayi.platform.judge.response.IntimateRelationDetailResponse;
import com.jiayi.platform.judge.service.ExportService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class IntimateRelationDetailExecutor implements JudgeDetailExecutor {
    @Autowired
    private IntimateRelationAnalysisDao intimateRelationAnalysisDao;

    @Autowired
    private ImpalaTableManager impalaTableManager;
    @Autowired
    private DeviceService deviceService;

    @Override
    public <T> List<?> query(T request, PageRequest pageRequest) {
        MultiFeatureDetailQuery query = new MultiFeatureDetailQuery();
        buildIntimateRelationDetailQuery(query, (IntimateRelationDetailRequest) request);
        query.setLimit(pageRequest.getPageSize());
        query.setOffset(pageRequest.calOffset());
        return intimateRelationAnalysisDao.selectIntimateRelationDetail(query);
    }

    @Override
    public <T> long count(T request) {
        MultiFeatureDetailQuery query = new MultiFeatureDetailQuery();
        buildIntimateRelationDetailQuery(query, (IntimateRelationDetailRequest) request);
        return intimateRelationAnalysisDao.countIntimateRelationDetail(query);
    }

    @Override
    public List<?> convert2Response(List<?> dots, String objectType) {
        List<IntimateRelationDetailResponse> responseList = new ArrayList<>();
        Map<Long, Device> deviceMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(dots)) {
            Set<Long> deviceIds = new HashSet<>();
            deviceIds.addAll(dots.stream().map(dto -> ((MultiFeatureDetailDto) dto).getBeginDeviceId()).filter(Objects::nonNull)
                    .collect(Collectors.toSet()));
            deviceIds.addAll(dots.stream().map(dto -> ((MultiFeatureDetailDto) dto).getEndDeviceId()).filter(Objects::nonNull)
                    .collect(Collectors.toSet()));
            deviceMap.putAll(deviceService.findByIds(deviceIds));
        }
        dots.forEach(dto -> {
            MultiFeatureDetailDto item = (MultiFeatureDetailDto) dto;
            IntimateRelationDetailResponse response = new IntimateRelationDetailResponse();
            response.setRecordDate(item.getRecordDate());
            response.setBeginTime(item.getBeginTime());
            response.setEndTime(item.getEndTime());
            if (item.getBeginDeviceId() != null && deviceMap.get(item.getBeginDeviceId()) != null)
                response.setBeginAddress(deviceMap.get(item.getBeginDeviceId()).getAddress());
            if (item.getEndDeviceId() != null && deviceMap.get(item.getEndDeviceId()) != null)
                response.setEndAddress(deviceMap.get(item.getEndDeviceId()).getAddress());
            response.setMatchNum(item.getMatchNum());

            responseList.add(response);
        });
        return responseList;
    }

    @Override
    public <T extends JudgeDetailRequest> int exportResult(List<String> contents, T request, long offset) {
        MultiFeatureDetailQuery query = new MultiFeatureDetailQuery();
        buildIntimateRelationDetailQuery(query, (IntimateRelationDetailRequest) request);
        query.setLimit(ExportService.LOAD_SIZE);
        query.setOffset(offset);
        List<MultiFeatureDetailDto> data = intimateRelationAnalysisDao.selectIntimateRelationDetail(query);

        Map<Long, Device> deviceMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(data)) {
            Set<Long> deviceIds = new HashSet<>();
            deviceIds.addAll(data.stream().map(MultiFeatureDetailDto::getBeginDeviceId).filter(Objects::nonNull).collect(Collectors.toSet()));
            deviceIds.addAll(data.stream().map(MultiFeatureDetailDto::getEndDeviceId).filter(Objects::nonNull).collect(Collectors.toSet()));
            deviceMap.putAll(deviceService.findByIds(deviceIds));
        }
        int colCount = 6;
        List<String[]> rowData = new ArrayList<>();
        for (MultiFeatureDetailDto datum : data) {
            String[] rowValue = new String[colCount];
            int j = 0;
            rowValue[j++] = ExportService.SDF.format(datum.getRecordDate());
            rowValue[j++] = datum.getMatchNum().toString();
            rowValue[j++] = ExportService.SDF.format(datum.getBeginTime());
            rowValue[j++] = (datum.getBeginDeviceId() != null && deviceMap.get(datum.getBeginDeviceId()) != null)
                    ? deviceMap.get(datum.getBeginDeviceId()).getAddress()
                    : "";
            rowValue[j++] = ExportService.SDF.format(datum.getEndTime());
            rowValue[j] = (datum.getEndDeviceId() != null && deviceMap.get(datum.getEndDeviceId()) != null)
                    ? deviceMap.get(datum.getEndDeviceId()).getAddress()
                    : "";
            rowData.add(rowValue);
        }
        contents.add(ExportUtil.genContentByStringList(rowData));
        return data.size();
    }

    private void buildIntimateRelationDetailQuery(MultiFeatureDetailQuery query, IntimateRelationDetailRequest request) {
        try {
            query.setObjectType(CollectType.valueOf(request.getObjectTypeName().toUpperCase()).code());
            query.setObjectValue(request.getObjectValue().toUpperCase().replaceAll("[-:：\\s]", ""));
            query.setBeginDate(request.getBeginDate());
            query.setEndDate(request.getEndDate());
            query.setMatchObjectType(CollectType.valueOf(request.getMatchObjectTypeName().toUpperCase()).code());
            query.setMatchObjectValue(request.getMatchObjectValue().toUpperCase().replaceAll("[-:：\\s]", ""));
        } catch (Exception e) {
            log.error("intimate relation detail request convert error, request is {}", request, e);
            throw new ArgumentException("intimate relation detail request convert error", e);
        }
    }
}
