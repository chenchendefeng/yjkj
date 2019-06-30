package com.jiayi.platform.security.core.dao;

import com.jiayi.platform.security.core.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DepartmentDao extends JpaRepository<Department, Integer> {


	@Query("select d.id from Department d where d.pid in(:deptIds)")
	List<Integer> findDeptsByPid(@Param("deptIds") List<Integer> deptIds);
	
	@Query("select d from Department d where d.pid in(:deptIds)")
	List<Department> getDeptsByPid(@Param("deptIds") List<Integer> deptIds);

//	@Query("select count(p) from com.jiayi.platform.security.core.vo.Place p where p.department.id=:deptIds")
//	int isUsedInPlace(@Param("deptIds") Integer deptIds);
	@Query("select count(u) from User u where u.departmentId.id=:deptIds and u.beValid=1")
	int isUsedInUser(@Param("deptIds") Integer deptIds);
	@Query("select count(d) from Department d where d.pid=:deptIds")
	int isUsedInChild(@Param("deptIds") Integer deptIds);
	@Query("select count(d) from Department d where d.name=:name and pid=:pid")
	int isNameUsedInPid(@Param("name") String name, @Param("pid") Integer pid);

	Department findByCode(String code);
}
