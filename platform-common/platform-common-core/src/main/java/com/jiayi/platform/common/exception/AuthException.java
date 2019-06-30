package com.jiayi.platform.common.exception;

import com.jiayi.platform.common.enums.ErrorEnum;

public class AuthException extends ServiceException {

    public AuthException() {
        super(ErrorEnum.AUTH_ERROR.code());
    }

    public AuthException(String message) {
        super(ErrorEnum.AUTH_ERROR.code(), message);
    }

    public AuthException(int unlogin) {
        super(ErrorEnum.UNLOGIN_ERROR.code(), ErrorEnum.UNLOGIN_ERROR.message());
    }

    public AuthException(Throwable cause) {
        super(ErrorEnum.ARGUMENT_ERROR.code(), cause);
    }

    public AuthException(String message, Throwable cause) {
        super(ErrorEnum.ARGUMENT_ERROR.code(), message, cause);
    }
}
