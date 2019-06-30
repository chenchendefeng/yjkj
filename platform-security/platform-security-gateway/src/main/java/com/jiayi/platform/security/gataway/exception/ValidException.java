package com.jiayi.platform.security.gataway.exception;


import com.jiayi.platform.security.core.enums.ErrorEnum;

@SuppressWarnings("unused")
public class ValidException extends ServiceException {

	public ValidException(String message) {
		super(ErrorEnum.VALID_ERROR.getCode(), message);
	}
	
}
