package com.jiayi.platform.common.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;
import redis.clients.util.JedisByteHashMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    @Autowired
    protected RedisTemplate<String, Object> redisTemplate;

    /**
     *
     * @param key
     * @param value
     * @param timeout 超时时间设置，单位为秒
     *
     */
    public void put(String key, Object value, long timeout) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        ops.set(key, value, timeout, TimeUnit.SECONDS);
    }

    public void put(String key, Object value) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        ops.set(key, value);
    }

//    public void batchPut(Map<String, DeviceStatusInfoDto> data,long timeout) {
//        data.forEach((key,b)->{
//            put(key,b,timeout);
//        });
//    }
//
//    public void batchPut(Map<String, DeviceStatusInfoDto> map) {
//        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
//        ops.multiSet(map);
//    }

    public <T> T get(String key) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        return (T) ops.get(key);
    }

    public <T> List<T> batchGet(Collection<String> keys) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        return (List<T>) ops.multiGet(keys);
    }

    public void putHash(String key, Map<String, Object> map) {
        HashOperations<String, String, Object> ops = redisTemplate.opsForHash();
        ops.putAll(key, map);
    }

    public void setHashItem(String key, String field, Object value) {
        HashOperations<String, String, Object> ops = redisTemplate.opsForHash();
        ops.put(key, field, value);
    }

    public void deleteHashItem(String key, String... fields) {
        HashOperations<String, String, Object> ops = redisTemplate.opsForHash();
        ops.delete(key, fields);
    }

    public boolean existHashItem(String key, String field) {
        HashOperations<String, String, Object> ops = redisTemplate.opsForHash();
        return ops.hasKey(key, field);
    }

    public <T> T getHashItem(String key, String field) {
        HashOperations<String, String, Object> ops = redisTemplate.opsForHash();
        return (T) ops.get(key, field);
    }

    public <T> Map<String, T> getHash(String key) {
        HashOperations<String, String, Object> ops = redisTemplate.opsForHash();
        return (Map<String, T>) ops.entries(key);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    public void delete(Collection<String> keys) {
        redisTemplate.delete(keys);
    }

    public boolean exist(String key) {
        return redisTemplate.hasKey(key);
    }

    public Set<String> getKeys(String pattern) {
        return this.redisTemplate.keys(pattern);
    }

    public List<Map<String, String>> pipelineMultiGet(List<String> keys){
        List<Map<String, String>> dataList = Lists.newArrayList();
        StringRedisSerializer kSerializer = ((StringRedisSerializer)redisTemplate.getKeySerializer());
        Jackson2JsonRedisSerializer vSerializer = (Jackson2JsonRedisSerializer)redisTemplate.getHashValueSerializer();
        final List<byte[]> keyList = Lists.newArrayList();
        keys.forEach(a->{
            byte[] rawKey = kSerializer.serialize(a);
            keyList.add(rawKey);
        });

        RedisCallback<List<Object>> pipelineCallback = new RedisCallback<List<Object>>() {
            public List<Object> doInRedis(RedisConnection connection) throws DataAccessException {
                connection.openPipeline();
                keyList.forEach(a->{
                    connection.hGetAll(a);
                });
                return connection.closePipeline();
            }

        };
        List<Object> results = redisTemplate.execute(pipelineCallback);
        for(Object item : results){
            Map<String,String> dataMap = Maps.newHashMap();
            JedisByteHashMap map = (JedisByteHashMap)item;
            map.forEach((key,value)->{
                String keyStr = kSerializer.deserialize(key);
                String valueStr = (String)vSerializer.deserialize(value);
                dataMap.put(keyStr,valueStr);
            });
            dataList.add(dataMap);
        }
        return dataList;
    }
}
