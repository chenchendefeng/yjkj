package com.jiayi.platform.judge.dao.mysql;

import com.jiayi.platform.judge.entity.mysql.RequestHistory;
import org.apache.commons.lang3.tuple.Pair;
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
public interface RequestHistoryDao extends CrudRepository<RequestHistory, Long> {
    
    @Modifying
    @Query("update RequestHistory set resultName = :resultName, updateDate = :updateDate where id = :id")
    void updateResultName(@Param("id") Long id, @Param("resultName") String resultName, @Param("updateDate") Date updateDate);

    @Modifying
    @Query("update RequestHistory set requestRemark = :remark, updateDate = :updateDate where id = :id")
    void updateRequestRemark(@Param("id") Long id, @Param("remark") String remark, @Param("updateDate") Date updateDate);
    
    @Modifying
    @Query("update RequestHistory set twoCollision=:twoCollision,resultName=:resultName,updateDate=:date where id=:id")
    void updateTwoCollision(@Param("twoCollision") Boolean twoCollision, @Param("resultName") String resultName,
                            @Param("id") Long id, @Param("date") Date date);

    @Modifying
    @Query("update RequestHistory set twoCollision=:twoCollision where id=:id")
    void updateTwoCollision(@Param("twoCollision") Boolean twoCollision, @Param("id") Long id);

    @Modifying
    @Query("update RequestHistory set valid=:valid where id=:id")
    void setValid(@Param("id") Long id, @Param("valid") Boolean valid);

    @Modifying
    @Query("update RequestHistory set requestDate = :requestDate, updateDate = :requestDate where id = :id")
    void updateRequestDate(@Param("id") Long id, @Param("requestDate") Date requestDate);

    @Query("select id from RequestHistory where caseId = :caseId and userId = :userId and queryId = :queryId and valid = 1")
    Long getExistRequestId(@Param("caseId") String caseId, @Param("userId") Long userId, @Param("queryId") Long queryId);

//    RequestHistory findOneByCaseIdAndUserIdAndQueryIdAndValid(@Param("caseId") String caseId, @Param("userId") Long userId,
//                                                              @Param("queryId") Long queryId, @Param("valid") Boolean valid);

//    RequestHistory findOneById(@Param("id") Long id);

    List<RequestHistory> findByCaseIdAndUserIdAndQueryIdAndValid(@Param("caseId") String caseId, @Param("userId") Long userId,
                                                                 @Param("queryId") Long queryId, @Param("valid") Boolean valid);

    @Query("select id from RequestHistory where caseId = :caseId and userId = :userId and queryId = :queryId and valid = 1")
    List<Long> getExistRequestIds(@Param("caseId") String caseId, @Param("userId") Long userId, @Param("queryId") Long queryId);

    @Query("select caseId from RequestHistory where id = :id")
    String getExistCaseId(@Param("id") Long id);

//    @Query(value = "select 1 from request_history where case_id = ?1 and result_name = ?2 and valid = 1 limit 1", nativeQuery = true)
//    Integer checkRequestResultName(String caseId, String resultName);

    Long countByCaseIdAndResultNameAndValid(@Param("caseId") String caseId, @Param("resultName") String resultName, @Param("valid") Boolean valid);

    @Query("select new org.apache.commons.lang3.tuple.ImmutablePair(qh.id, qh.requestType) from RequestHistory rh "
            + "left join QueryHistory qh on qh.id = rh.queryId where qh.status != :deteledStatus and qh.resultCount != 0 "
            + "group by qh.id having (max(rh.requestDate) < :largeCacheCleanDate and max(qh.resultCount) >= :cacheSize) "
            + "or max(rh.requestDate) < :cleanDate")
    List<Pair<Long, String>> getOutDatedQueryList(@Param("largeCacheCleanDate") Date largeCacheCleanDate,
                                                  @Param("cacheSize") Long cacheSize, @Param("cleanDate") Date cleanDate, @Param("deteledStatus") Integer deteledStatus);

    @Query("select count(*) from RequestHistory where caseId=:caseId and resultName=:resultName")
    int findResultName(@Param("caseId") String caseId, @Param("resultName") String resultName);
}
