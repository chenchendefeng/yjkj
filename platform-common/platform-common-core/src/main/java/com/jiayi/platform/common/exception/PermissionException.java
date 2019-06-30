package com.jiayi.platform.common.exception;

import com.jiayi.platform.common.enums.ErrorEnum;

public class PermissionException extends ServiceException {

    public PermissionException() {
        super(ErrorEnum.PERMISSION_ERROR.code());
    }

    public PermissionException(String message) {
        super(ErrorEnum.PERMISSION_ERROR.code(), message);
    }

    public PermissionException(Throwable cause) {
        super(ErrorEnum.PERMISSION_ERROR.code(), cause);
    }

    public PermissionException(String message, Throwable cause) {
        super(ErrorEnum.PERMISSION_ERROR.code(), message, cause);
    }
}
