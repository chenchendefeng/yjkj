//package com.jiayi.platform.alarm.config;
//
//import com.fasterxml.jackson.annotation.JsonAutoDetect;
//import com.fasterxml.jackson.annotation.PropertyAccessor;
//import com.fasterxml.jackson.databind.DeserializationFeature;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
//import org.springframework.data.redis.serializer.RedisSerializer;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
//
///**
// * Created by wuwf on 17/4/24.
// */
//@Configuration
//public class RedisCacheConfig {
//	@Bean
//	public RedisTemplate<String, ?> getRedisTemplate(JedisConnectionFactory jedisConnectionFactory) {
//		RedisTemplate<String, ?> redisTemplate = new RedisTemplate<>();
//		redisTemplate.setConnectionFactory(jedisConnectionFactory);
//		// key序列化方式,但是如果方法上有Long等非String类型的话，会报类型转换错误
//		RedisSerializer<String> redisSerializer = new StringRedisSerializer();// Long类型不可以会出现异常信息;
//		redisTemplate.setKeySerializer(redisSerializer);
//		redisTemplate.setHashKeySerializer(redisSerializer);
//		Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
//		ObjectMapper om = new ObjectMapper();
//		om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//		om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//		om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
//		jackson2JsonRedisSerializer.setObjectMapper(om);
//
//		redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
//		redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
//		redisTemplate.afterPropertiesSet();
//
//		return redisTemplate;
//	}
//
//}
