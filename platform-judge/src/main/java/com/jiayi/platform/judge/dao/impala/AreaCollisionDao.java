package com.jiayi.platform.judge.dao.impala;

import com.jiayi.platform.judge.dto.AreaCollisionDto;
import com.jiayi.platform.judge.dto.AreaConditionDto;
import com.jiayi.platform.judge.query.AreaCollisionQuery;
import com.jiayi.platform.judge.query.AreaConditionQuery;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AreaCollisionDao {

    /**
     * 从数据库中查询一页数据
     */
    List<AreaCollisionDto> selectAreaCollision(AreaCollisionQuery query);

    /**
     * 计算数据库中总数量
     */
    Long countAreaCollision(AreaCollisionQuery query);

    /**
     * 缓存满足条件的结果
     */
    int insertAreaResult(AreaCollisionQuery query);

    /**
     * 从缓存中分页查询
     */
    List<AreaCollisionDto> selectAreaResult(@Param("uid") Long queryId, @Param("limit") Integer limit, @Param("offset") Long offset);

    /**
     * 计算缓存的大小
     */
    Long countAreaResult(@Param("uid") Long queryId);


    /**
     * 区域碰撞匹配结果：
     *
     * 从数据库中查询一页数据
     */
    List<AreaConditionDto> selectAreaCondition(AreaConditionQuery query);

    /**
     * 区域碰撞匹配结果：
     *
     * 计算数据库中总数量
     */
    Long countAreaCondition(AreaConditionQuery query);
}
