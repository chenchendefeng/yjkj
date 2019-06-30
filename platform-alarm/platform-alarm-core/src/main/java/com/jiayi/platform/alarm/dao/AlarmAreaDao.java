//package com.jiayi.platform.alarm.dao;
//
//import com.jiayi.platform.alarm.entity.AlarmAreaInfo;
//import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
//import org.springframework.data.jpa.repository.Modifying;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.CrudRepository;
//import org.springframework.data.repository.query.Param;
//
//import java.util.List;
//
//public interface AlarmAreaDao extends CrudRepository<AlarmAreaInfo, Long>, JpaSpecificationExecutor<AlarmAreaInfo> {
//    Iterable<AlarmAreaInfo> findByValid(boolean valid);
//
//    @Modifying
//    @Query("update AlarmAreaInfo set valid = :valid where id = :id")
//    void setValid(@Param("id") long id, @Param("valid") boolean valid);
//    @Query("select count(1) from AlarmAreaInfo a where a.parentId = :parentId and a.name = :name")
//    int isNameUsed(@Param("name") String name, @Param("parentId") Long parentId);
//
//    @Query("select v.parentId,count(v) from AlarmAreaInfo v  group by v.parentId")
//    List<Object[]> subAreaNum();
//}
