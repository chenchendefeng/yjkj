package com.jiayi.platform.security.gataway.exception;

import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {
    private int code;
    public ServiceException(int code, String message) {
        super(message);
        this.code = code;
    }

    public ServiceException() {
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }

    public ServiceException(int error, Throwable cause) {
        super(cause);
        this.code = error;
    }
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public ServiceException(int code) {
        super();
        this.code = code;
    }
}
