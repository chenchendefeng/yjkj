package com.jiayi.platform.judge.controller;

import com.jiayi.platform.judge.common.bean.GeneralRequest;
import com.jiayi.platform.judge.common.bean.PageResult;
import com.jiayi.platform.judge.common.bean.Result;
import com.jiayi.platform.judge.enums.JudgeDetailType;
import com.jiayi.platform.judge.request.*;
import com.jiayi.platform.judge.service.JudgeDetailService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 轨迹详情查询
 * @author : weichengke
 * @date : 2019-04-18 16:58
 */
@RestController
@RequestMapping("detail")
public class TrackController {
    @Autowired
    private JudgeDetailService judgeDetailService;

    @ApiOperation(value = "路线碰撞详细轨迹", notes = "线路碰撞每个碰撞结果的详细轨迹列表")
    @PostMapping("/lineDetail")
    public PageResult<?> line(@RequestBody GeneralRequest<TrackDetailRequest> request) {
        return judgeDetailService.judge(request.getRequest(), request.getPageRequest(), JudgeDetailType.LINE_DETAIL);
    }

    @ApiOperation(value = "区域碰撞匹配情况", notes = "区域碰撞每个碰撞结果各条件的匹配情况")
    @PostMapping("/areaCondition")
    public PageResult<?> areaCondition(@RequestBody GeneralRequest<AreaConditionRequest> request) {
        return judgeDetailService.judge(request.getRequest(), request.getPageRequest(), JudgeDetailType.AREA_CONDITION);
    }

    @ApiOperation(value = "区域碰撞详细轨迹", notes = "区域碰撞每个匹配条件的详细轨迹列表")
    @PostMapping("/areaDetail")
    public PageResult<?> area(@RequestBody GeneralRequest<TrackDetailRequest> request) {
        return judgeDetailService.judge(request.getRequest(), request.getPageRequest(), JudgeDetailType.AREA_DETAIL);
    }

    @ApiOperation(value = "对象出现详细轨迹")
    @PostMapping("/appearDetail")
    public PageResult<?> appear(@RequestBody GeneralRequest<TrackDetailRequest> request) {
        return judgeDetailService.judge(request.getRequest(), request.getPageRequest(), JudgeDetailType.APPEAR_DETAIL);
    }

    @ApiOperation(value = "对象消失详细轨迹")
    @PostMapping("/disappearDetail")
    public PageResult<?> disappear(@RequestBody GeneralRequest<TrackDetailRequest> request) {
        return judgeDetailService.judge(request.getRequest(), request.getPageRequest(), JudgeDetailType.DISAPPEAR_DETAIL);
    }

    @ApiOperation(value = "伴随碰撞比对列表")
    @PostMapping("/followDetail")
    public PageResult<?> follow(@RequestBody GeneralRequest<FollowTrackDetailRequest> request) {
        return judgeDetailService.judge(request.getRequest(), request.getPageRequest(), JudgeDetailType.FOLLOW_DETAIL);
    }

    @ApiOperation(value = "多轨碰撞比对列表")
    @PostMapping("/multitrackDetail")
    public PageResult<?> multiTrack(@RequestBody GeneralRequest<MultiTrackDetailRequest> request) {
        return judgeDetailService.judge(request.getRequest(), request.getPageRequest(), JudgeDetailType.MULTI_TRACK_DETAIL);
    }

    @ApiOperation(value = "轨迹查询详细轨迹")
    @PostMapping("/trackQueryDetail")
    public PageResult<?> trackQuery(@RequestBody GeneralRequest<TrackDetailRequest> request) {
        return judgeDetailService.judge(request.getRequest(), request.getPageRequest(), JudgeDetailType.TRACK_QUERY_DETAIL);
    }

    @ApiOperation(value = "区域分析详细轨迹")
    @PostMapping("/deviceDetail")
    public PageResult<?> device(@RequestBody GeneralRequest<DeviceAnalysisDetailRequest> request) {
        return judgeDetailService.judge(request.getRequest(), request.getPageRequest(), JudgeDetailType.DEVICE_ANALYSIS_DETAIL);
    }

    @ApiOperation(value = "区域分析图表统计", notes = "区域分析按天、周、月统计图表展示")
    @PostMapping("/deviceStat")
    public Result<?> deviceStat(@RequestBody GeneralRequest<DeviceAnalysisStatRequest> request) {
        return judgeDetailService.judgeDeviceAnalysisStat(request.getRequest());
    }

    @ApiOperation(value = "地点分析详细轨迹")
    @PostMapping("/locationDetail")
    public PageResult<?> location(@RequestBody GeneralRequest<LocationAnalysisDetailRequest> request) {
        return judgeDetailService.judge(request.getRequest(), request.getPageRequest(), JudgeDetailType.LOCATION_ANALYSIS_DETAIL);
    }

    @ApiOperation(value = "驻留分析详细轨迹")
    @PostMapping("/movementDetail")
    public PageResult<?> movement(@RequestBody GeneralRequest<MovementAnalysisDetailRequest> request) {
        return judgeDetailService.judge(request.getRequest(), request.getPageRequest(), JudgeDetailType.MOVEMENT_ANALYSIS_DETAIL);
    }

    @ApiOperation(value = "多特征分析匹配结果")
    @PostMapping("/multiFeatureDetail")
    public PageResult<?> multiFeature(@RequestBody GeneralRequest<MultiFeatureDetailRequest> request) {
        return judgeDetailService.judge(request.getRequest(), request.getPageRequest(), JudgeDetailType.MULTI_FEATURE_DETAIL);
    }

    @ApiOperation(value = "多特征分析详细轨迹")
    @PostMapping("/multiFeatureTrack")
    public PageResult<?> multiFeatureTrack(@RequestBody GeneralRequest<MultiFeatureTrackDetailRequest> request) {
        return judgeDetailService.judge(request.getRequest(), request.getPageRequest(), JudgeDetailType.MULTI_FEATURE_TRACK_DETAIL);
    }

    @ApiOperation(value = "亲密关系分析匹配结果")
    @PostMapping("/intimateRelationDetail")
    public PageResult<?> intimateRelation(@RequestBody GeneralRequest<IntimateRelationDetailRequest> request) {
        return judgeDetailService.judge(request.getRequest(), request.getPageRequest(), JudgeDetailType.INTIMATE_RELATION_DETAIL);
    }

    @ApiOperation(value = "亲密关系分析详细轨迹")
    @PostMapping("/intimateRelationTrack")
    public PageResult<?> intimateRelationTrack(@RequestBody GeneralRequest<MultiFeatureTrackDetailRequest> request) {
        return judgeDetailService.judge(request.getRequest(), request.getPageRequest(), JudgeDetailType.INTIMATE_RELATION_TRACK_DETAIL);
    }
}
