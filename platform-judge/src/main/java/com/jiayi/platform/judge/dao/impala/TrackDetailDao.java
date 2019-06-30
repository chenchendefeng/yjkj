package com.jiayi.platform.judge.dao.impala;


import com.jiayi.platform.judge.dto.TrackDetailDto;
import com.jiayi.platform.judge.query.TrackDetailQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrackDetailDao {
    /**
     * 从数据库中查询一页数据
     */
    List<TrackDetailDto> selectTrackDetail(TrackDetailQuery query);

    /**
     * 计算数据库中总数量
     */
    Long countTrackDetail(TrackDetailQuery query);
}
