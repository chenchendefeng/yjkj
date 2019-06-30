package com.jiayi.platform.judge.dao.impala;

import com.jiayi.platform.judge.dto.RuleAnalysisDetailDto;
import com.jiayi.platform.judge.query.RuleAnalysisDetailQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RuleAnalysisDetailDao {
    /**
     * 从数据库中查询一页数据
     */
    List<RuleAnalysisDetailDto> selectRuleAnalysisDetail(RuleAnalysisDetailQuery query);

    /**
     * 计算数据库中总数量
     */
    Long countRuleAnalysisDetail(RuleAnalysisDetailQuery query);
}
