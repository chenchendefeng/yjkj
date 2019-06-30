package com.jiayi.platform.judge.util;

import com.jiayi.platform.common.exception.ArgumentException;
import com.jiayi.platform.common.util.JsonUtils;
import com.jiayi.platform.common.util.SpringUtil;
import com.jiayi.platform.judge.enums.JudgeDetailType;
import com.jiayi.platform.judge.enums.RequestType;
import com.jiayi.platform.judge.executor.*;
import com.jiayi.platform.judge.request.*;

import java.io.IOException;

public class JudgeUtil {
    public static JudgeExecutor getExecutor(RequestType requestType) {
        switch (requestType) {
            case LINE_COLLISION:
                return SpringUtil.getBean(LineCollisionExecutor.class);
            case AREA_COLLISION:
                return SpringUtil.getBean(AreaCollisionExecutor.class);
            case APPEAR_COLLISION:
                return SpringUtil.getBean(AppearCollisionExecutor.class);
            case DISAPPEAR_COLLISION:
                return SpringUtil.getBean(DisappearCollisionExecutor.class);
            case FOLLOW_COLLISION:
                return SpringUtil.getBean(FollowCollisionExecutor.class);
            case MULTI_TRACK_COLLISION:
                return SpringUtil.getBean(MultiTrackCollisionExecutor.class);
            case TRACK_QUERY:
                return SpringUtil.getBean(TrackQueryExecutor.class);
            case TRACK_MERGE:
                return SpringUtil.getBean(TrackMergeExecutor.class);
            case TRACK_COMPARE:
                return SpringUtil.getBean(TrackCompareExecutor.class);
            case DEVICE_ANALYSIS:
                return SpringUtil.getBean(DeviceAnalysisExecutor.class);
            case LOCATION_ANALYSIS:
                return SpringUtil.getBean(LocationAnalysisExecutor.class);
            case MOVEMENT_ANALYSIS:
                return SpringUtil.getBean(MovementAnalysisExecutor.class);
            case MULTI_FEATURE_ANALYSIS:
                return SpringUtil.getBean(MultiFeatureAnalysisExecutor.class);
            case INTIMATE_RELATION_ANALYSIS:
                return SpringUtil.getBean(IntimateRelationAnalysisExecutor.class);
            case AGGREGATE_COLLISION:
                return SpringUtil.getBean(AggregateExecutor.class);
            default:
                throw new ArgumentException("invalid requestType: " + requestType.typeName());
        }
    }

    public static JudgeDetailExecutor getDetailExecutor(JudgeDetailType requestType) {
        switch (requestType) {
            case LINE_DETAIL:
            case AREA_DETAIL:
            case APPEAR_DETAIL:
            case DISAPPEAR_DETAIL:
            case TRACK_DETAIL:
                return SpringUtil.getBean(TrackDetailExecutor.class);
            case AREA_CONDITION:
                return SpringUtil.getBean(AreaConditionExecutor.class);
            case FOLLOW_DETAIL:
                return SpringUtil.getBean(FollowTrackDetailExecutor.class);
            case MULTI_TRACK_DETAIL:
                return SpringUtil.getBean(MultiTrackDetailExecutor.class);
            case TRACK_QUERY_DETAIL:
                return SpringUtil.getBean(TrackQueryDetailExecutor.class);
            case DEVICE_ANALYSIS_DETAIL:
                return SpringUtil.getBean(DeviceAnalysisDetailExecutor.class);
            case LOCATION_ANALYSIS_DETAIL:
                return SpringUtil.getBean(LocationAnalysisDetailExecutor.class);
            case MOVEMENT_ANALYSIS_DETAIL:
                return SpringUtil.getBean(MovementAnalysisDetailExecutor.class);
            case MULTI_FEATURE_DETAIL:
                return SpringUtil.getBean(MultiFeatureDetailExecutor.class);
            case INTIMATE_RELATION_DETAIL:
                return SpringUtil.getBean(IntimateRelationDetailExecutor.class);
            case MULTI_FEATURE_TRACK_DETAIL:
            case INTIMATE_RELATION_TRACK_DETAIL:
                return SpringUtil.getBean(MultiFeatureTrackDetailExecutor.class);
            default:
                throw new ArgumentException("invalid requestType " + requestType.toString());
        }
    }

    public static JudgeRequest getJudgeRequest(String requestParameter, RequestType requestType) {
        try {
            switch (requestType) {
                case LINE_COLLISION:
                    return JsonUtils.parse(requestParameter, LineCollisionRequest.class);
                case AREA_COLLISION:
                    return JsonUtils.parse(requestParameter, AreaCollisionRequest.class);
                case APPEAR_COLLISION:
                case DISAPPEAR_COLLISION:
                    return JsonUtils.parse(requestParameter, AppearCollisionRequest.class);
                case FOLLOW_COLLISION:
                    return JsonUtils.parse(requestParameter, FollowCollisionRequest.class);
                case MULTI_TRACK_COLLISION:
                    return JsonUtils.parse(requestParameter, MultiTrackCollisionRequest.class);
                case DEVICE_ANALYSIS:
                    return JsonUtils.parse(requestParameter, DeviceAnalysisRequest.class);
                case MULTI_FEATURE_ANALYSIS:
                    return JsonUtils.parse(requestParameter, MultiFeatureAnalysisRequest.class);
                case INTIMATE_RELATION_ANALYSIS:
                    return JsonUtils.parse(requestParameter, IntimateRelationAnalysisRequest.class);
                case REPO_IMPORT:
                    return JsonUtils.parse(requestParameter, MonitorRepoParam.class);
                case AGGREGATE_COLLISION:
                    return JsonUtils.parse(requestParameter, AggregateRequest.class);
                case FILE_IMPORT:
                    return JsonUtils.parse(requestParameter, FileImportParam.class);
                case MINING_REPO:
                    return JsonUtils.parse(requestParameter, MiningQueryHistoryParam.class);
                default:
                    throw new ArgumentException("invalid request type");
            }
        } catch (IOException e) {
            throw new ArgumentException("invalid request type", e);
        }
    }
}
