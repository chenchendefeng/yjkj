package com.jiayi.platform.judge.dao.impala;

import com.jiayi.platform.judge.dto.AppearCollisionDto;
import com.jiayi.platform.judge.query.AppearCollisionQuery;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppearCollisionDao {
    /**
     * 从数据库中查询一页数据
     */
    List<AppearCollisionDto> selectAppear(AppearCollisionQuery query);

    /**
     * 计算数据库中总数量
     */
    Long countAppear(AppearCollisionQuery query);

    /**
     * 缓存满足条件的结果
     */
    int insertAppearResult(AppearCollisionQuery query);

    /**
     * 从缓存中分页查询
     */
    List<AppearCollisionDto> selectAppearResult(@Param("uid") Long queryId, @Param("limit") Integer limit, @Param("offset") Long offset);

    /**
     * 计算缓存的大小
     */
    Long countAppearResult(@Param("uid") Long queryId);
}
