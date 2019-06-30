package com.jiayi.platform.basic.dao;

import com.jiayi.platform.basic.entity.Src;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface SrcDao extends CrudRepository<Src, Long> {

    @Query("select s from Src s")
    Page<Src> findAllResult(Pageable pageable);

//    @Query("select count(1) from Place d where d.src.id=:srcId")
//    int isSrcUsedInPlace(@Param("srcId") Long srcId);

    @Query("select count(1) from Device d where d.src=:srcCode")
    int isSrcUsedInDevice(@Param("srcCode") String srcCode);

    @Query("select count(1) from Src s where s.name=:name")
    int isNameUsed(@Param("name") String name);

    @Query("select count(1) from Src s where s.code=:code")
    int isCodeUsed(@Param("code") String code);

    @Query("select s from Src s where s.code=:code")
    Src findByCode(String code);

    @Query("select s from Src s where s.dataType=:dataType order by s.id desc")
    List<Src> findByDataType(@Param("dataType") Integer dataType);

    @Query(value = "select s.code,s.name from t_src s where s.code in :codes", nativeQuery = true)
    List<Object[]> findSrcs(@Param("codes") List<String> codes);

    @Query("select s from Src s where s.name=:name")
    Src findByName(@Param("name") String name);
}
