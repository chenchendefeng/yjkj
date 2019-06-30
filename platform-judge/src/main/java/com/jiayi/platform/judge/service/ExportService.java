package com.jiayi.platform.judge.service;

import com.jiayi.platform.common.enums.CollectType;
import com.jiayi.platform.common.exception.ArgumentException;
import com.jiayi.platform.common.exception.ServiceException;
import com.jiayi.platform.common.web.util.ExportUtil;
import com.jiayi.platform.judge.dao.mysql.CollisionResultFieldDao;
import com.jiayi.platform.judge.entity.mysql.QueryHistory;
import com.jiayi.platform.judge.enums.JudgeDetailType;
import com.jiayi.platform.judge.enums.JudgeStatus;
import com.jiayi.platform.judge.enums.RequestType;
import com.jiayi.platform.judge.executor.JudgeDetailExecutor;
import com.jiayi.platform.judge.executor.JudgeExecutor;
import com.jiayi.platform.judge.manage.HistoryQueryResultManager;
import com.jiayi.platform.judge.manage.RequestHistoryManager;
import com.jiayi.platform.judge.request.*;
import com.jiayi.platform.judge.response.QueryResultFieldInfo;
import com.jiayi.platform.judge.util.JudgeUtil;
import com.jiayi.platform.library.minerepo.manager.MiningRepoCacheManager;
import com.jiayi.platform.library.minerepo.vo.MiningRepoTableDesc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ExportService {
    @Autowired
    private RequestHistoryManager requestHistoryManager;
    @Autowired
    private HistoryQueryResultManager historyQueryResultManager;
    @Autowired
    private MiningRepoCacheManager miningRepoCacheManager;
    @Autowired
    private CollisionResultFieldDao collisionResultFieldDao;

    private static final String DATA_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final SimpleDateFormat SDF = new SimpleDateFormat(DATA_PATTERN);
    public static final int LOAD_SIZE = 50000;

    /**
     * 碰撞分析查询结果导出
     */
    public <T extends JudgeRequest> void exportJudgeResult(T request, HttpServletResponse response, RequestType requestType) {
        QueryHistory history = requestHistoryManager.findLatestQueryHistory(request, requestType);
        if (history == null || history.getStatus() != JudgeStatus.SUCCEED.code()) {
            throw new ServiceException("results cannot be exported now");
        }
        // TODO update case time?
        List<String> contents = new ArrayList<>();
        try {
            int index = 0;
            int dataSize;
            switch (requestType) {
                // 研判工具类（包括二次碰撞）的结果导出
                case LINE_COLLISION:
                case AREA_COLLISION:
                case APPEAR_COLLISION:
                case DISAPPEAR_COLLISION:
                case FOLLOW_COLLISION:
                case MULTI_TRACK_COLLISION:
                case DEVICE_ANALYSIS:
                case TRACK_QUERY:
                case TRACK_MERGE:
                case LOCATION_ANALYSIS:
                case MOVEMENT_ANALYSIS:
                case MULTI_FEATURE_ANALYSIS:
                case INTIMATE_RELATION_ANALYSIS:
                case AGGREGATE_COLLISION:
                    JudgeExecutor executor = JudgeUtil.getExecutor(requestType);
                    do {
                        long offset = LOAD_SIZE * (index);
                        dataSize = executor.exportResult(contents, request, offset, history.getId());
                        index++;
                    } while (dataSize == LOAD_SIZE);
                    break;
                case FILE_IMPORT:
                    do {
                        long offset = LOAD_SIZE * (index);
                        dataSize = historyQueryResultManager.exportFileImportResult(contents, offset, history.getId());
                        index++;
                    } while (dataSize == LOAD_SIZE);
                    break;
                case MINING_REPO:
                    do {
                        dataSize = historyQueryResultManager.exportMiningRepoResult(contents, (MiningQueryHistoryParam) request, index);
                        index++;
                    } while (dataSize == LOAD_SIZE);
                    break;
                case REPO_IMPORT:
                    do {
                        dataSize = historyQueryResultManager.exportRepoImportResult(contents, (MonitorRepoParam) request, index);
                        index++;
                    } while (dataSize == LOAD_SIZE);
                    break;
                default:
                    throw new ArgumentException("invalid request type for result export");
            }
        } catch (Exception e) {
            log.error(requestType.description() + " impala search error", e);
        }
        String fileName = requestType.name() + "_" + System.currentTimeMillis();
        String colNames;
        switch (requestType) {
            case LINE_COLLISION:
            case AREA_COLLISION:
            case APPEAR_COLLISION:
            case DISAPPEAR_COLLISION:
            case FOLLOW_COLLISION:
            case MULTI_TRACK_COLLISION:
            case DEVICE_ANALYSIS:
            case TRACK_QUERY:
            case TRACK_MERGE:
            case LOCATION_ANALYSIS:
            case MOVEMENT_ANALYSIS:
            case MULTI_FEATURE_ANALYSIS:
            case INTIMATE_RELATION_ANALYSIS:
            case AGGREGATE_COLLISION:
                colNames = getColNames(request, requestType, history.getId());
                break;
            case FILE_IMPORT:
                colNames = collisionResultFieldDao.getResultFieldInfoByTmplId(history.getId())
                        .stream().map(QueryResultFieldInfo::getText).collect(Collectors.joining(","));
                break;
            case MINING_REPO:
                colNames = miningRepoCacheManager.getMineReopById(((MiningQueryHistoryParam) request).getRepoId()).getDetailTableDescObj()
                        .getFields().stream().map(MiningRepoTableDesc.FieldDesc::getDesc).collect(Collectors.joining(","));
                break;
            case REPO_IMPORT:
                colNames = String.join(",", ((MonitorRepoParam) request).getObjTypes());
                break;
            default:
                throw new ArgumentException("invalid request type for result export");
        }
        if (!ExportUtil.doExport(contents, colNames, fileName, response))
            log.error("writing csv file error!");
    }

    /**
     * 碰撞分析详情结果导出
     */
    public <T extends JudgeDetailRequest> void exportJudgeDetailResult(T request, HttpServletResponse response, JudgeDetailType requestType) {
        // TODO update case time?
        List<String> contents = new ArrayList<>();
        try {
            int index = 0;
            int dataSize;
            JudgeDetailExecutor executor = JudgeUtil.getDetailExecutor(requestType);
            do {
                long offset = LOAD_SIZE * (index);
                dataSize = executor.exportResult(contents, request, offset);
                index++;
            } while (dataSize == LOAD_SIZE);
        } catch (Exception e) {
            log.error(requestType.description() + " impala search error", e);
        }
        String fileName = requestType.name() + "_" + System.currentTimeMillis();
        if (!ExportUtil.doExport(contents, getDetailColNames(request, requestType), fileName, response))
            log.error("writing csv file error!");
    }

    private <T extends JudgeRequest> String getColNames(T request, RequestType requestType, Long queryId) {
        switch (requestType) {
            case LINE_COLLISION:
                return CollectType.valueOf(request.getObjectTypeName().toUpperCase()).desc()
                        + ",区域数,轨迹数,路线开始时间,路线结束时间,路线开始位置,路线结束位置," + getDescName(request.getObjectTypeName());
            case AREA_COLLISION:
                return CollectType.valueOf(request.getObjectTypeName().toUpperCase()).desc()
                        + ",匹配条件,匹配次数,开始时间,结束时间,开始位置,结束位置," + getDescName(request.getObjectTypeName());
            case APPEAR_COLLISION:
            case DISAPPEAR_COLLISION:
                return CollectType.valueOf(request.getObjectTypeName().toUpperCase()).desc()
                        + ",开始时间,结束时间,开始位置,结束位置," + getDescName(request.getObjectTypeName());
            case FOLLOW_COLLISION:
                return CollectType.valueOf(request.getObjectTypeName().toUpperCase()).desc()
                        + ",地点数,伴随数,轨迹数,伴随开始时间,伴随结束时间,伴随开始位置,伴随结束位置," + getDescName(request.getObjectTypeName());
            case MULTI_TRACK_COLLISION:
                return CollectType.valueOf(((MultiTrackCollisionRequest) request).getFollowObjectTypeName().toUpperCase()).desc()
                        + ",区域数,伴随数,轨迹数,伴随开始时间,伴随结束时间,伴随开始位置,伴随结束位置," + getDescName(request.getObjectTypeName());
            case DEVICE_ANALYSIS:
                return CollectType.valueOf(request.getObjectTypeName().toUpperCase()).desc()
                    + ",连接次数,开始时间,结束时间," + getDescName(request.getObjectTypeName());
            case TRACK_QUERY:
                return "数据类型,设备/账号,IMSI/IMEI,开始时间,结束时间,开始位置,结束位置,轨迹数,地点数,设备详情";
            case TRACK_MERGE:
                return "数据类型,设备/账号,IMSI/IMEI,采集时间,出现地点,设备详情";
//            case TRACK_COMPARE:
//                return "状态,原始目标时间,原始目标地点,匹配目标时间,匹配目标地点,时间差(s),设备距离(m)";
            case LOCATION_ANALYSIS:
                switch (((LocationAnalysisRequest)request).getCountTime()) {
                    case 0:
                        return "编号,地址信息(设备数量),统计次数,00:00-05:59,06:00-11:59,12:00-17:59,18:00-23:59";
                    case 1:
                        return "编号,地址信息(设备数量),统计次数,星期一,星期二,星期三,星期四,星期五,星期六,星期天";
                    case 2:
                        return "编号,地址信息(设备数量),统计次数,1日-5日,6日-10日,11日-15日,16日-20日,21日-日25,26日-31日";
                }
            case MOVEMENT_ANALYSIS:
                return "编号,地址信息(设备数量),日均驻留时长/小时,最早出现时间,最晚出现时间,入圈时间统计,出圈时间统计,驻留时间统计";
            case MULTI_FEATURE_ANALYSIS:
                return "数据类型,数据值,匹配天数,匹配系数,匹配轨迹数,详细信息";
            case INTIMATE_RELATION_ANALYSIS:
                return "数据类型,数据值,匹配天数,匹配轨迹数,详细信息";
            case AGGREGATE_COLLISION:
                List<QueryResultFieldInfo> resultFieldInfo = historyQueryResultManager.getAggregateResultFieldInfo(queryId);
                return resultFieldInfo.stream().map(QueryResultFieldInfo::getText).collect(Collectors.joining(","));
            default:
                throw new ArgumentException("invalid export requestType: " + requestType.typeName());
        }
    }

    private <T extends JudgeDetailRequest> String getDetailColNames(T request, JudgeDetailType requestType) {
        switch (requestType) {
            case AREA_CONDITION:
                return "匹配条件,区域开始时间,区域结束时间";
            case FOLLOW_DETAIL:
                return ("imei".equals(request.getObjectTypeName().toLowerCase())) ? "IMSI," : ("imsi".equals(request.getObjectTypeName()) ? ",IMEI" : "")
                        + "地点,原始时间,匹配时间,伴随间隔(秒)";
            case MULTI_TRACK_DETAIL:
                return ("imei".equals(request.getObjectTypeName().toLowerCase())) ? "IMSI," : ("imsi".equals(request.getObjectTypeName()) ? ",IMEI" : "")
                        + "原始时间,匹配时间,伴随间隔(秒),原始地点,伴随地点,伴随距离(米)";
            case TRACK_DETAIL:
                return ("imei".equals(request.getObjectTypeName().toLowerCase())) ? "IMSI," : ("imsi".equals(request.getObjectTypeName()) ? ",IMEI" : "")
                        + "时间,地点";
            case DEVICE_ANALYSIS_DETAIL:
                return "时间,地点";
            case TRACK_QUERY_DETAIL:
            case LOCATION_ANALYSIS_DETAIL:
            case MOVEMENT_ANALYSIS_DETAIL:
                switch(request.getObjectTypeName().toLowerCase()) {
                    case "mac":
                        return "时间,地点,热点MAC,热点名称,信道,场强";
                    case "carno":
                    case "imsi":
                        return "时间,地点,IMEI";
                    case "imei":
                        return "时间,地点,IMSI";
                }
            case MULTI_FEATURE_DETAIL:
                return "日期,匹配系数,匹配轨迹数,开始时间,开始地点,结束时间,结束地点";
            case INTIMATE_RELATION_DETAIL:
                return "日期,匹配轨迹数,开始时间,开始地点,结束时间,结束地点";
            case MULTI_FEATURE_TRACK_DETAIL:
            case INTIMATE_RELATION_TRACK_DETAIL:
                return "状态,原始目标时间,匹配目标时间,时间差(秒),原始目标地点,匹配目标地点,距离差(米)";
            default:
                throw new ArgumentException("invalid export requestType: " + requestType.name());
        }
    }

    private String getDescName(String type) {
        return type.toLowerCase().equals("carno") ? "归属地" : (type.toLowerCase().equals("imsi") ? "运营商" : "厂商");
    }
}
