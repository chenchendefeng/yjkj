package com.jiayi.platform.judge.dao.impala;

import com.jiayi.platform.judge.dto.FollowCollisionDto;
import com.jiayi.platform.judge.dto.FollowTrackDetailDto;
import com.jiayi.platform.judge.query.FollowCollisionQuery;
import com.jiayi.platform.judge.query.FollowTrackDetailQuery;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowCollisionDao {
    /**
     * 从数据库中查询一页数据
     */
    List<FollowCollisionDto> selectFollow(FollowCollisionQuery query);

    /**
     * 计算数据库中总数量
     */
    Long countFollow(FollowCollisionQuery query);

    /**
     * 缓存满足条件的结果
     */
    int insertFollowResult(FollowCollisionQuery query);

    /**
     * 从缓存中分页查询
     */
    List<FollowCollisionDto> selectFollowResult(@Param("uid") Long queryId, @Param("limit") Integer limit, @Param("offset") Long offset);

    /**
     * 计算缓存的大小
     */
    Long countFollowResult(@Param("uid") Long queryId);


    /**
     * 伴随比对结果：
     *
     * 从数据库中查询一页数据
     */
    List<FollowTrackDetailDto> selectFollowTrackDetail(FollowTrackDetailQuery query);

    /**
     * 伴随比对结果：
     *
     * 计算数据库中总数量
     */
    Long countFollowTrackDetail(FollowTrackDetailQuery query);
}
