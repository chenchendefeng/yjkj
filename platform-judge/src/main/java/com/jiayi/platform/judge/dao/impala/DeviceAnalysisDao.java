package com.jiayi.platform.judge.dao.impala;

import com.jiayi.platform.judge.dto.DeviceAnalysisDetailDto;
import com.jiayi.platform.judge.dto.DeviceAnalysisDto;
import com.jiayi.platform.judge.dto.DeviceAnalysisStatDto;
import com.jiayi.platform.judge.query.DeviceAnalysisDetailQuery;
import com.jiayi.platform.judge.query.DeviceAnalysisQuery;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceAnalysisDao {

    /**
     * 从数据库中查询一页数据
     */
    List<DeviceAnalysisDto> selectDeviceAnalysis(DeviceAnalysisQuery query);

    /**
     * 计算数据库中总数量
     */
    Long countDeviceAnalysis(DeviceAnalysisQuery query);

    /**
     * 缓存满足条件的结果
     */
    int insertDeviceAnalysisResult(DeviceAnalysisQuery query);

    /**
     * 从缓存中分页查询
     */
    List<DeviceAnalysisDto> selectDeviceAnalysisResult(@Param("uid") Long queryId, @Param("limit") Integer limit, @Param("offset") Long offset);

    /**
     * 计算缓存的大小
     */
    Long countDeviceAnalysisResult(@Param("uid") Long queryId);


    /**
     * 区域分析详细轨迹：
     *
     * 从数据库中查询一页数据
     */
    List<DeviceAnalysisDetailDto> selectDeviceAnalysisDetail(DeviceAnalysisDetailQuery query);

    /**
     * 区域分析详细轨迹：
     *
     * 计算数据库中总数量
     */
    Long countDeviceAnalysisDetail(DeviceAnalysisDetailQuery query);


    /**
     * 区域分析图表统计：
     *
     * 从数据库中查询一页数据
     */
    List<DeviceAnalysisStatDto> selectDeviceAnalysisStat(DeviceAnalysisDetailQuery query);
}
