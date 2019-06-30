package com.jiayi.platform.judge.executor;

import com.jiayi.platform.judge.common.bean.PageRequest;
import com.jiayi.platform.judge.common.bean.PageResult;
import com.jiayi.platform.judge.request.JudgeRequest;


import java.util.List;

/**
 * 研判逻辑的封装，只做数据的查询，不做业务流程的控制(业务流程在JudgeService统一管理)。
 *
 * @author : weichengke
 * @date : 2019-04-20 10:38
 */
public interface JudgeExecutor {

    /**
     * 从数据库中查询并返回当前页数据
     */
    <T extends JudgeRequest> List<?> query(T request, PageRequest pageRequest);

    /**
     * 从数据库查询记录总数
     */
    <T extends JudgeRequest> long count(T request);

    /**
     * 从缓存中查询并返回当前页数据
     */
    List<?> queryCache(Long queryHistoryId, PageRequest pageRequest);

    /**
     * 从缓存中查询记录总数
     */
    long countCache(Long queryHistoryId);

    /**
     * 缓存所有的数据
     */
    <T extends JudgeRequest> long cache(T request, long queryHistoryId);

    /**
     * 将数据库数据转换为前端展示需要的数据
     */
    <T extends JudgeRequest> PageResult convert2Response(List<?> dots, long count, T request, PageRequest pageRequest);

    /**
     * 获取导出结果数据
     */
    <T extends JudgeRequest> int exportResult(List<String> contents, T request, long offset, long queryId);
}