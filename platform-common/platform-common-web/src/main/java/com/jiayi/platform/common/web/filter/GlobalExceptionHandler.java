package com.jiayi.platform.common.web.filter;

import com.jiayi.platform.common.enums.ErrorEnum;
import com.jiayi.platform.common.exception.*;
import com.jiayi.platform.common.web.dto.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    protected static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Value("${spring.servlet.multipart.max-file-size:1Mb}")
    private String fileMaxSize;

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public JsonObject<?> handleException(HttpServletRequest request, IllegalArgumentException exception) throws Exception {
        logException(request, exception, HttpStatus.BAD_REQUEST.value());
        return new JsonObject<>(null, ErrorEnum.ARGUMENT_ERROR.code(), "检测到非法参数");
    }

    // 参数绑定异常处理
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public JsonObject handleException(HttpServletRequest request, BindException exception) throws Exception {
        List<FieldError> fieldErrors = exception.getFieldErrors();
        String errorMessage = "参数绑定异常,";
        for (FieldError fieldError : fieldErrors) {
            errorMessage += fieldError.getDefaultMessage() + "!";
        }
        logException(request, exception, HttpStatus.BAD_REQUEST.value());
        return new JsonObject<>(null, ErrorEnum.ARGUMENT_ERROR.code(), errorMessage);
    }

    // @Valid异常处理
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public JsonObject<?> handleException(HttpServletRequest request, MethodArgumentNotValidException exception)
            throws Exception {
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        String errorMessage = "参数校验失败";
        if (fieldErrors != null && fieldErrors.size() > 0) {
            errorMessage = fieldErrors.get(0).getDefaultMessage();
        }
        logException(request, exception, HttpStatus.BAD_REQUEST.value());
        return new JsonObject<>(null, ErrorEnum.ARGUMENT_ERROR.code(), errorMessage);//"参数校验失败"
    }

    // @RequestParam异常处理
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public JsonObject<?> handleException(HttpServletRequest request, MissingServletRequestParameterException exception)
            throws Exception {
        logException(request, exception, HttpStatus.BAD_REQUEST.value());
        return new JsonObject<>(null, ErrorEnum.ARGUMENT_ERROR.code(), "缺少参数：" + exception.getParameterName());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public JsonObject<?> handleException(HttpServletRequest request, MaxUploadSizeExceededException exception) throws Exception {
    	logException(request, exception, HttpStatus.BAD_REQUEST.value());
        return new JsonObject<>(null, ErrorEnum.ARGUMENT_ERROR.code(), "文件大小不能大于" + fileMaxSize);
    }

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public JsonObject<?> handleException(HttpServletRequest request, Exception exception) throws Exception {
		if (exception.getClass().getName().equals("org.apache.shiro.authz.UnauthorizedException")) {
			return new JsonObject<>(null, ErrorEnum.AUTH_ERROR.code(), "您无权限操作该数据！");
		}
		logException(request, exception, HttpStatus.INTERNAL_SERVER_ERROR.value());
		return new JsonObject<>(null, ErrorEnum.SERVER_ERROR.code(),ErrorEnum.SERVER_ERROR.message());
	}

//	@ExceptionHandler({UnauthenticatedException.class, TokenExpiredException.class})
//	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//	public JsonObject<?> handleException(HttpServletRequest request, UnauthenticatedException exception) throws Exception {
//		logException(request, exception, HttpStatus.INTERNAL_SERVER_ERROR.value());
//        if (exception.getClass().getName().equals("com.auth0.jwt.exceptions.TokenExpiredException")) {
//            return new JsonObject<>(null, ErrorEnum.TOKEN_ERROR.code(), ErrorEnum.TOKEN_ERROR.message());
//        }
//		return new JsonObject<>(null, ErrorEnum.UNLOGIN_ERROR.code(), ErrorEnum.UNLOGIN_ERROR.message());
//	}

    @ExceptionHandler({AuthException.class, ArgumentException.class, PermissionException.class, DBException.class, ServiceException.class})
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public JsonObject handleException(HttpServletRequest request, ServiceException exception) throws IOException {
        logException(request, exception, HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new JsonObject<>(null, exception.getCode(), exception.getMessage());
    }

    @ExceptionHandler(ValidException.class)
    @ResponseStatus(HttpStatus.OK)//校验数据未通过提示信息，返回状态码200
    public JsonObject<?> handleException(HttpServletRequest request, ValidException exception) throws Exception {
        logException(request, exception, HttpStatus.OK.value());
        return new JsonObject<>(null, exception.getCode(), exception.getMessage());
    }

    private void logException(HttpServletRequest request, Throwable throwable, int errorCode) throws IOException {
        throwable.printStackTrace();
        log.error("拦截错误日志，如下：",throwable);
        // HttpRequestInfo httpRequestInfo = new
        // HttpRequestInfo(request.getServletPath(), request.getParameterMap(),
        // request.getReader().lines().reduce((s1, s2) -> s1 + s2).orElse(""));
        // LOG.error("errorCode: {0}, request: {1}", throwable,errorCode + "",
        // new ObjectMapper().writeValueAsString(httpRequestInfo));
    }

}
