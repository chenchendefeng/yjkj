package com.jiayi.platform.basic.dao;


import com.jiayi.platform.basic.entity.DeviceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface DeviceTypeDao extends CrudRepository<DeviceType, Integer>, JpaRepository<DeviceType, Integer> {

    @Query("select count(1) from DeviceType t where t.name = :name")
    int isNameUsed(@Param("name") String name);

    @Query("select count(1) from DeviceType t where t.type = :type")
    int isTypeUsed(@Param("type") int type);

    @Query("select count(1) from Device d left join DeviceSubType st on st.id=d.type left join DeviceType t on t.type=st.deviceType where t.id=:id")
    int isUsedInDevice(@Param("id") int id);
}
