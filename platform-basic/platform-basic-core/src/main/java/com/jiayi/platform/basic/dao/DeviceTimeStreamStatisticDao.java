package com.jiayi.platform.basic.dao;

import com.jiayi.platform.basic.dto.DeviceStatusInfoDto;
import com.jiayi.platform.basic.entity.DeviceTimeStreamStatistic;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface DeviceTimeStreamStatisticDao extends PagingAndSortingRepository<DeviceTimeStreamStatistic, Long>, CrudRepository<DeviceTimeStreamStatistic, Long>, JpaSpecificationExecutor<DeviceTimeStreamStatistic> {
    @Query("select d.dataEndTime from DeviceTimeStreamStatistic d where d.src=:srcCode and d.code=:deviceCode")
    Long selectEndTime(@Param("srcCode") String srcCode, @Param("deviceCode") String deviceCode);

    @Query("select d from DeviceTimeStreamStatistic d where d.src=:srcCode and d.code=:deviceCode")
    DeviceTimeStreamStatistic selectDeviceStreamStatistic(@Param("srcCode") String srcCode, @Param("deviceCode") String deviceCode);

    @Query("select d from DeviceTimeStreamStatistic d where concat(d.src,'|',d.code) in(:srcAndCodes)")
    List<DeviceTimeStreamStatistic> findDeviceStatusInfo(@Param("srcAndCodes") Set<String> srcAndCodes);

    @Query("select new com.jiayi.platform.basic.dto.DeviceStatusInfoDto(d.id, CASE WHEN s.heartbeatTime/1000<now()-:onLineTime THEN 1 ELSE 0 END, CASE WHEN s.dataEndTime/1000<now()-:activeTime THEN 1 ELSE 0 END, dts.qualify, s.dataStartTime,\n" +
            "s.dataEndTime, dts.threshold, dts.average) from DeviceTimeStreamStatistic s " +
            " left join Device d on d.src=s.src and d.code=s.code" +
            " left join DeviceTimeStatistic dts on dts.src=s.src and dts.code=s.code")
    List<DeviceStatusInfoDto> findAllDeviceStatus(@Param("onLineTime") Integer onLineTime, @Param("activeTime") Integer activeTime);

}
