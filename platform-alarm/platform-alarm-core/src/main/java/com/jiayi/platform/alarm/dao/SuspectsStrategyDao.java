package com.jiayi.platform.alarm.dao;

import com.jiayi.platform.alarm.entity.SuspectsStrategy;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface SuspectsStrategyDao extends CrudRepository<SuspectsStrategy, Long> {

	@Query("select s from SuspectsStrategy s where s.alarmStrategyId=:strategyId and s.suspectId=:suspectId")
	SuspectsStrategy findStrategyBySuspectId(@Param("strategyId") long strategyId, @Param("suspectId") long suspectId);

//	@Query("select s from SuspectsStrategy s where s.suspectId=:suspectId")
//	List<SuspectsStrategy> findBinding(@Param("suspectId") long suspectId);

	@Query("select s.alarmStrategyId from SuspectsStrategy s where s.suspectId=:suspectId")
	List<Long> findStrategyId(@Param("suspectId") long suspectId);

	@Query("select s from SuspectsStrategy s where s.alarmStrategyId=:alarmStrategyId")
	List<SuspectsStrategy> findBindingSuspects(@Param("alarmStrategyId") long alarmStrategyId);

	@Query("select count(1) from SuspectsStrategy s where s.suspectId=:suspectId")
	int findBindingCount(@Param("suspectId") long suspectId);

	@Transactional
	void deleteBySuspectId(@Param("suspectId") long suspectId);

	@Query("select s.suspectId from SuspectsStrategy s where s.alarmStrategyId=:alarmStrategyId")
	List<Long> findSuspectIdByStrategyId(@Param("alarmStrategyId") long alarmStrategyId);

	@Query("select count(1) from SuspectsStrategy s where s.alarmStrategyId=:alarmStrategyId")
	int count(@Param("alarmStrategyId") long alarmStrategyId);

	@Query("select count(1) from SuspectsStrategy s left join AlarmStrategy a on s.alarmStrategyId=a.id where s.suspectId =:suspectId and a.caseId =:caseId and s.alarmStrategyId !=:alarmStrategyId and  a.beActive = 0")
	int countOthers(@Param("suspectId") long suspectId,
                    @Param("alarmStrategyId") long alarmStrategyId, @Param("caseId") Integer caseId);
}
