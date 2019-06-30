package com.jiayi.platform.security.gataway.core.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class UserTokenParam {

    @NotBlank(message = "刷新token不能为空")
    private String token;
    @NotBlank(message = "用户名ID不能为空")
    private String id;

}
