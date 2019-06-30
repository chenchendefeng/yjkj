package com.jiayi.platform.judge.manage;

import com.jiayi.platform.common.exception.DBException;
import com.jiayi.platform.common.util.JsonUtils;
import com.jiayi.platform.judge.dao.mysql.QueryHistoryDao;
import com.jiayi.platform.judge.dao.mysql.RequestHistoryDao;
import com.jiayi.platform.judge.entity.mysql.QueryHistory;
import com.jiayi.platform.judge.entity.mysql.RequestHistory;
import com.jiayi.platform.judge.enums.JudgeStatus;
import com.jiayi.platform.judge.enums.RequestType;
import com.jiayi.platform.judge.request.JudgeRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 用户查询记录，以及查询结果的管理，主要操作request_history和query_history表
 *
 * @author : weichengke
 * @date : 2019-04-19 16:00
 */
@Component
@Slf4j
public class RequestHistoryManager {

    @Autowired
    private RequestHistoryDao requestHistoryDao;
    @Autowired
    private QueryHistoryDao queryHistoryDao;

    /**
     * 保存用户的查询记录
     *
     * @param request    用户查询的请求参数
     * @param queryId    当前用户查询对应的查询缓存Id
     * @param resultName 用户自定义的查询结果名称
     */
    public <T extends JudgeRequest> long saveRequestHistory(T request, long queryId, String resultName) {
        RequestHistory requestHistory = new RequestHistory();
        requestHistory.setCaseId(request.getCaseId());
        requestHistory.setUserId(request.getUserId());
        requestHistory.setQueryId(queryId);
        Date curDate = new Date();
        requestHistory.setRequestDate(curDate);
        requestHistory.setCreateDate(curDate);
        requestHistory.setUpdateDate(curDate);
        requestHistory.setResultName(resultName);
        requestHistory.setValid(true);
        requestHistory.setTwoCollision(resultName != null);
        try {
            requestHistoryDao.save(requestHistory);
            return requestHistory.getId();
        }
        catch (Exception e) {
            log.error("request history mysql save error", e);
            throw new DBException("query history save error", e);
        }
    }

    public <T extends JudgeRequest> long saveRequestHistory(T request, long queryId) {
        return saveRequestHistory(request, queryId, null);
    }

    /**
     * 更新或保存用户的请求历史，没有则保存请求历史，有则只更新请求时间。
     */
    public <T extends JudgeRequest> void upsertRequestHistoryTime(T request, QueryHistory queryHistory, RequestHistory requestHistory, String resultSetName) {
        if (requestHistory != null) {
            Date curDate = new Date();
            requestHistory.setRequestDate(curDate);
            requestHistory.setUpdateDate(curDate);
            requestHistory.setQueryId(queryHistory.getId()); // queryHistory可能被重新创建
            requestHistoryDao.save(requestHistory);
        } else {
            saveRequestHistory(request, queryHistory.getId(), resultSetName);
        }
    }

    public <T extends JudgeRequest> void updateQueryHistory(T request, long queryId, Long resultCount, JudgeStatus status) {
        QueryHistory queryHistory = queryHistoryDao.findById(queryId).orElseThrow(() -> new DBException("find queryHistory by id error"));
        String param = JsonUtils.toJson(request);
        queryHistory.setRequestParameter(param);
        queryHistory.setMd5(DigestUtils.md5Hex(param));
        queryHistory.setStatus(status.code());
        queryHistory.setUpdateDate(new Date());
        queryHistory.setResultCount(resultCount);
        try {
            queryHistoryDao.save(queryHistory);
        } catch (Exception e) {
            log.error("query history mysql update error", e);
            throw new DBException("query history update error", e);
        }
    }

    public void updateQueryStatus (long queryId, JudgeStatus status) {
        try {
            queryHistoryDao.updateQueryStatus(queryId, status.code(), new Date());
        } catch (Exception e) {
            log.error("query history mysql update error", e);
            throw new DBException("query history update error", e);
        }
    }

    public void setValid(long requestId, Boolean valid) {
        try {
//            RequestHistory requestHistory = requestHistoryDao.findById(requestId).get();
//            queryHistoryDao.deleteById(requestHistory.getQueryId());
            requestHistoryDao.setValid(requestId, valid);
        } catch (Exception e) {
            log.error("request history mysql update error", e);
            throw new DBException("request history update error", e);
        }
    }

    public <T extends JudgeRequest> QueryHistory findLatestQueryHistory(T request, RequestType requestType) {
        String md5 = DigestUtils.md5Hex(JsonUtils.toJson(request));
        Sort sort = Sort.by(Sort.Direction.DESC, "updateDate");
        Page<QueryHistory> queryHistoies = queryHistoryDao.findByMd5AndRequestType(md5, requestType.typeName(), PageRequest.of(0, 1, sort));
        if (queryHistoies.getTotalElements() > 0) {
            return queryHistoies.getContent().get(0);
        } else {
            return null;
        }
    }

    public QueryHistory getQueryHistoryByRequestId (long requestId) {
        try {
            return queryHistoryDao.findQueryHistoryByRequestId(requestId);
        } catch (Exception e) {
            log.error("request/query history mysql search error", e);
            throw new DBException("request/query history mysql search error", e);
        }
    }

    public <T extends JudgeRequest> QueryHistory saveQueryHistory(T request, String requestType, JudgeStatus status) {
        return saveQueryHistory(request, requestType, status, 0L);
    }

    public <T extends JudgeRequest> QueryHistory saveQueryHistory(T request, String requestType, JudgeStatus status, Long resultCount) {
        QueryHistory queryHistory = new QueryHistory();
        String param = JsonUtils.toJson(request);
        queryHistory.setRequestParameter(param);
        queryHistory.setMd5(DigestUtils.md5Hex(param));
        queryHistory.setRequestType(requestType);
        queryHistory.setStatus(status.code());
        Date curDate = new Date();
        queryHistory.setCreateDate(curDate);
        queryHistory.setUpdateDate(curDate);
        queryHistory.setResultCount(resultCount);
        try {
            queryHistoryDao.save(queryHistory);
            return queryHistory;
        } catch (Exception e) {
            log.error("query history mysql insert error, request: {}", request);
            throw new DBException("query history mysql insert error", e);
        }
    }

    public void updateRequestRemark (long requestId, String remark) {
        Date curDate = new Date();
        try {
            requestHistoryDao.updateRequestRemark(requestId, remark, curDate);
        } catch (Exception e) {
            log.error("request history mysql update error", e);
            throw new DBException("request history update error", e);
        }
    }

    public void updateRequestTwoCollision (long requestId, Boolean twoCollision, String resultName) {
        try {
            requestHistoryDao.updateTwoCollision(twoCollision, resultName, requestId, new Date());
        } catch (Exception e) {
            log.error("request history mysql update error", e);
            throw new DBException("request history update error", e);
        }
    }

    public boolean existsResultName(String caseId, String resultName) {
        try {
            return requestHistoryDao.countByCaseIdAndResultNameAndValid(caseId, resultName, true) > 0;
        } catch (Exception e) {
            log.error("request history mysql query error", e);
            throw new DBException("request history query error", e);
        }
    }

    public List<Pair<Long, String>> getOutDatedQueryList (Date largeCacheCleanDate, Long cacheSize, Date cleanDate) {
        try {
            return requestHistoryDao.getOutDatedQueryList(largeCacheCleanDate, cacheSize, cleanDate,
                    JudgeStatus.DELETED.code());
        } catch (Exception e) {
            log.error("request history info mysql search error", e);
            throw new DBException("request history info mysql search error", e);
        }
    }
}
