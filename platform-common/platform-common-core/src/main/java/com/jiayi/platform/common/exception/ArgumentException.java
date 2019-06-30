package com.jiayi.platform.common.exception;

import com.jiayi.platform.common.enums.ErrorEnum;

public class ArgumentException extends ServiceException {

    public ArgumentException() {
        super(ErrorEnum.ARGUMENT_ERROR.code());
    }

    public ArgumentException(String message) {
        super(ErrorEnum.ARGUMENT_ERROR.code(), message);
    }

    public ArgumentException(Throwable cause) {
        super(ErrorEnum.ARGUMENT_ERROR.code(), cause);
    }

    public ArgumentException(String message, Throwable cause) {
        super(ErrorEnum.ARGUMENT_ERROR.code(), message, cause);
    }
}
