package com.jiayi.platform.judge.dao.impala;

import com.jiayi.platform.judge.dto.MovementAnalysisDto;
import com.jiayi.platform.judge.query.MovementAnalysisQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovementAnalysisDao {
    /**
     * 从数据库中查询一页数据
     */
    List<MovementAnalysisDto> selectMovementAnalysis(MovementAnalysisQuery query);

    /**
     * 计算数据库中总数量
     */
    Long countMovementAnalysis(MovementAnalysisQuery query);
}
