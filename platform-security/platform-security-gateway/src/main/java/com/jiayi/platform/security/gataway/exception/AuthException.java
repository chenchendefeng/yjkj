package com.jiayi.platform.security.gataway.exception;


import com.jiayi.platform.security.core.enums.ErrorEnum;

public class AuthException extends ServiceException {

    public AuthException() {
        super(ErrorEnum.AUTH_ERROR.getCode());
    }

    public AuthException(String message) {
        super(ErrorEnum.AUTH_ERROR.getCode(), message);
    }

    public AuthException(int unlogin) {
        super(ErrorEnum.UNLOGIN_ERROR.getCode(), ErrorEnum.UNLOGIN_ERROR.getMessage());
    }

    public AuthException(Throwable cause) {
        super(ErrorEnum.ARGUMENT_ERROR.getCode(), cause);
    }

    public AuthException(String message, Throwable cause) {
        super(ErrorEnum.ARGUMENT_ERROR.getCode(), message, cause);
    }
}
