package com.jiayi.platform.basic.dao;

import com.jiayi.platform.basic.entity.DeviceTimeStatistic;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface DeviceTimeStatisticDao extends PagingAndSortingRepository<DeviceTimeStatistic, Long> {
    @Query("select d from DeviceTimeStatistic d where concat(d.src,'|',d.code) in(:srcAndCodes)")
    List<DeviceTimeStatistic> findDeviceStatisticInfo(@Param("srcAndCodes") Set<String> srcAndCodes);
}
