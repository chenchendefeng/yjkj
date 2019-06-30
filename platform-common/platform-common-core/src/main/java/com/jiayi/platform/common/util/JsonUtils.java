package com.jiayi.platform.common.util;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

/**
 * @author : weichengke
 * @date : 2019-04-18 17:31
 */
public class JsonUtils {

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static <T> T parse(String str, Class<T> clazz) throws IOException {
        return objectMapper.readValue(str, clazz);
    }

    public static <T> List<T> parseArray(String str, Class<T> clazz) {
        return JSON.parseArray(str, clazz);
    }

    public static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
