package com.jiayi.platform.judge.dao.impala;

import com.jiayi.platform.judge.dto.MultiTrackCollisionDto;
import com.jiayi.platform.judge.dto.MultiTrackDetailDto;
import com.jiayi.platform.judge.query.MultiTrackCollisionQuery;
import com.jiayi.platform.judge.query.MultiTrackDetailQuery;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MultiTrackCollisionDao {
    /**
     * 从数据库中查询一页数据
     */
    List<MultiTrackCollisionDto> selectMultiTrack(MultiTrackCollisionQuery query);

    /**
     * 计算数据库中总数量
     */
    Long countMultiTrack(MultiTrackCollisionQuery query);

    /**
     * 缓存满足条件的结果
     */
    int insertMultiTrackResult(MultiTrackCollisionQuery query);

    /**
     * 从缓存中分页查询
     */
    List<MultiTrackCollisionDto> selectMultiTrackResult(@Param("uid") Long queryId, @Param("limit") Integer limit, @Param("offset") Long offset);

    /**
     * 计算缓存的大小
     */
    Long countMultiTrackResult(@Param("uid") Long queryId);


    /**
     * 多轨碰撞比对结果：
     *
     * 从数据库中查询一页数据
     */
    List<MultiTrackDetailDto> selectMultiTrackDetail(MultiTrackDetailQuery query);

    /**
     * 多轨碰撞比对结果：
     *
     * 计算数据库中总数量
     */
    Long countMultiTrackDetail(MultiTrackDetailQuery query);
}
