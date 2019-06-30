package com.jiayi.platform.judge.dao.impala;

import com.jiayi.platform.judge.dto.LocationAnalysisDto;
import com.jiayi.platform.judge.query.LocationAnalysisQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationAnalysisDao {
    /**
     * 从数据库中查询一页数据
     */
    List<LocationAnalysisDto> selectLocationAnalysis(LocationAnalysisQuery query);

    /**
     * 计算数据库中总数量
     */
    Long countLocationAnalysis(LocationAnalysisQuery query);

    /**
     * 查询地点分析全局数据信息
     */
    List<LocationAnalysisDto> selectGlobalLocationAnalysis(LocationAnalysisQuery query);
}
