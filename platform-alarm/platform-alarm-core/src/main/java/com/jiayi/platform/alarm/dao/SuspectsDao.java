package com.jiayi.platform.alarm.dao;

import com.jiayi.platform.alarm.entity.Suspects;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SuspectsDao extends CrudRepository<Suspects, Long>, JpaSpecificationExecutor<Suspects> {

	@Query("select s from Suspects s")
    Page<Suspects> findAllResult(Pageable pageable);

	@Modifying
	@Query("update Suspects s set s.status=:status where s.id=:id")
	void update(@Param("status") Integer status, @Param("id") Long id);

	@Query("select s from Suspects s where s.caseId=:caseId")
	List<Suspects> findAllResult(@Param("caseId") Integer caseId);

	@Query("select s from Suspects s where s.id in(:suspectIds)")
	List<Suspects> find(@Param("suspectIds") List<Long> suspectIds);

	@Query("select s from Suspects s where s.caseId=:caseId")
	List<Suspects> findResultByCaseId(@Param("caseId") Integer caseId);

	@Query("select count(1) from Suspects s where s.caseId=:caseId and s.name=:name")
	int isNameExistByCase(@Param("name") String name, @Param("caseId") Integer caseId);
}
