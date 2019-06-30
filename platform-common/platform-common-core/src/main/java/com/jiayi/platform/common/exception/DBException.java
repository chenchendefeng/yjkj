package com.jiayi.platform.common.exception;

import com.jiayi.platform.common.enums.ErrorEnum;

public class DBException extends ServiceException {

    public DBException() {
        super(ErrorEnum.DB_ERROR.code());
    }

    public DBException(String message) {
        super(ErrorEnum.DB_ERROR.code(), message);
    }

    public DBException(Throwable cause) {
        super(ErrorEnum.DB_ERROR.code(), cause);
    }

    public DBException(String message, Throwable cause) {
        super(ErrorEnum.DB_ERROR.code(), message, cause);
    }
}
