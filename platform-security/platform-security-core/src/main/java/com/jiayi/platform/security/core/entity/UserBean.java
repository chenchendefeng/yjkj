package com.jiayi.platform.security.core.entity;

import javax.persistence.*;

@Entity
@Table(name = "user")
public class UserBean {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String username;
	private String nickname;
	@Column(name = "portrait_url")
	private String portraitUrl;
	private String sex;
	
	@Transient
    private String realPath;
	
	public UserBean() {
		super();
	}
	public UserBean(Long id) {
		this.id = id;
	}
	public UserBean(Long id, String username, String nickname) {
		super();
		this.id = id;
		this.username = username;
		this.nickname = nickname;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getPortraitUrl() {
		return portraitUrl;
	}
	public void setPortraitUrl(String portraitUrl) {
		this.portraitUrl = portraitUrl;
	}
	public String getRealPath() {
		return realPath;
	}
	public void setRealPath(String realPath) {
		this.realPath = realPath;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}

@Override
public String toString() {
	return "UserBean{" +
			"id=" + id +
			", username='" + username + '\'' +
			", nickname='" + nickname + '\'' +
			", portraitUrl='" + portraitUrl + '\'' +
			", sex='" + sex + '\'' +
			", realPath='" + realPath + '\'' +
			'}';
}
}
