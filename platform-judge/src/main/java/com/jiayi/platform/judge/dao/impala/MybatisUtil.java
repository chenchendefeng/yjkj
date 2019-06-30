package com.jiayi.platform.judge.dao.impala;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * TODO 移回platform-common中
 */
@Repository
public interface MybatisUtil {

    Set<String> showTables();

    void deleteHistoryResultById(@Param("tableName") String tableName, @Param("id") Long id);
}