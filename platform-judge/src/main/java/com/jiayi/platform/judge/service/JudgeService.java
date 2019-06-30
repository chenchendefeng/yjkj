package com.jiayi.platform.judge.service;

import com.jiayi.platform.common.exception.ArgumentException;
import com.jiayi.platform.common.exception.DBException;
import com.jiayi.platform.common.exception.ValidException;
import com.jiayi.platform.common.util.ThreadPoolUtil;
import com.jiayi.platform.judge.common.bean.PageInfo;
import com.jiayi.platform.judge.common.bean.PageRequest;
import com.jiayi.platform.judge.common.bean.PageResult;
import com.jiayi.platform.judge.dao.mysql.QueryHistoryDao;
import com.jiayi.platform.judge.dao.mysql.RequestHistoryDao;
import com.jiayi.platform.judge.entity.mysql.QueryHistory;
import com.jiayi.platform.judge.entity.mysql.RequestHistory;
import com.jiayi.platform.judge.enums.JudgeStatus;
import com.jiayi.platform.judge.enums.RequestType;
import com.jiayi.platform.judge.executor.*;
import com.jiayi.platform.judge.manage.HistoryQueryResultManager;
import com.jiayi.platform.judge.manage.RequestHistoryManager;
import com.jiayi.platform.judge.request.*;
import com.jiayi.platform.judge.response.QueryResultFieldInfo;
import com.jiayi.platform.judge.response.QueryResultResponse;
import com.jiayi.platform.judge.util.JudgeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;

/**
 * 研判流程管控，完成研判、用户操作记录、历史记录的流程管理。
 * @author : weichengke
 * @date : 2019-04-18 17:12
 */
@Service
@Slf4j
public class JudgeService {

    @Autowired
    private RequestHistoryManager requestHistoryManager;
    @Autowired
    private HistoryQueryResultManager historyQueryResultManager;
    @Autowired
    private RequestHistoryDao requestHistoryDao;
    @Autowired
    private QueryHistoryDao queryHistoryDao;

    public PageResult judge(JudgeRequest request, PageRequest pageRequest, String requestType) {
        return judge(request, pageRequest, RequestType.valueOf(requestType.toUpperCase()));
    }

    // 为了方便处理全量和分页数据，分页信息pageRequest单独提出来
    public PageResult judge(JudgeRequest request, PageRequest pageRequest, RequestType requestType) {
        long start = System.currentTimeMillis();
        if (request.getCaseId() == null)
            request.setCaseId("0");
        String resultSetName = null;
        if (requestType.equals(RequestType.AGGREGATE_COLLISION)) {
            resultSetName = ((AggregateRequest) request).getResultSetName();
            if (requestHistoryManager.existsResultName(request.getCaseId(), resultSetName))
                throw new ValidException("结果集名称已存在，请更换名称");
        }

        QueryHistory queryHistory = requestHistoryManager.findLatestQueryHistory(request, requestType);
        RequestHistory requestHistory = null;
        if (queryHistory == null) {
            queryHistory = new QueryHistory();
            queryHistory.setStatus(JudgeStatus.UNKNOWN.code());
        } else {
            List<RequestHistory> historyList = requestHistoryDao.
                    findByCaseIdAndUserIdAndQueryIdAndValid(request.getCaseId(), request.getUserId(), queryHistory.getId(), true);
            if (historyList != null && !historyList.isEmpty())
                requestHistory = historyList.get(0);
        }

        JudgeExecutor judgeExecutor = JudgeUtil.getExecutor(requestType);

        // 查询并管理查询缓存的状态
        List<?> queryResult;
        long count;
        Future<List<?>> dataFuture;
        Future<Long> countFuture;
        try {
            switch (JudgeStatus.getStatusByCode(queryHistory.getStatus())) {
                case SUCCEED: // 缓存存在，直接查询缓存
                    // 规律分析 及 轨迹比对全部结果 暂无缓存，每次都重新查询
                    if (requestType.equals(RequestType.LOCATION_ANALYSIS) || requestType.equals(RequestType.MOVEMENT_ANALYSIS)
                            || requestType.equals(RequestType.TRACK_COMPARE) && ((TrackCompareRequest) request).getMatch() == 0) {
                        requestHistoryManager.updateQueryStatus(queryHistory.getId(), JudgeStatus.CALCULATING);
                        dataFuture = ThreadPoolUtil.getInstance().submit(() -> judgeExecutor.query(request, pageRequest));
                        countFuture = ThreadPoolUtil.getInstance().submit(() -> judgeExecutor.count(request));
                        queryResult = dataFuture.get();
                        count = countFuture.get();
                        queryHistoryDao.updateResultCountAndStatus(queryHistory.getId(), count, JudgeStatus.SUCCEED.code(), new Date());
                    } else {
                        QueryHistory queryHistoryFinal = queryHistory;
                        dataFuture = ThreadPoolUtil.getInstance().submit(() -> judgeExecutor.queryCache(queryHistoryFinal.getId(), pageRequest));
                        countFuture = ThreadPoolUtil.getInstance().submit(() -> judgeExecutor.countCache(queryHistoryFinal.getId()));
                        queryResult = dataFuture.get();
                        count = countFuture.get();
                    }
                    break;
                case WAITING:
                case CALCULATING: // 准备或正在保存缓存结果，直接查询当前页，不新生成QueryHistory
                    dataFuture = ThreadPoolUtil.getInstance().submit(() -> judgeExecutor.query(request, pageRequest));
                    countFuture = ThreadPoolUtil.getInstance().submit(() -> judgeExecutor.count(request));
                    queryResult = dataFuture.get();
                    count = countFuture.get();
                    break;
                default:
                    // 其他情况，要么之前出错了，要么保存的记录已经没有了，需要重新生成新的QueryHistory
                    // 这里重新查询后，不直接更新最新的已有QueryHistory，避免不同查询后结果集相互干扰。比如以前错误的数据还没有清除完
                    queryHistory = requestHistoryManager.saveQueryHistory(request, requestType.typeName(), JudgeStatus.CALCULATING);
                    long queryStart = System.currentTimeMillis();
                    dataFuture = ThreadPoolUtil.getInstance().submit(() -> judgeExecutor.query(request, pageRequest));
                    countFuture = ThreadPoolUtil.getInstance().submit(() -> judgeExecutor.count(request));
                    queryResult = dataFuture.get();
                    count = countFuture.get();
                    log.info(requestType + " query time used: " + (System.currentTimeMillis() - queryStart) / 100 / 10.0 + "s");
                    QueryHistory queryHistoryF = queryHistory;
                    ThreadPoolUtil.getInstance().execute(() -> {
                        try {
                            long totalCount = judgeExecutor.cache(request, queryHistoryF.getId());
                            queryHistoryDao.updateResultCountAndStatus(queryHistoryF.getId(), totalCount, JudgeStatus.SUCCEED.code(), new Date());
                        } catch (Exception e) {
                            requestHistoryManager.updateQueryStatus(queryHistoryF.getId(), JudgeStatus.FAILED);
                        }
                    });
                    break;
            }
        } catch (Exception e) {
            requestHistoryManager.updateQueryStatus(queryHistory.getId(), JudgeStatus.FAILED);
//            requestHistoryManager.upsertRequestHistoryTime(request, queryHistory.getId());
            requestHistoryManager.upsertRequestHistoryTime(request, queryHistory, requestHistory, resultSetName);
            throw new DBException(requestType + " query error", e);
        }

        // 管理用户操作历史记录
//        requestHistoryManager.upsertRequestHistoryTime(request, queryHistory.getId());
        requestHistoryManager.upsertRequestHistoryTime(request, queryHistory, requestHistory, resultSetName);

        // 封装前端需要的数据格式
        PageResult result =  judgeExecutor.convert2Response(queryResult, count, request, pageRequest);
        log.info(requestType + " time used: " + (System.currentTimeMillis() - start) / 100 / 10.0 + "s");
        return result;
    }

    /**
     * 查询历史记录结果
     */
    public PageResult<QueryResultResponse> judgeHistory(QueryResultRequest request, PageRequest pageRequest) {
        RequestType requestType = RequestType.getRequestType(request.getRequestType());
        long uid = request.getUid();
        QueryHistory queryHistory = requestHistoryManager.getQueryHistoryByRequestId(uid);
        if (queryHistory == null)
            throw new DBException("invalid request id");

        switch (requestType) {
            // 研判工具类（包括二次碰撞）的结果查询
            case LINE_COLLISION:
            case AREA_COLLISION:
            case APPEAR_COLLISION:
            case DISAPPEAR_COLLISION:
            case FOLLOW_COLLISION:
            case MULTI_TRACK_COLLISION:
            case DEVICE_ANALYSIS:
            case MULTI_FEATURE_ANALYSIS:
            case INTIMATE_RELATION_ANALYSIS:
            case AGGREGATE_COLLISION:
                JudgeRequest judgeRequest = JudgeUtil.getJudgeRequest(queryHistory.getRequestParameter(), requestType);
                RequestHistory requestHistory;
                Optional<RequestHistory> requestHistoryOpt = requestHistoryDao.findById(uid);
                if (requestHistoryOpt.isPresent())
                    requestHistory = requestHistoryOpt.get();
                else
                    throw new ArgumentException("invalid request id");

                JudgeExecutor judgeExecutor = JudgeUtil.getExecutor(requestType);

                List<?> queryResult;
                long count;
                Future<List<?>> dataFuture;
                Future<Long> countFuture;
                try {
                    if (JudgeStatus.getStatusByCode(queryHistory.getStatus()) == JudgeStatus.SUCCEED) { // 缓存存在，直接查询缓存
                        QueryHistory queryHistoryFinal = queryHistory;
                        dataFuture = ThreadPoolUtil.getInstance().submit(() -> judgeExecutor.queryCache(queryHistoryFinal.getId(), pageRequest));
                        countFuture = ThreadPoolUtil.getInstance().submit(() -> judgeExecutor.countCache(queryHistoryFinal.getId()));
                        queryResult = dataFuture.get();
                        count = countFuture.get();
                    } else {
                        queryHistory = requestHistoryManager.saveQueryHistory(judgeRequest, requestType.typeName(), JudgeStatus.CALCULATING);
                        dataFuture = ThreadPoolUtil.getInstance().submit(() -> judgeExecutor.query(judgeRequest, pageRequest));
                        countFuture = ThreadPoolUtil.getInstance().submit(() -> judgeExecutor.count(judgeRequest));
                        queryResult = dataFuture.get();
                        count = countFuture.get();
                        QueryHistory queryHistoryF = queryHistory;
                        ThreadPoolUtil.getInstance().execute(() -> {
                            try {
                                long totalCount = judgeExecutor.cache(judgeRequest, queryHistoryF.getId());
                                queryHistoryDao.updateResultCountAndStatus(queryHistoryF.getId(), totalCount, JudgeStatus.SUCCEED.code(), new Date());
                            } catch (Exception e) {
                                requestHistoryManager.updateQueryStatus(queryHistoryF.getId(), JudgeStatus.FAILED);
                            }
                        });
                    }
                } catch (Exception e) {
                    requestHistoryManager.updateQueryStatus(queryHistory.getId(), JudgeStatus.FAILED);
                    requestHistoryManager.upsertRequestHistoryTime(judgeRequest, queryHistory, requestHistory, null);
                    throw new DBException(requestType + " query error", e);
                }

                requestHistoryManager.upsertRequestHistoryTime(judgeRequest, queryHistory, requestHistory, null);

                QueryResultResponse resultResponse = new QueryResultResponse();
                String objectType = judgeRequest.getObjectTypeName();
                if (queryHistory.getRequestType().equals(RequestType.MULTI_TRACK_COLLISION.typeName()))
                    objectType = ((MultiTrackCollisionRequest) judgeRequest).getFollowObjectTypeName(); // 多轨碰撞objectType字段名不同
                List<QueryResultFieldInfo> resultFieldInfo;
                PageResult result;
                if (requestType.equals(RequestType.AGGREGATE_COLLISION)) {
                    resultFieldInfo = historyQueryResultManager.getAggregateResultFieldInfo(queryHistory.getId());
                    result = ((AggregateExecutor) judgeExecutor).convert2AggregateResponse(queryResult, count, judgeRequest, pageRequest, resultFieldInfo);
                } else {
                    resultFieldInfo = historyQueryResultManager.getResultFieldInfo(queryHistory.getRequestType(), objectType);
                    result = judgeExecutor.convert2Response(queryResult, count, judgeRequest, pageRequest);
                }
                resultResponse.setFieldNames(resultFieldInfo);
                List<?> responseList = (List<?>) result.getPayload();
                resultResponse.setResponseList(responseList);

                PageInfo pageInfo = new PageInfo(queryResult.size(), count, pageRequest);

                return new PageResult<>(resultResponse, pageInfo);
            case MINING_REPO: // 挖掘库导入数据查看
                return historyQueryResultManager.getMiningRepoResult(queryHistory, pageRequest);
            case FILE_IMPORT: // 文件导入数据查看
                return historyQueryResultManager.getFileImportResult(queryHistory, pageRequest);
            case REPO_IMPORT: // 常用库导入数据查看
                return historyQueryResultManager.getRepoImportResult(queryHistory, pageRequest);
            default:
                throw new ArgumentException("invalid request type for history result");
        }
    }

}
