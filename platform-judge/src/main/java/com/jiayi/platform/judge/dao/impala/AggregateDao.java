package com.jiayi.platform.judge.dao.impala;

import com.jiayi.platform.judge.dto.AggregateDto;
import com.jiayi.platform.judge.query.AggregateQuery;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AggregateDao {
    /**
     * 从数据库中查询一页数据
     */
    List<AggregateDto> selectAggregate(AggregateQuery query);

    /**
     * 计算数据库中总数量
     */
    Long countAggregate(AggregateQuery query);

    /**
     * 缓存满足条件的结果
     */
    int insertAggregateResult(AggregateQuery query);

    /**
     * 从缓存中分页查询
     */
    List<AggregateDto> selectAggregateResult(@Param("uid") Long queryId, @Param("limit") Integer limit, @Param("offset") Long offset);

    /**
     * 计算缓存的大小
     */
    Long countAggregateResult(@Param("uid") Long queryId);
}
