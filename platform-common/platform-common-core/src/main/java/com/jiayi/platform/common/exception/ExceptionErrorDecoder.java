package com.jiayi.platform.common.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Configuration;

/**
 * @author : weichengke
 * @date : 2019-03-07 13:53
 */
@Configuration
public class ExceptionErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            if (response.body() != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                ExceptionInfo exceptionInfo = objectMapper.readValue(Util.toString(response.body().asReader()), ExceptionInfo.class);// = JSON.parseObject(Util.toString(response.body().asReader()), new TypeReference<ExceptionInfo>() { });
                Class clazz = Class.forName(exceptionInfo.getException());
                return (Exception) clazz.getDeclaredConstructor(String.class).newInstance(exceptionInfo.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return FeignException.errorStatus(methodKey, response);
    }
}
