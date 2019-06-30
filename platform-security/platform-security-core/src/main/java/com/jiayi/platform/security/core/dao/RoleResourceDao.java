package com.jiayi.platform.security.core.dao;

import com.jiayi.platform.security.core.entity.RoleResource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleResourceDao extends JpaRepository<RoleResource, Long> {
    void deleteByRoleId(Long roleId);
}
