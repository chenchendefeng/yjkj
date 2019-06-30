package com.jiayi.platform.common.exception;

import com.jiayi.platform.common.enums.ErrorEnum;

public class ValidException extends ServiceException{

	public ValidException() {
		super(ErrorEnum.VALID_ERROR.code());
	}
	
	public ValidException(String message) {
		super(ErrorEnum.VALID_ERROR.code(), message);
	}
	
	public ValidException(Throwable cause) {
		super(ErrorEnum.VALID_ERROR.code(), cause);
	}
	
	public ValidException(String message, Throwable cause) {
		super(ErrorEnum.VALID_ERROR.code(), message, cause);
	}
}
