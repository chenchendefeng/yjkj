package com.jiayi.platform.judge.executor;

import com.jiayi.platform.judge.common.bean.PageRequest;
import com.jiayi.platform.judge.request.JudgeDetailRequest;

import java.util.List;

public interface JudgeDetailExecutor {
    /**
     * 从数据库中查询并返回当前页数据
     */
    <T> List<?> query(T request, PageRequest pageRequest);

    /**
     * 从数据库查询记录总数
     */
    <T> long count(T request);

    /**
     * 将数据库数据转换为前端展示需要的数据
     */
    List<?> convert2Response(List<?> dots, String objectType);

    /**
     * 获取导出结果数据
     */
    <T extends JudgeDetailRequest> int exportResult(List<String> contents, T request, long offset);
}
