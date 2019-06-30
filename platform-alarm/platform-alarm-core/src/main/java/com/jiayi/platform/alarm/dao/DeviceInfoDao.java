package com.jiayi.platform.alarm.dao;

import com.jiayi.platform.alarm.entity.DeviceInfo;
import org.springframework.data.repository.CrudRepository;

public interface DeviceInfoDao extends CrudRepository<DeviceInfo, Long> {

//	@Query("select d from DeviceInfo d")
//    Page<DeviceInfo> findAllResult(Pageable pageable);
//
//	@Query("select d from DeviceInfo d where d.suspects.id =:suspectId")
//	List<DeviceInfo> findResultBySusId(@Param("suspectId") long suspectId);
//
//	@Query("select d from DeviceInfo d where d.suspects.id =:suspectId and d.type=:type and d.code=:code")
//	DeviceInfo findDeviceInfo(@Param("suspectId") long suspectId, @Param("type") int type, @Param("code") String code);
//
//	@Query("select d from DeviceInfo d where d.suspects.id =:suspectId and d.type=:type and d.code=:code and d.id!=:id")
//	DeviceInfo isHaveDevice(@Param("suspectId") long suspectId, @Param("type") int type, @Param("code") String code, @Param("id") long id);
}
