package com.jiayi.platform.security.core.dao;

import com.jiayi.platform.security.core.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoleDao extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {

	@Query("select count(1) from UserRole ur where ur.role.id=:roleId")
	int isUsedInUser(@Param("roleId") Long roleId);

	@Query("select count(1) from Role r where r.name=:name")
	int isNameUserd(@Param("name") String name);
}
