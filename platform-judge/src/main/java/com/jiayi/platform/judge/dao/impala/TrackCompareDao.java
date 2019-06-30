package com.jiayi.platform.judge.dao.impala;

import com.jiayi.platform.judge.dto.TrackCompareAllInfo;
import com.jiayi.platform.judge.dto.TrackCompareDto;
import com.jiayi.platform.judge.dto.TrackCompareInfo;
import com.jiayi.platform.judge.query.TrackCompareQuery;
import com.jiayi.platform.judge.query.TrackCompareSingleQuery;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrackCompareDao {

    List<TrackCompareInfo> selectTrackCompareTracks(TrackCompareQuery query);

    Long countTrackCompareAll(TrackCompareSingleQuery query);

    TrackCompareAllInfo selectTrackCompareTime(TrackCompareSingleQuery query);

    int insertTrackCompareResult(@Param("uid") Long queryId, @Param("resultList") List<TrackCompareDto> resultList);

    List<TrackCompareDto> selectTrackCompareResult(@Param("uid") Long queryId, @Param("limit") Integer limit, @Param("offset") Long offset);

    Long countTrackCompareResult(@Param("uid") Long queryId);
}
