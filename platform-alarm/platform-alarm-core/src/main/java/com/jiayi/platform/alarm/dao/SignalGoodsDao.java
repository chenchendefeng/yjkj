package com.jiayi.platform.alarm.dao;

import com.jiayi.platform.alarm.entity.SignalGoods;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface SignalGoodsDao extends CrudRepository<SignalGoods, Long>, JpaSpecificationExecutor<SignalGoods> {

//	@Query("select s from SignalGoods s where s.caseId=:caseId")
//	Page<SignalGoods> findAllResult(@Param("caseId")String caseId, Pageable pageable);

	@Query("select count(1) from SignalGoods s where s.objType=:objType and s.objValue=:objValue and s.caseId=:caseId")
	int isGoodsExistInCase(@Param("objType") int objType, @Param("objValue") String objValue, @Param("caseId") Integer caseId);

//	@Query("select count(1) from SignalGoods s where s.objType=:objType and s.objValue=:objValue and s.caseId=:caseId and s.id!=:id")
//	int findGoods(@Param("objType") int objType, @Param("objValue") String objValue, @Param("caseId") Integer caseId, @Param("id") long id);

	@Query("select count(1) from SignalGoods s where s.name=:name and s.caseId=:caseId")
	int isNameExist(@Param("name") String name, @Param("caseId") Integer caseId);

}
