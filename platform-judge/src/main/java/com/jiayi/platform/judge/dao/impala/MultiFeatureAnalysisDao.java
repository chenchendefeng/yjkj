package com.jiayi.platform.judge.dao.impala;

import com.jiayi.platform.judge.dto.MultiFeatureAnalysisDto;
import com.jiayi.platform.judge.dto.MultiFeatureDetailDto;
import com.jiayi.platform.judge.query.MultiFeatureAnalysisQuery;
import com.jiayi.platform.judge.query.MultiFeatureDetailQuery;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MultiFeatureAnalysisDao {

    /**
     * 从数据库中查询一页数据
     */
    List<MultiFeatureAnalysisDto> selectMultiFeatureAnalysis(MultiFeatureAnalysisQuery query);

    /**
     * 计算数据库中总数量
     */
    Long countMultiFeatureAnalysis(MultiFeatureAnalysisQuery query);

    /**
     * 缓存满足条件的结果
     */
    int insertMultiFeatureResult(MultiFeatureAnalysisQuery query);

    /**
     * 从缓存中分页查询
     */
    List<MultiFeatureAnalysisDto> selectMultiFeatureResult(@Param("uid") Long queryId, @Param("limit") Integer limit, @Param("offset") Long offset);

    /**
     * 计算缓存的大小
     */
    Long countMultiFeatureResult(@Param("uid") Long queryId);


    /**
     * 多特征分析匹配情况：
     *
     * 从数据库中查询一页数据
     */
    List<MultiFeatureDetailDto> selectMultiFeatureDetail(MultiFeatureDetailQuery query);

    /**
     * 多特征分析匹配情况：
     *
     * 计算数据库中总数量
     */
    Long countMultiFeatureDetail(MultiFeatureDetailQuery query);
}
