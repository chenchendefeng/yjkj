package com.jiayi.platform.security.core.dao;

import com.jiayi.platform.security.core.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRoleDao extends JpaRepository<UserRole, Long> {

	@Query("select u from UserRole u where u.user.id=:userId")
	List<UserRole> findByUserId(@Param("userId") Long userId);
	
	void deleteByUserId(Long userId);

}
