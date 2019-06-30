package com.jiayi.platform.judge.dao.mysql;

import com.jiayi.platform.judge.entity.mysql.QueryHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Transactional
@Repository
public interface QueryHistoryDao extends CrudRepository<QueryHistory, Long> {
    
    @Modifying
    @Query("update QueryHistory set resultCount = :resultCount, updateDate = :updateDate where id = :id")
    void updateResultCount(@Param("id") Long id, @Param("resultCount") Long resultCount, @Param("updateDate") Date updateDate);

    @Modifying
    @Query("update QueryHistory set status = :status, updateDate = :updateDate where id = :id")
    void updateQueryStatus(@Param("id") Long id, @Param("status") Integer status, @Param("updateDate") Date updateDate);
    
    @Modifying
    @Query("update QueryHistory set resultCount = :resultCount, status = :status, updateDate = :updateDate where id = :id")
    void updateResultCountAndStatus(@Param("id") Long id, @Param("resultCount") Long resultCount,
                                    @Param("status") Integer status, @Param("updateDate") Date updateDate);
    
    @Query("select requestType from QueryHistory where id=:id")
    String getRequestTypeById(@Param("id") long id);
    
    @Query("select qh from QueryHistory qh left join RequestHistory rh on qh.id = rh.queryId where rh.id=:requestId")
    QueryHistory findQueryHistoryByRequestId(@Param("requestId") long requestId);

    QueryHistory findOneByMd5AndRequestTypeAndStatusIn(String md5, String requestType, List<Integer> status);

    @Query("select history from QueryHistory history where history.md5=:md5 and history.requestType=:requestType")
    Page<QueryHistory> findByMd5AndRequestType(@Param("md5") String md5, @Param("requestType") String requestType, Pageable pageable);
    
    List<QueryHistory> findByMd5AndRequestTypeAndStatusIn(String md5, String requestType, List<Integer> status);
    
    List<QueryHistory> findByStatus(Integer status);
}