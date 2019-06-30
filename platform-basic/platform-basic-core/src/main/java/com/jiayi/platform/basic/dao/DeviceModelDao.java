package com.jiayi.platform.basic.dao;

import com.jiayi.platform.basic.entity.DeviceModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceModelDao extends CrudRepository<DeviceModel, Integer>, JpaRepository<DeviceModel, Integer> {
    @Query("select m from DeviceModel m where m.name=:name")
    List<DeviceModel> findByName(@Param("name") String name);
}
