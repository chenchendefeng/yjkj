package com.jiayi.platform.security.core.dao;

import com.jiayi.platform.security.core.entity.User;
import com.jiayi.platform.security.core.dto.CaseDir;
import com.jiayi.platform.security.core.entity.UserBean;
import com.jiayi.platform.security.core.entity.part.UserPwd;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserDao extends CrudRepository<User, Long>, JpaSpecificationExecutor<User> {



	@Query("select u.departmentId.id from User u where u.id=:userId")
	Integer findDeptsByUserId(@Param("userId") Long userId);
	@Query("select count(1) from User u where u.username=:username")
	int isUserExist(@Param("username") String username);
	@Query("select new com.jiayi.platform.security.core.dto.CaseDir(u.id,u.username,u.nickname,u.departmentId.id,u.departmentId.pid,u.departmentId.name) from User u where u.departmentId.id in(:departIds) and u.beValid=1")
    Page<CaseDir> getDepartmentStaffTree(@Param("departIds") List<Integer> departIds, Pageable pageable);
	@Modifying
	@Query("update User u set u.remark=:remark where u.id=:id")
	void updateRemark(@Param("remark") String remark, @Param("id") long id);

	@Query("select u from User u where u.username=:username and u.beValid=1")
	User findUserByName(@Param("username") String username);

	@Query("select up from UserPwd up where up.username=:username and up.beValid=1")
	UserPwd findPwdByName(@Param("username") String username);

	/**
	 * 更新token
	 *
	 * @param token 待更新token
	 * @param id    用户ID
	 */
	@Modifying
	@Query("update User u set u.token=:token where u.id=:id")
	void updateToken(@Param("token") String token, @Param("id") long id);

	@Query("select new UserBean(u.id, u.username, u.nickname) from User u where u.id in (:userIds)")
	List<UserBean> findUserObjByIds(@Param("userIds") List<Long> userIds);

	@Query("select new UserBean(u.id, u.username, u.nickname) from User u where u.id=:userId")
	UserBean findUserById(@Param("userId") long userId);
}
