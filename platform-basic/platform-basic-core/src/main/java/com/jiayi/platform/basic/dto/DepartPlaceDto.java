package com.jiayi.platform.basic.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

public class DepartPlaceDto {

	private Long id;
	private String name;
	private String address;
	private String code;
	private Integer departmentId;
	private Long longitude;
    private Long latitude;
    
	public DepartPlaceDto() {
		super();
	}
	
	public DepartPlaceDto(Long id, String name, String address, String code, Integer departmentId, Long longitude, Long latitude) {
		super();
		this.id = id;
		this.name = name;
		this.address = address;
		this.code = code;
		this.departmentId = departmentId;
		this.longitude = longitude;
		this.latitude = latitude;
	}

	@JsonSerialize(using=ToStringSerializer.class)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Integer getDepartmentId() {
		return departmentId;
	}
	public void setDepartmentId(Integer departmentId) {
		this.departmentId = departmentId;
	}
	public Double getLongitude() {
        return longitude == null ? null : longitude / Math.pow(10, 12);
    }

    public void setLongitude(Long longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
    	return latitude == null ? null : latitude / Math.pow(10, 12);
    }

    public void setLatitude(Long latitude) {
        this.latitude = latitude;
    }
}
