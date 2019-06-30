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
 * 碰撞类研判
 *
 * @author : weichengke
 * @date : 2019-04-18 16:53
 */
@RestController
@RequestMapping("collision")
public class CollisionController {
    @Autowired
    private JudgeService judgeService;

    @ApiOperation(value = "路线碰撞", notes = "在指定时空范围内，轨迹点数大于匹配系数，则认为碰撞成功")
    @PostMapping("/line")
    public PageResult<?> line(@RequestBody GeneralRequest<LineCollisionRequest> request) {
        return judgeService.judge(request.getRequest(), request.getPageRequest(), RequestType.LINE_COLLISION);
    }

    @ApiOperation(value = "区域碰撞", notes = "指定对象在一组时空范围内出现的的次数大于等于匹配系数，则认为区域碰撞成功")
    @PostMapping("/area")
    public PageResult<?> area(@RequestBody GeneralRequest<AreaCollisionRequest> request) {
        return judgeService.judge(request.getRequest(), request.getPageRequest(), RequestType.AREA_COLLISION);
    }

    @ApiOperation(value = "对象出现", notes = "在指定时空范围类有轨迹点，在另一个指定时空范围不存在轨迹点")
    @PostMapping("/appear")
    public PageResult<?> appear(@RequestBody GeneralRequest<AppearCollisionRequest> request) {
        return judgeService.judge(request.getRequest(), request.getPageRequest(), RequestType.APPEAR_COLLISION);
    }

    @ApiOperation(value = "对象消失", notes = "如果轨迹表中的对象，在指定的参考时空范围出现，在分析时空范围不出现，则说明对象消失")
    @PostMapping("/disappear")
    public PageResult<?> disappear(@RequestBody GeneralRequest<AppearCollisionRequest> request) {
        return judgeService.judge(request.getRequest(), request.getPageRequest(), RequestType.DISAPPEAR_COLLISION);
    }

    @ApiOperation(value = "伴随碰撞", notes = "指定时空范围内，与指定对象出现的时间差少于指定值的匹配地点数大于等于匹配系数，则认定为伴随")
    @PostMapping("/follow")
    public PageResult<?> follow(@RequestBody GeneralRequest<FollowCollisionRequest> request) {
        return judgeService.judge(request.getRequest(), request.getPageRequest(), RequestType.FOLLOW_COLLISION);
    }

    @ApiOperation(value = "多轨碰撞", notes = "指定时空范围内，与指定对象出现的时间差少于指定值，并且相隔距离小于指定距离的匹配地点数大于等于匹配系数，则碰撞成功")
    @PostMapping("/multitrack")
    public PageResult<?> multiTrack(@RequestBody GeneralRequest<MultiTrackCollisionRequest> request) {
        return judgeService.judge(request.getRequest(), request.getPageRequest(), RequestType.MULTI_TRACK_COLLISION);
    }
}
