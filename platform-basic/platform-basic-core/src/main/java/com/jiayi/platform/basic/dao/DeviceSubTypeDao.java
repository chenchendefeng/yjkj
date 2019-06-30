package com.jiayi.platform.basic.dao;

import com.jiayi.platform.basic.dto.DeviceTypeInfo;
import com.jiayi.platform.basic.entity.DeviceSubType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DeviceSubTypeDao extends CrudRepository<DeviceSubType, Integer>, JpaRepository<DeviceSubType, Integer>{

	@Query("select count(1) from DeviceSubType d where d.name=:name")
	int isNameUsed(@Param("name") String name);

	@Query("select count(1) from Device d where d.type=:id")
	int isUsedInDevice(@Param("id") Integer id);

//	DeviceSubType findByCode(String code);

	@Query("select d.dataType from DeviceSubType d where d.id=:id")
	String findCollectById(@Param("id") Integer id);

	@Query("select d from DeviceSubType d where d.name=:name")
	DeviceSubType findByName(@Param("name") String name);

//	@Query("select new com.jiayi.platform.basic.dto.DeviceSubTypeDto(st.id,st.name,st.dataType,st.description,st.createDate,st.updateDate,t.type,t.name) from DeviceSubType st left join DeviceType t on t.type = st.deviceType")
//	List<DeviceSubTypeDto> findAllWithParentType();

	@Query("select new com.jiayi.platform.basic.dto.DeviceTypeInfo(sub.id, sub.name, main.name) " +
			"from DeviceSubType sub left join DeviceType main on sub.deviceType=main.type")
	List<DeviceTypeInfo> findAllSubNameAndMainName();
}
