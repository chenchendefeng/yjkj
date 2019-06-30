package com.jiayi.platform.security.core.vo;

import com.jiayi.platform.security.core.entity.Department;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

public class DepartmentRequest {

	@NotNull(message = "请选择上级部门，无上级部门pid设置为0")
	private Integer pid;
	@NotBlank(message = "名称不能为空")
	private String name;
	public Department toEntity() {
		Department department = new Department();
		department.setName(name);
		department.setPid(pid);
		Date date = new Date();
		department.setCreateAt(date);
		department.setUpdateAt(date);
		return department;
	}
	public Integer getPid() {
		return pid;
	}
	public void setPid(Integer pid) {
		this.pid = pid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
