package com.jiayi.platform.alarm.dao;

import com.jiayi.platform.alarm.entity.AlarmMsgBean;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface AlarmMsgDao extends CrudRepository<AlarmMsgBean, Long>, JpaSpecificationExecutor<AlarmMsgBean> {

//    @Modifying
//    @Query("update AlarmMsgBean sc set sc.status = 1,sc.updateAt=:updateAt where sc.id in(:ids)")
//    void updateAlarmMsg(@Param("ids") List<Long> ids, @Param(value = "updateAt") Date updateAt);

    @Modifying
    @Query("update AlarmMsgBean sc set sc.status = 1,sc.updateAt=:updateAt where sc.id=:id")
    void updateAlarmMsg(@Param("id") Long id, @Param(value = "updateAt") Date updateAt);

    @Modifying
    @Query("update AlarmMsgBean sc set sc.status = 1,sc.updateAt=:updateAt where sc.assembleGroupId=:assembleGroupId")
    void updateAlarmMsgByAssembleGroupId(@Param("assembleGroupId") String assembleGroupId, @Param(value = "updateAt") Date updateAt);
}
