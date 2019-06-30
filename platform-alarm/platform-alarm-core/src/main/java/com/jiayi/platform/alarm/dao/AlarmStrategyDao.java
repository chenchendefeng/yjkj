package com.jiayi.platform.alarm.dao;

import com.jiayi.platform.alarm.entity.AlarmStrategy;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AlarmStrategyDao extends CrudRepository<AlarmStrategy, Long>,JpaSpecificationExecutor<AlarmStrategy> {

//	@Query("select a from AlarmStrategy a where a.caseId=:caseId")
//	Page<AlarmStrategy> findAllResult(@Param("caseId")Integer caseId, Pageable pageable);

//	@Query("select a from AlarmStrategy a where a.id in (:strategyId)")
//	List<AlarmStrategy> find(@Param("strategyId") Long[] strategyId);
//
//	@Query("select a from AlarmStrategy a where a.caseId=:caseId and a.id not in (:strategyId)")
//	List<AlarmStrategy> findNotIn(@Param("caseId") Integer caseId, @Param("strategyId") Long[] strategyId);
//
//	@Query("select a from AlarmStrategy a where a.caseId=:caseId")
//	List<AlarmStrategy> findAllResult(@Param("caseId") Integer caseId);

	@Query("select a from AlarmStrategy a where a.caseId=:caseId and a.type=:type and a.beActive=0")
	List<AlarmStrategy> findByCaseIdAndType(@Param("caseId") Integer caseId, @Param("type") Integer type);

//	@Query("select a from AlarmStrategy a where a.caseId=:caseId and a.type=:type and a.beActive=0")
//	List<AlarmStrategy> findActive(@Param("caseId") Integer caseId, @Param("type") Integer type);

//	@Query("select a from AlarmStrategy a  left join SuspectsStrategy s on a.id=s.alarmStrategyId where s.suspectId=:suspectId and a.beActive=0")
//	List<AlarmStrategy> findActiveBySuspectId(@Param("suspectId") Long suspect);

	@Query("select count(1) from AlarmStrategy a where a.caseId=:caseId and a.type=:type and a.beActive=0 and a.status=1")
	int countStatusOpen(@Param("caseId") Integer caseId, @Param("type") Integer type);

	@Query("select count(1) from AlarmStrategy a where a.caseId=:caseId and a.type=:type and a.beActive=0 and a.status=1 and a.id!=:id")
	int countOtherActive(@Param("caseId") Integer caseId, @Param("type") Integer type, @Param("id") Long id);

	@Query("select a from AlarmStrategy a where a.caseId=:caseId and a.beActive=:status")
	List<AlarmStrategy> findByCaseIdAndActive(@Param("caseId") Integer caseId, @Param("status") Integer status);

	@Query("select a from AlarmStrategy a where a.id=:id")
	AlarmStrategy findStatusOpenById(@Param("id") long id);

}
