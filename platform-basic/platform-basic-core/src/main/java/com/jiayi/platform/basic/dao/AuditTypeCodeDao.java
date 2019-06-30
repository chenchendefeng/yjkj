package com.jiayi.platform.basic.dao;

import com.jiayi.platform.basic.entity.AuditTypeCode;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AuditTypeCodeDao extends PagingAndSortingRepository<AuditTypeCode, Long> {

    @Query("select a from AuditTypeCode a where a.collectType=:type")
    List<AuditTypeCode> findByType(@Param("type") String type);
}
