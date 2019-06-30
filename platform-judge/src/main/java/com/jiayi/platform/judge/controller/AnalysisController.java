package com.jiayi.platform.judge.controller;

import com.jiayi.platform.judge.common.bean.GeneralRequest;
import com.jiayi.platform.judge.common.bean.PageResult;
import com.jiayi.platform.judge.enums.RequestType;
import com.jiayi.platform.judge.request.*;
import com.jiayi.platform.judge.service.JudgeService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 分析类研判
 * @author : weichengke
 * @date : 2019-04-18 16:58
 */
@RestController
@RequestMapping("analysis")
public class AnalysisController {
    @Autowired
    private JudgeService judgeService;

    @ApiOperation(value = "区域分析", notes = "在指定时空范围内，各个设备采集数据的情况分析")
    @PostMapping("/device")
    public PageResult<?> device(@RequestBody GeneralRequest<DeviceAnalysisRequest> request) {
        return judgeService.judge(request.getRequest(), request.getPageRequest(), RequestType.DEVICE_ANALYSIS);
    }

    @ApiOperation(value = "轨迹查询")
    @PostMapping("/trackQuery")
    public PageResult<?> trackQuery(@RequestBody GeneralRequest<TrackQueryRequest> request) {
        return judgeService.judge(request.getRequest(), request.getPageRequest(), RequestType.TRACK_QUERY);
    }

    @ApiOperation(value = "轨迹合并")
    @PostMapping("/trackMerge")
    public PageResult<?> trackMerge(@RequestBody GeneralRequest<TrackQueryRequest> request) {
        return judgeService.judge(request.getRequest(), request.getPageRequest(), RequestType.TRACK_MERGE);
    }

    @ApiOperation(value = "轨迹比对")
    @PostMapping("/trackCompare")
    public PageResult<?> trackCompare(@RequestBody GeneralRequest<TrackCompareRequest> request) {
        return judgeService.judge(request.getRequest(), request.getPageRequest(), RequestType.TRACK_COMPARE);
    }

    @ApiOperation(value = "地点分析")
    @PostMapping("/location")
    public PageResult<?> location(@RequestBody GeneralRequest<LocationAnalysisRequest> request) {
        return judgeService.judge(request.getRequest(), request.getPageRequest(), RequestType.LOCATION_ANALYSIS);
    }

    @ApiOperation(value = "驻留分析")
    @PostMapping("/movement")
    public PageResult<?> movement(@RequestBody GeneralRequest<MovementAnalysisRequest> request) {
        return judgeService.judge(request.getRequest(), request.getPageRequest(), RequestType.MOVEMENT_ANALYSIS);
    }

    @ApiOperation(value = "多特征分析")
    @PostMapping("/multiFeature")
    public PageResult<?> multiFeature(@RequestBody GeneralRequest<MultiFeatureAnalysisRequest> request) {
        return judgeService.judge(request.getRequest(), request.getPageRequest(), RequestType.MULTI_FEATURE_ANALYSIS);
    }

    @ApiOperation(value = "亲密关系分析")
    @PostMapping("/intimateRelation")
    public PageResult<?> intimateRelation(@RequestBody GeneralRequest<IntimateRelationAnalysisRequest> request) {
        return judgeService.judge(request.getRequest(), request.getPageRequest(), RequestType.INTIMATE_RELATION_ANALYSIS);
    }
}
