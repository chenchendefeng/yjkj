package com.jiayi.platform.security.gataway.exception;


import com.jiayi.platform.security.core.enums.ErrorEnum;

public class DBException extends ServiceException {

    public DBException() {
        super(ErrorEnum.DB_ERROR.getCode());
    }

    public DBException(String message) {
        super(ErrorEnum.DB_ERROR.getCode(), message);
    }

    public DBException(Throwable cause) {
        super(ErrorEnum.DB_ERROR.getCode(), cause);
    }

    public DBException(String message, Throwable cause) {
        super(ErrorEnum.DB_ERROR.getCode(), message, cause);
    }
}
