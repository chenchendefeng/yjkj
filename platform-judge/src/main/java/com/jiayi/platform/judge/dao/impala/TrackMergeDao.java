package com.jiayi.platform.judge.dao.impala;

import com.jiayi.platform.judge.dto.TrackMergeDto;
import com.jiayi.platform.judge.query.TrackQueryQuery;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrackMergeDao {

    /**
     * 从数据库中查询一页数据
     */
    List<TrackMergeDto> selectTrackMerge(TrackQueryQuery query);

    /**
     * 计算数据库中总数量
     */
    Long countTrackMerge(TrackQueryQuery query);

    /**
     * 缓存满足条件的结果
     */
    int insertTrackMergeResult(TrackQueryQuery query);

    /**
     * 从缓存中分页查询
     */
    List<TrackMergeDto> selectTrackMergeResult(@Param("uid") Long queryId, @Param("limit") Integer limit, @Param("offset") Long offset);

    /**
     * 计算缓存的大小
     */
    Long countTrackMergeResult(@Param("uid") Long queryId);
}
