package com.jiayi.platform.judge.manage;

import com.jiayi.platform.common.exception.ArgumentException;
import com.jiayi.platform.common.exception.DBException;
import com.jiayi.platform.judge.dao.impala.MybatisUtil;
import com.jiayi.platform.judge.dao.mysql.QueryHistoryDao;
import com.jiayi.platform.judge.entity.mysql.QueryHistory;
import com.jiayi.platform.judge.enums.JudgeStatus;
import com.jiayi.platform.judge.enums.RequestType;
import com.jiayi.platform.judge.executor.JudgeExecutor;
import com.jiayi.platform.judge.request.JudgeRequest;
import com.jiayi.platform.judge.util.JudgeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Async
@Slf4j
public class ScheduleManager {
    @Autowired
    private RequestHistoryManager requestHistoryManager;
    @Autowired
    private QueryHistoryDao queryHistoryDao;
    @Autowired
    private MybatisUtil mybatisUtil;

    @Value("${scheduleClean.retentionDays:30}")
    private int rententionDays;
    @Value("${scheduleClean.largeCacheRetentionDays:7}")
    private int largeCacheRetentionDays;
    @Value("${scheduleClean.largeCacheSize:2000000}")
    private long largeCacheSize;

    /**
     * 定时清理查询历史结果缓存
     */
//    @Scheduled(cron = "${scheduleClean.scheduleTime:0 0 1 * * ?}") // TODO 定时清理parquet文件处理
    public void scheduledClean() {
        log.info("Start clean collision cache:");

        Date cleanDate = new Date(System.currentTimeMillis() - rententionDays * 24 * 60 * 60 * 1000L);
        Date largeCacheCleanDate = new Date(System.currentTimeMillis() - largeCacheRetentionDays * 24 * 60 * 60 * 1000L);
        List<Pair<Long, String>> outDatedQueryList = requestHistoryManager.getOutDatedQueryList(largeCacheCleanDate, largeCacheSize, cleanDate);

        Map<String, List<Long>> outDatedResults = new HashMap<>();
        for (Pair<Long, String> outDatedQuery : outDatedQueryList) {
            String resultTableName = getResultTableName(outDatedQuery.getRight());
            if (!resultTableName.equals("")) {
                if (!outDatedResults.containsKey(resultTableName)) {
                    outDatedResults.put(resultTableName, new ArrayList<>());
                }
                outDatedResults.get(resultTableName).add(outDatedQuery.getLeft());
            }
        }

        for (Map.Entry<String, List<Long>> result : outDatedResults.entrySet()) {
            for (Long queryId : result.getValue()) {
                try {
                    log.debug("delete from " + result.getKey() + " where uid = " + queryId);
                    mybatisUtil.deleteHistoryResultById(result.getKey(), queryId);
                    requestHistoryManager.updateQueryStatus(queryId, JudgeStatus.DELETED);
                } catch (Exception e) {
                    requestHistoryManager.updateQueryStatus(queryId, JudgeStatus.FAILED);
                    throw new DBException("history query results impala delete error", e);
                }
            }
        }

        log.info("Collision cache cleaning done.");
    }

    /**
     * 服务重启后重新计算未完成的查询
     */
    public void reCalculateQueries() {
        log.info("Start recalculate unfinished queries:");
        List<QueryHistory> queryList = queryHistoryDao.findByStatus(JudgeStatus.CALCULATING.code());
        log.info("Unfinished queries num: " + queryList.size());
        for (QueryHistory queryHistory : queryList) {
            try {
                RequestType requestType = RequestType.getRequestType(queryHistory.getRequestType());
                JudgeExecutor judgeExecutor = JudgeUtil.getExecutor(requestType);
                JudgeRequest judgeRequest = JudgeUtil.getJudgeRequest(queryHistory.getRequestParameter(), requestType);

                long resultCount = judgeExecutor.cache(judgeRequest, queryHistory.getId());
                queryHistoryDao.updateResultCountAndStatus(queryHistory.getId(), resultCount, JudgeStatus.SUCCEED.code(), new Date());

                log.debug("query:{} finished", queryHistory.getId());
            } catch (Exception e) {
                requestHistoryManager.updateQueryStatus(queryHistory.getId(), JudgeStatus.FAILED);
                log.error("recalculate query error");
                e.printStackTrace();
            }
        }
        log.info("Unfinished queries recalculation done.");
    }

    private String getResultTableName(String requestType) {
        String tablePrefix = "judge_result_";
        switch (RequestType.getRequestType(requestType)) {
            case AREA_COLLISION:
                return tablePrefix + "area";
            case LINE_COLLISION:
                return tablePrefix + "line";
            case APPEAR_COLLISION:
            case DISAPPEAR_COLLISION:
                return tablePrefix + "appear";
            case FOLLOW_COLLISION:
                return tablePrefix + "follow";
            case MULTI_TRACK_COLLISION:
                return tablePrefix + "multi_track";
            case AGGREGATE_COLLISION:
                return tablePrefix + "aggregate";
            case DEVICE_ANALYSIS:
                return tablePrefix + "device_analysis";
            case TRACK_QUERY:
                return tablePrefix + "track_query";
            case TRACK_MERGE:
                return tablePrefix + "track_merge";
            case TRACK_COMPARE:
                return tablePrefix + "track_compare";
            case MULTI_FEATURE_ANALYSIS:
                return tablePrefix + "multi_feature";
            case INTIMATE_RELATION_ANALYSIS:
                return tablePrefix + "intimate_relation";
            case FILE_IMPORT:
                return tablePrefix + "file_import";
            case LOCATION_ANALYSIS:
            case MOVEMENT_ANALYSIS:
            case MINING_REPO:
            case REPO_IMPORT:
                return "";
            default:
                throw new ArgumentException("invalid request type name");
        }
    }
}
