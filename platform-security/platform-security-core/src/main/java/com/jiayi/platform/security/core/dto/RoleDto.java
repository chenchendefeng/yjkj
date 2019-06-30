package com.jiayi.platform.security.core.dto;


import com.jiayi.platform.security.core.entity.Role;

import java.util.Date;
import java.util.List;

public class RoleDto {
    private Long id;
    private String name;
    private String description;
    private String isValid;
    private Date createDate;
    private Date updateDate;

    private List<Integer> resourceIds;

    public RoleDto() {

    }

    public RoleDto(Role role) {
        this.id = role.getId();
        this.name = role.getName();
        this.description = role.getDescription();
        this.isValid = role.getIsValid();
        this.createDate = role.getCreateDate();
        this.updateDate = role.getUpdateDate();
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIsValid() {
        return isValid;
    }

    public void setIsValid(String isValid) {
        this.isValid = isValid;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public List<Integer> getResourceIds() {
        return resourceIds;
    }

    public void setResourceIds(List<Integer> resourceIds) {
        this.resourceIds = resourceIds;
    }
}
