package com.jiayi.platform.judge.service;

import com.jiayi.platform.common.exception.ServiceException;
import com.jiayi.platform.judge.entity.mysql.QueryHistory;
import com.jiayi.platform.judge.enums.JudgeStatus;
import com.jiayi.platform.judge.enums.RequestType;
import com.jiayi.platform.judge.manage.RequestHistoryManager;
import com.jiayi.platform.judge.manage.SaveResultFieldManager;
import com.jiayi.platform.judge.request.JudgeRequest;
import com.jiayi.platform.judge.request.QueryResultRequest;
import com.jiayi.platform.judge.response.AggregateCollideField;
import com.jiayi.platform.judge.response.AggregateCollideFieldInfo;
import com.jiayi.platform.judge.util.JudgeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AggregateService {
    @Autowired
    private RequestHistoryManager requestHistoryManager;
    @Autowired
    private SaveResultFieldManager saveResultFieldManager;
    @Autowired
    private ExportService exportService;

    public List<AggregateCollideField> getAggregateCollideField(List<Long> uidList) {
        List<AggregateCollideField> collideFieldList = new ArrayList<>();
        for (Long uid : uidList) {
            List<AggregateCollideFieldInfo> resultFields = saveResultFieldManager.getResultFieldList(uid)
                    .stream().map(AggregateCollideFieldInfo::new).collect(Collectors.toList());
            AggregateCollideField collideField = new AggregateCollideField(uid, resultFields, resultFields.get(0).getRequestType());
            collideFieldList.add(collideField);
        }
        return collideFieldList;
    }

    public void exportAggregateRecordResult(QueryResultRequest request, HttpServletResponse response) {
        long uid = request.getUid();
        QueryHistory history = requestHistoryManager.getQueryHistoryByRequestId(uid);
        if (history == null || history.getStatus() != JudgeStatus.SUCCEED.code()) {
            throw new ServiceException("results cannot be exported now");
        }
        RequestType requestType = RequestType.getRequestType(request.getRequestType());
        JudgeRequest judgeRequest = JudgeUtil.getJudgeRequest(history.getRequestParameter(), requestType);
        exportService.exportJudgeResult(judgeRequest, response, requestType);
    }
}
