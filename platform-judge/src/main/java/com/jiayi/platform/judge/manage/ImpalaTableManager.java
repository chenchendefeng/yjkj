package com.jiayi.platform.judge.manage;

import com.jiayi.platform.common.exception.ArgumentException;
import com.jiayi.platform.common.util.RedisUtil;
import com.jiayi.platform.judge.dao.impala.MybatisUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author : weichengke
 * @date : 2019-04-20 15:46
 */
@Component
public class ImpalaTableManager {

    @Autowired
    private MybatisUtil mybatisUtil;
    @Autowired
    private RedisUtil redisUtil;

    public static final int SPLIT_HOURS = 1;
    public static final int QUERY_SPLIT_HOURS = 5 * 24;
    private static String IMPALA_DB_NAME = "platform_v2"; // todo config

    public Map<String, Pair<Long, Long>> getValidTableList(Map<String, Pair<Long, Long>> tableList) {
        Map<String, Pair<Long, Long>> validTableList = new HashMap<>();
        Set<String> tableNames = mybatisUtil.showTables();
        for (Map.Entry<String, Pair<Long, Long>> tableName : tableList.entrySet()) {
            if (tableNames.contains(tableName.getKey())) {
                validTableList.put(tableName.getKey(), tableName.getValue());
            }
        }
        if (validTableList.size() == 0) {
            throw new ArgumentException("no valid table, invalid object type or request dates");
        }
        return validTableList;
    }

    public Map<String, Pair<Long, Long>> getValidTableList(String queryType, long startTime, long endTime) {
        Map<String, Pair<Long, Long>> tableList = new HashMap<>();
        if (queryType.equals("collision"))
            tableList = getCollisionTrackTableNames(startTime, endTime);
        else if (queryType.equals("query"))
            tableList = getQueryTrackTableNames(startTime, endTime);;
        return getValidTableList(tableList);
    }

    public Map<String, Pair<Long, Long>> getValidTableList(String queryType, long startTime, long endTime, int secondOffset) {
        return getValidTableList(queryType, startTime - secondOffset * 1000, endTime + secondOffset * 1000);
    }

    /**
     * 获取碰撞类impala表名及对应起止查询时间
     * 碰撞类：路线碰撞、区域碰撞、对象出现、对象消失、伴随碰撞、多轨碰撞、区域分析
     */
    private Map<String, Pair<Long, Long>> getCollisionTrackTableNames(long startTime, long endTime) {
        Map<String, Pair<Long, Long>> tableList = new HashMap<>();
        Integer mergeDay = redisUtil.get("merge:" + IMPALA_DB_NAME + ":collision");
        if (mergeDay == null)
            mergeDay = 0;
        long mergeTime = mergeDay * (24 * 60 * 60 * 1000L);

        long recentBeginDate = mergeTime;
        if (startTime > mergeTime)
            recentBeginDate = startTime;
        long endDate = mergeTime - 1;
        if (endTime < mergeTime)
            endDate = endTime;

        if (endTime >= mergeTime)
            tableList.put("collision_recent", Pair.of(recentBeginDate, endTime));
        if (startTime < mergeTime)
            tableList.put("collision", Pair.of(startTime, endDate));

        return tableList;
    }

    /**
     * 获取查询类impala表名及对应起止查询时间
     * 查询类：轨迹查询、轨迹合并、轨迹比对，及所有查询的详细轨迹
     */
    private Map<String, Pair<Long, Long>> getQueryTrackTableNames(long startTime, long endTime) {
        Map<String, Pair<Long, Long>> tableList = new HashMap<>();
        Integer mergeDay = redisUtil.get("merge:" + IMPALA_DB_NAME + ":query");
        if (mergeDay == null)
            mergeDay = 0;
        long mergeTime = mergeDay * (5 * 24 * 60 * 60 * 1000L);

        long recentBeginDate = mergeTime;
        if (startTime > mergeTime)
            recentBeginDate = startTime;
        long endDate = mergeTime - 1;
        if (endTime < mergeTime)
            endDate = endTime;

        if (endTime >= mergeTime)
            tableList.put("query_recent", Pair.of(recentBeginDate, endTime));
        if (startTime < mergeTime)
            tableList.put("query", Pair.of(startTime, endDate));

        return tableList;
    }

//    public Set<String> getTableNames(){
//        return mybatisUtil.showTables();
//    }
}
