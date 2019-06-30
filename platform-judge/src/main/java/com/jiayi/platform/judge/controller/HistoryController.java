package com.jiayi.platform.judge.controller;

import com.jiayi.platform.common.web.dto.JsonObject;
import com.jiayi.platform.judge.common.bean.GeneralRequest;
import com.jiayi.platform.judge.common.bean.PageResult;
import com.jiayi.platform.judge.request.*;
import com.jiayi.platform.judge.response.HistoryRequestResponse;
import com.jiayi.platform.judge.response.QueryResultResponse;
import com.jiayi.platform.judge.service.HistoryQueryService;
import com.jiayi.platform.judge.service.JudgeService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author : weichengke
 * @date : 2019-04-18 17:05
 */
@RestController
@RequestMapping("history")
public class HistoryController {
    @Autowired
    private HistoryQueryService historyQueryService;
    @Autowired
    private JudgeService judgeService;

    @ApiOperation(value = "碰撞分析查询历史记录")
    @PostMapping("/requestSearch")
    public PageResult<List<HistoryRequestResponse>> searchQueryRequest (@RequestBody GeneralRequest<RequestSearchRequest> request) {
        return historyQueryService.searchQueryRequest(request.getRequest(), request.getPageRequest());
    }

    @ApiOperation(value = "碰撞分析查询历史结果")
    @SuppressWarnings("rawtypes")
    @PostMapping("/resultList")
    public PageResult<QueryResultResponse> getResult (@RequestBody GeneralRequest<QueryResultRequest> request) {
        return judgeService.judgeHistory(request.getRequest(), request.getPageRequest());
    }

    @ApiOperation(value = "案件中心碰撞分析历史记录")
    @PostMapping("/search")
    public JsonObject<?> searchRequestInfo (@RequestBody RequestRecordRequest request) {
        return historyQueryService.searchRequestHistoryInfo(request);
    }

    @ApiOperation(value = "案件中心分析记录类型")
    @GetMapping("/searchType")
    public JsonObject<?> searchType () {
        return historyQueryService.searchType();
    }

    @ApiOperation(value = "案件中心分析记录备注修改")
    @PostMapping("/updateRemark")
    public JsonObject<?> updateRemark (@RequestBody RequestRemarkRequest request) {
        return historyQueryService.updateRemark(request.getId(), request.getRemark());
    }

    @ApiOperation(value = "删除/移除碰撞分析记录")
    @PostMapping("/batch/{op}")
    public JsonObject<?> deleteRequestRecord (@RequestBody RequestModifyRequest request, @PathVariable String op) {
        return historyQueryService.deleteRequestRecord(request.getIds(), op);
    }

    @ApiOperation(value = "碰撞分析记录加入二次碰撞列表")
    @PostMapping("/aggregate/add/{id}")
    public JsonObject<?> addAggregate (@RequestBody RequestModifyRequest request, @PathVariable Long id) {
        return historyQueryService.addAggregate(id, request.getResultName());
    }

    @ApiOperation(value = "碰撞分析记录移出二次碰撞列表")
    @PostMapping("/aggregate/remove/{id}")
    public JsonObject<?> removeAggregate (@PathVariable Long id) {
        return historyQueryService.removeAggregate(id);
    }
}
