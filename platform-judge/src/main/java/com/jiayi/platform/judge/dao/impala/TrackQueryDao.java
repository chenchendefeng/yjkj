package com.jiayi.platform.judge.dao.impala;

import com.jiayi.platform.judge.dto.TrackQueryDetailDto;
import com.jiayi.platform.judge.dto.TrackQueryDto;
import com.jiayi.platform.judge.query.TrackDetailQuery;
import com.jiayi.platform.judge.query.TrackQueryQuery;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrackQueryDao {

    /**
     * 从数据库中查询一页数据
     */
    List<TrackQueryDto> selectTrackQuery(TrackQueryQuery query);

    /**
     * 计算数据库中总数量
     */
    Long countTrackQuery(TrackQueryQuery query);

    /**
     * 缓存满足条件的结果
     */
    int insertTrackQueryResult(TrackQueryQuery query);

    /**
     * 从缓存中分页查询
     */
    List<TrackQueryDto> selectTrackQueryResult(@Param("uid") Long queryId, @Param("limit") Integer limit, @Param("offset") Long offset);

    /**
     * 计算缓存的大小
     */
    Long countTrackQueryResult(@Param("uid") Long queryId);


    /**
     * 轨迹查询详细轨迹：
     *
     * 从数据库中查询一页数据
     */
    List<TrackQueryDetailDto> selectTrackQueryDetail(TrackDetailQuery query);

    /**
     * 轨迹查询详细轨迹：
     *
     * 计算数据库中总数量
     */
    Long countTrackQueryDetail(TrackDetailQuery query);
}
