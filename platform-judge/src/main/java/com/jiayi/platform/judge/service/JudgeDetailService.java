package com.jiayi.platform.judge.service;

import com.jiayi.platform.common.exception.DBException;
import com.jiayi.platform.common.util.SpringUtil;
import com.jiayi.platform.common.util.ThreadPoolUtil;
import com.jiayi.platform.judge.common.bean.PageInfo;
import com.jiayi.platform.judge.common.bean.PageRequest;
import com.jiayi.platform.judge.common.bean.PageResult;
import com.jiayi.platform.judge.common.bean.Result;
import com.jiayi.platform.judge.dto.DeviceAnalysisStatDto;
import com.jiayi.platform.judge.enums.JudgeDetailType;
import com.jiayi.platform.judge.executor.*;
import com.jiayi.platform.judge.request.DeviceAnalysisStatRequest;
import com.jiayi.platform.judge.request.JudgeDetailRequest;
import com.jiayi.platform.judge.util.JudgeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Future;

@Service
@Slf4j
public class JudgeDetailService {

    public PageResult judge(JudgeDetailRequest request, PageRequest pageRequest, String requestType) {
        return judge(request, pageRequest, JudgeDetailType.valueOf(requestType.toUpperCase()));
    }

    public PageResult judge(JudgeDetailRequest request, PageRequest pageRequest, JudgeDetailType requestType) {
        long start = System.currentTimeMillis();

        JudgeDetailExecutor judgeExecutor = JudgeUtil.getDetailExecutor(requestType);

        List<?> queryResult;
        long count;
        Future<List<?>>  dataFuture = ThreadPoolUtil.getInstance().submit(() -> judgeExecutor.query(request, pageRequest));
        Future<Long>  countFuture = ThreadPoolUtil.getInstance().submit(() -> judgeExecutor.count(request));

        try {
            queryResult = dataFuture.get();
            count = countFuture.get();
        } catch (Exception e) {
            throw new DBException(requestType + " query error", e);
        }

        // 封装前端需要的数据格式
        PageInfo pageInfo = new PageInfo(queryResult.size(), count, pageRequest);
        PageResult<List<?>> result = new PageResult<>();
        result.setPageInfo(pageInfo);
        result.setPayload(judgeExecutor.convert2Response(queryResult, request.getObjectTypeName()));
        log.info(requestType + " time used: " + (System.currentTimeMillis() - start) / 100 / 10.0 + "s");
        return result;
    }

    /**
     * 区域分析图表统计查询
     */
    public Result judgeDeviceAnalysisStat(DeviceAnalysisStatRequest request) {
        long start = System.currentTimeMillis();
        DeviceAnalysisStatExecutor judgeExecutor = SpringUtil.getBean(DeviceAnalysisStatExecutor.class);
        try {
            List<DeviceAnalysisStatDto> queryResult = judgeExecutor.query(request);

            log.info("device analysis stat time used: " + (System.currentTimeMillis() - start) / 100 / 10.0 + "s");
            return new Result<>(judgeExecutor.convert2Response(queryResult, request.getStatType()));
        } catch (Exception e) {
            throw new DBException("device analysis stat query error", e);
        }
    }

}
