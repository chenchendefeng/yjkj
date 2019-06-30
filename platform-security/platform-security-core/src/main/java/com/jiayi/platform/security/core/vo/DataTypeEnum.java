package com.jiayi.platform.security.core.vo;

public enum DataTypeEnum {

	DATA_ACCESS("data_access","数据接入"),
	DATA_DISTRIBUTION("data_distribution","数据分发"),
	DATA_POINT("data_point","数据类型"),
	DEVICE("device","设备"),
	PLACE("place", "场所"),
	DEPARTMENT("department","下级部门"),
	USER("user","用户");
	
	private String name;
	private String description;
	private DataTypeEnum(String name, String description) {
		this.name = name;
		this.description = description;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
