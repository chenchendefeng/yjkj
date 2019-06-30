package com.jiayi.platform.security.core.dto;

public class CaseDir extends DepartDir{
    private Long userId;
    private String username;
    private String nickName;
    private String departRolePermissions;

    public CaseDir() {
    }
    public CaseDir(Long userId, String username,String nickName, Integer departmentId, Integer pid, String departmentName) {
        this.userId = userId;
        this.username = username;
        this.nickName=nickName;
        super.setDepartmentId(departmentId);
        super.setPid(pid);
        super.setDepartmentName(departmentName);
//        this.departRolePermissions = departRolePermissions;
    }
    

    public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getNickName() {
        return nickName;
    }
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getDepartRolePermissions() {
        return departRolePermissions;
    }
    public void setDepartRolePermissions(String departRolePermissions) {
        this.departRolePermissions = departRolePermissions;
    }

}
