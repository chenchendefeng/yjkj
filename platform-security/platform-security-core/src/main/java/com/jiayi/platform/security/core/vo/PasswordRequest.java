package com.jiayi.platform.security.core.vo;

import javax.validation.constraints.NotBlank;

public class PasswordRequest {

//	@NotNull(message = "请选择需要修改的用户")
//	@Min(value = 1, message = "用户不正确")
//	private Long id;
	@NotBlank(message = "旧密码不能为空")
	private String oldPassword;
	@NotBlank(message = "新密码不能为空")
	private String newPassword;
	
	public String getOldPassword() {
		return oldPassword;
	}
	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	
}
