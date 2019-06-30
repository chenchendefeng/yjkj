package com.jiayi.platform.basic.config;

import com.jiayi.platform.common.exception.ExceptionInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author : weichengke
 * @date : 2019-03-07 14:16
 */
//@ControllerAdvice
public class ServiceExceptionHandle {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseEntity<?> handle(HttpServletRequest request, Exception e) {
        ExceptionInfo exceptionInfo = new ExceptionInfo();
        exceptionInfo.setException(e.getClass().getName());
        exceptionInfo.setMessage(e.getMessage());
        return new ResponseEntity<>(exceptionInfo, getStatus(request));
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.valueOf(statusCode);
    }
}
