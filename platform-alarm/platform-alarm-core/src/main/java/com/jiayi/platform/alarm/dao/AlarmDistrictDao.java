package com.jiayi.platform.alarm.dao;

import com.jiayi.platform.alarm.entity.AlarmDistrict;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface AlarmDistrictDao extends CrudRepository<AlarmDistrict, Long>, JpaSpecificationExecutor<AlarmDistrict> {

    @Query("select a from AlarmDistrict a where a.startTime <=:date and a.endTime >=:date")
    List<AlarmDistrict> findValid(@Param("date") Date date);
}
