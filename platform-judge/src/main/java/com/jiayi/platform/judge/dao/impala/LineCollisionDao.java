package com.jiayi.platform.judge.dao.impala;

import com.jiayi.platform.judge.dto.LineCollisionDto;
import com.jiayi.platform.judge.query.LineCollisionQuery;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author : weichengke
 * @date : 2019-04-20 14:46
 */
@Repository
public interface LineCollisionDao {

    /**
     * 从数据库中查询一页数据
     */
    List<LineCollisionDto> selectLineCollision(LineCollisionQuery query);

    /**
     * 计算数据库中总数量
     */
    Long countLineCollision(LineCollisionQuery query);

    /**
     * 缓存满足条件的结果
     */
    int insertLineResult(LineCollisionQuery query);

    /**
     * 从缓存中分页查询
     */
    List<LineCollisionDto> selectLineResult(@Param("uid") Long queryId, @Param("limit") Integer limit, @Param("offset") Long offset);

    /**
     * 计算缓存的大小
     */
    Long countLineResult(@Param("uid") Long queryId);

}
