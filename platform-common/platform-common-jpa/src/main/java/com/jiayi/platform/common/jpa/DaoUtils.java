package com.jiayi.platform.common.jpa;


import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : weichengke
 * @date : 2019-04-09 16:28
 */
@Repository
public class DaoUtils {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public int execute(String hql, Map<String, Object> params) {

        return createQuery(hql, params).executeUpdate();
    }

    @Transactional
    public int executeNative(String sql, Map<String, Object> params) {
        return createNativeQuery(sql, params).executeUpdate();
    }

    @Transactional
    public int updateNativeById(String tableName, Integer id, Map<String, Object> params) {
        return updateNative(tableName, params, ImmutableMap.of("id", id));
    }

    @Transactional
    public int updateNative(String tableName, Map<String, Object> params, Map<String, Object> where) {
        StringBuffer sql = new StringBuffer("update ")
                .append(tableName)
                .append(" set ");
        params.forEach((k, v) -> {
            sql.append(k + "=:" + k + ",");
        });
        sql.setLength(sql.length() - 1);//移除最后一个逗号
        sql.append(" where ");
        where.forEach((k, v) -> {
            sql.append(k + "=:" + k + ",");
        });
        sql.setLength(sql.length() - 1);//移除最后一个逗号
        Map<String, Object> allParams = new HashMap<>();
        allParams.putAll(params);
        allParams.putAll(where);
        return createNativeQuery(sql.toString(), allParams).executeUpdate();
    }

    private Query createQuery(String hql, Map<String, Object> params) {
        Query query = em.createQuery(hql);
        if (params != null) {
            params.forEach((key, value) -> {
                query.setParameter(key, value);
            });
        }
        return query;
    }

    private Query createNativeQuery(String hql, Map<String, Object> params) {
        Query query = em.createNativeQuery(hql);
        if (params != null) {
            params.forEach((key, value) -> {
                query.setParameter(key, value);
            });
        }
        return query;
    }
}
