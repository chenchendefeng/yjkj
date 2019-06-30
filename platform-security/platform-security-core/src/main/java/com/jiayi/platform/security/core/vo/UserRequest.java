package com.jiayi.platform.security.core.vo;

import com.jiayi.platform.security.core.entity.User;
import com.jiayi.platform.security.core.util.Md5Util;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

public class UserRequest {

	@NotBlank(message = "警号不能为空")
	private String username;
	@NotBlank(message = "密码不能为空")
	private String password;
	@NotNull(message = "部门不能为空")
	private Integer departmentId;
	private String duties;
	@NotBlank(message = "姓名不能为空")
	private String nickname;
	private String sex;
	private Date birthday;
	private String portraitUrl;
	private String mobile;
	private String email;
	private List<Long> roleIds;
	private Integer beActive;
	private Integer regionCode;
	private String option;
	private String metaInfo;
	private String remark;

	public User toEntity() {
		User user = new User();
		user.setUsername(username);
		user.setPassword(Md5Util.getEncryptedPwd(password));
		user.setDuties(duties);
		user.setNickname(nickname);
		user.setSex(sex);
		user.setBirthday(birthday);
		user.setPortraitUrl(portraitUrl);
		user.setMobile(mobile);
		user.setEmail(email);
		user.setBeActive(beActive);
		user.setRegionCode(regionCode);
		user.setOption(option);
		user.setMetaInfo(metaInfo);
		user.setRemark(remark);
		Date date = new Date();
		user.setCreateAt(date);
		user.setUpdateAt(date);
		return user;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Integer departmentId) {
		this.departmentId = departmentId;
	}

	public String getDuties() {
		return duties;
	}

	public void setDuties(String duties) {
		this.duties = duties;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<Long> getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(List<Long> roleIds) {
		this.roleIds = roleIds;
	}

	public Integer getBeActive() {
		return beActive;
	}

	public void setBeActive(Integer beActive) {
		this.beActive = beActive;
	}

	public Integer getRegionCode() {
		return regionCode;
	}

	public void setRegionCode(Integer regionCode) {
		this.regionCode = regionCode;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String getPortraitUrl() {
		return portraitUrl;
	}

	public void setPortraitUrl(String portraitUrl) {
		this.portraitUrl = portraitUrl;
	}

	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}

	public String getMetaInfo() {
		return metaInfo;
	}

	public void setMetaInfo(String metaInfo) {
		this.metaInfo = metaInfo;
	}
}
