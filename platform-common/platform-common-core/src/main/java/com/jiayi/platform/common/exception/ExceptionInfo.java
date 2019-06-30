package com.jiayi.platform.common.exception;

import lombok.Data;

@Data
public class ExceptionInfo{

   private Long timestamp;

   private Integer status;

   private String exception;

   private String message;

   private String path;

   private String error;

}