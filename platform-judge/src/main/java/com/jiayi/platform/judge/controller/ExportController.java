package com.jiayi.platform.judge.controller;

import com.jiayi.platform.judge.enums.JudgeDetailType;
import com.jiayi.platform.judge.enums.RequestType;
import com.jiayi.platform.judge.request.*;
import com.jiayi.platform.judge.service.ExportService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("export")
public class ExportController {
    @Autowired
    private ExportService exportService;

    @ApiOperation(value = "路线碰撞结果导出")
    @PostMapping("/lineDownload")
    public void lineDownload(@RequestBody LineCollisionRequest request, HttpServletResponse response) {
        exportService.exportJudgeResult(request, response, RequestType.LINE_COLLISION);
    }

    @ApiOperation(value = "区域碰撞结果导出")
    @PostMapping("/areaDownload")
    public void areaDownload(@RequestBody AreaCollisionRequest request, HttpServletResponse response) {
        exportService.exportJudgeResult(request, response, RequestType.AREA_COLLISION);
    }

    @ApiOperation(value = "区域碰撞匹配结果导出")
    @PostMapping("/areaConditionDownload")
    public void areaConditionDownload(@RequestBody AreaConditionRequest request, HttpServletResponse response) {
        exportService.exportJudgeDetailResult(request, response, JudgeDetailType.AREA_CONDITION);
    }

    @ApiOperation(value = "对象出现结果导出")
    @PostMapping("/appearDownload")
    public void appearDownload(@RequestBody AppearCollisionRequest request, HttpServletResponse response) {
        exportService.exportJudgeResult(request, response, RequestType.APPEAR_COLLISION);
    }

    @ApiOperation(value = "对象消失结果导出")
    @PostMapping(value = "/disappearDownload")
    public void disappearDownload(@RequestBody AppearCollisionRequest request, HttpServletResponse response) {
        exportService.exportJudgeResult(request, response, RequestType.DISAPPEAR_COLLISION);
    }

    @ApiOperation(value = "伴随碰撞结果导出")
    @PostMapping("/followDownload")
    public void followDownload(@RequestBody FollowCollisionRequest request, HttpServletResponse response) {
        exportService.exportJudgeResult(request, response, RequestType.FOLLOW_COLLISION);
    }

    @ApiOperation(value = "伴随碰撞比对列表导出")
    @PostMapping("/followDetailDownload")
    public void followDetailDownload(@RequestBody FollowTrackDetailRequest request, HttpServletResponse response) {
        exportService.exportJudgeDetailResult(request, response, JudgeDetailType.FOLLOW_DETAIL);
    }

    @ApiOperation(value = "多轨碰撞结果导出")
    @PostMapping("/multitrackDownload")
    public void multiTrackDownload(@RequestBody MultiTrackCollisionRequest request, HttpServletResponse response) {
        exportService.exportJudgeResult(request, response, RequestType.MULTI_TRACK_COLLISION);
    }

    @ApiOperation(value = "多轨碰撞比对列表导出")
    @PostMapping("/multitrackDetailDownload")
    public void multiTrackDetailDownload(@RequestBody MultiTrackDetailRequest request, HttpServletResponse response) {
        exportService.exportJudgeDetailResult(request, response, JudgeDetailType.MULTI_TRACK_DETAIL);
    }

    @ApiOperation(value = "碰撞查询详细轨迹导出", notes = "路线碰撞/区域碰撞/对象出现/消失的详细轨迹导出")
    @PostMapping("/trackDetailDownload")
    public void trackDetailDownload(@RequestBody TrackDetailRequest request, HttpServletResponse response) {
        exportService.exportJudgeDetailResult(request, response, JudgeDetailType.TRACK_DETAIL);
    }

    @ApiOperation(value = "区域分析结果导出")
    @PostMapping("/deviceDownload")
    public void deviceDownload(@RequestBody DeviceAnalysisRequest request, HttpServletResponse response) {
        exportService.exportJudgeResult(request, response, RequestType.DEVICE_ANALYSIS);
    }

    @ApiOperation(value = "区域分析详细轨迹导出")
    @PostMapping("/deviceDetailDownload")
    public void deviceDetailDownload(@RequestBody DeviceAnalysisDetailRequest request, HttpServletResponse response) {
        exportService.exportJudgeDetailResult(request, response, JudgeDetailType.DEVICE_ANALYSIS_DETAIL);
    }

    @ApiOperation(value = "轨迹查询结果导出")
    @PostMapping("/trackQueryDownload")
    public void trackQueryDownload(@RequestBody TrackQueryRequest request, HttpServletResponse response) {
        exportService.exportJudgeResult(request, response, RequestType.TRACK_QUERY);
    }

    @ApiOperation(value = "轨迹查询详细轨迹导出")
    @PostMapping("/trackQueryDetailDownload")
    public void trackQueryDetailDownload(@RequestBody TrackDetailRequest request, HttpServletResponse response) {
        exportService.exportJudgeDetailResult(request, response, JudgeDetailType.TRACK_QUERY_DETAIL);
    }

    @ApiOperation(value = "轨迹合并结果导出")
    @PostMapping("/trackMergeDownload")
    public void trackMergeDownload(@RequestBody TrackQueryRequest request, HttpServletResponse response) {
        exportService.exportJudgeResult(request, response, RequestType.TRACK_MERGE);
    }

//    @ApiOperation(value = "轨迹比对结果导出")
//    @PostMapping("/trackCompareDownload")
//    public void trackCompareDownload(@RequestBody TrackCompareRequest request, HttpServletResponse response) {
//        exportService.exportJudgeResult(request, response, RequestType.TRACK_COMPARE);
//    }

    @ApiOperation(value = "地点分析结果导出")
    @PostMapping("/locationDownload")
    public void locationDownload(@RequestBody LocationAnalysisRequest request, HttpServletResponse response) {
        exportService.exportJudgeResult(request, response, RequestType.LOCATION_ANALYSIS);
    }

    @ApiOperation(value = "地点分析详细轨迹导出")
    @PostMapping("/locationDetailDownload")
    public void locationDetailDownload(@RequestBody LocationAnalysisDetailRequest request, HttpServletResponse response) {
        exportService.exportJudgeDetailResult(request, response, JudgeDetailType.LOCATION_ANALYSIS_DETAIL);
    }

    @ApiOperation(value = "驻留分析结果导出")
    @PostMapping("/movementDownload")
    public void movementDownload(@RequestBody MovementAnalysisRequest request, HttpServletResponse response) {
        exportService.exportJudgeResult(request, response, RequestType.MOVEMENT_ANALYSIS);
    }

    @ApiOperation(value = "驻留分析详细轨迹导出")
    @PostMapping("/movementDetailDownload")
    public void movementDetailDownload(@RequestBody MovementAnalysisDetailRequest request, HttpServletResponse response) {
        exportService.exportJudgeDetailResult(request, response, JudgeDetailType.MOVEMENT_ANALYSIS_DETAIL);
    }

    @ApiOperation(value = "多特征分析结果导出")
    @PostMapping("/multiFeatureDownload")
    public void multiFeatureDownload(@RequestBody MultiFeatureAnalysisRequest request, HttpServletResponse response) {
        exportService.exportJudgeResult(request, response, RequestType.MULTI_FEATURE_ANALYSIS);
    }

    @ApiOperation(value = "多特征分析匹配结果导出")
    @PostMapping("/multiFeatureDetailDownload")
    public void multiFeatureDetailDownload(@RequestBody MultiFeatureDetailRequest request, HttpServletResponse response) {
        exportService.exportJudgeDetailResult(request, response, JudgeDetailType.MULTI_FEATURE_DETAIL);
    }

    @ApiOperation(value = "多特征分析详细轨迹导出")
    @PostMapping("/multiFeatureTrackDownload")
    public void multiFeatureTrackDownload(@RequestBody MultiFeatureTrackDetailRequest request, HttpServletResponse response) {
        exportService.exportJudgeDetailResult(request, response, JudgeDetailType.MULTI_FEATURE_TRACK_DETAIL);
    }

    @ApiOperation(value = "亲密关系分析结果导出")
    @PostMapping("/intimateRelationDownload")
    public void intimateRelationDownload(@RequestBody IntimateRelationAnalysisRequest request, HttpServletResponse response) {
        exportService.exportJudgeResult(request, response, RequestType.INTIMATE_RELATION_ANALYSIS);
    }

    @ApiOperation(value = "亲密关系分析匹配结果导出")
    @PostMapping("/intimateRelationDetailDownload")
    public void intimateRelationDetailDownload(@RequestBody IntimateRelationDetailRequest request, HttpServletResponse response) {
        exportService.exportJudgeDetailResult(request, response, JudgeDetailType.INTIMATE_RELATION_DETAIL);
    }

    @ApiOperation(value = "亲密关系分析详细轨迹导出")
    @PostMapping("/intimateRelationTrackDownload")
    public void intimateRelationTrackDownload(@RequestBody MultiFeatureTrackDetailRequest request, HttpServletResponse response) {
        exportService.exportJudgeDetailResult(request, response, JudgeDetailType.INTIMATE_RELATION_TRACK_DETAIL);
    }

//    @ApiOperation(value = "二次碰撞记录结果导出")
//    @PostMapping(value = "/aggregateDownload")
//    public void aggregateDownload(@RequestBody QueryResultRequest request, HttpServletResponse response) {
//        exportService.exportJudgeResult(request, response, RequestType.AGGREGATE_COLLISION);
//    }
}
