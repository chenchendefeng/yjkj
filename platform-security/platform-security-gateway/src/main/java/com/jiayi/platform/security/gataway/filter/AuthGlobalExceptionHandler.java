package com.jiayi.platform.security.gataway.filter;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.jiayi.platform.security.core.dto.JsonObject;
import com.jiayi.platform.security.core.enums.ErrorEnum;
import com.jiayi.platform.security.gataway.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class AuthGlobalExceptionHandler {

    @ExceptionHandler({ServiceException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public JsonObject<?> serviceException(ServiceException ex) {
        return new JsonObject<>(null, ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler({TokenExpiredException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public JsonObject<?> handleException(TokenExpiredException ex) {
        log.warn(ex.getMessage());
        return new JsonObject<>(null, ErrorEnum.TOKEN_ERROR.getCode(), ErrorEnum.TOKEN_ERROR.getMessage());
    }
}
