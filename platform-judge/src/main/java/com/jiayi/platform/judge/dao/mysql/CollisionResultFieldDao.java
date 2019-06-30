package com.jiayi.platform.judge.dao.mysql;

import com.jiayi.platform.judge.entity.mysql.CollisionResultField;
import com.jiayi.platform.judge.response.QueryResultFieldInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface CollisionResultFieldDao extends CrudRepository<CollisionResultField, Long> {

    @Query("select crf.fieldName from CollisionResultField crf where crf.id=:id")
    String getResultFieldNameById(@Param("id") long id);

    List<CollisionResultField> findByRequestTmplId(@Param("requestTmplId") long requestTmplId);

    @Query("from CollisionResultField crf where crf.requestTmplId = :tmplId and field_desc not in ('开始地点', '结束地点', '出现地点')")
    List<CollisionResultField> getResultFieldInfoWithoutAddressByTmplId(@Param("tmplId") long tmplId);

    @Query("select crf.fieldDesc from CollisionResultField crf where crf.requestTmplId = :tmplId")
    List<String> getResultFieldDescByTmplId(@Param("tmplId") long tmplId);

    @Query("select new com.jiayi.platform.judge.response.QueryResultFieldInfo(crf.fieldName, crf.fieldDesc, crf.fieldType) " +
            "from CollisionResultField crf where crf.requestTmplId = :tmplId")
    List<QueryResultFieldInfo> getResultFieldInfoByTmplId(@Param("tmplId") long tmplId);
}
