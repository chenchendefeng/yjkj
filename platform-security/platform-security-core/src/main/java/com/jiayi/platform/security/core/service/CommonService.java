package com.jiayi.platform.security.core.service;

import com.google.common.collect.Lists;
import com.jiayi.platform.security.core.entity.Department;
import com.jiayi.platform.security.core.dao.DepartmentDao;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CommonService {

	@Autowired
	private DepartmentDao departmentDao;
	
	/**
	 * 获取部门及底下所有子部门id
	 * @param departmentId
	 * @return
	 */
	public List<Integer> getDeptIdsByPid(Integer departmentId) {
		List<Integer> deptsId = new ArrayList<>();
		if (departmentId != null)
			deptsId.add(departmentId);
		if (deptsId.size() > 0) {
			this.getDeptIdsByPid(deptsId, deptsId);
		}
		return deptsId;
	}
	
	/**
	 * 查询部门底下所有子部门id
	 * @param pId 用于查询的父部门id
	 * @param deptsId 保存查询到的所有部门id
	 * @return
	 */
	private void getDeptIdsByPid(List<Integer> pId, List<Integer> deptsId) {
		List<Integer> list = departmentDao.findDeptsByPid(pId);
		if (CollectionUtils.isNotEmpty(list)) {
			deptsId.addAll(list);
			getDeptIdsByPid(list, deptsId);
		}
	}

	/**
	 * 获取部门底下及所有子部门对象
	 * @param departId 用于查询的父部门id
	 */
	public List<Department> getDepartmentByPid(Integer departId) {
		Optional<Department> deptOpt = departmentDao.findById(departId);
		List<Department> departments = new ArrayList<>();
		deptOpt.ifPresent(departments::add);
		this.getDepartmentByPid(Lists.newArrayList(departId), departments);
		return departments;
	}
	
	/**
     * 查询部门底下所有子部门对象
     * @param departIds 用于查询的父部门id
     * @param departments 保存查询到的所有部门
     * @return
     */
	private void getDepartmentByPid(List<Integer> departIds, List<Department> departments){
    	List<Department> child = departmentDao.getDeptsByPid(departIds);
    	if(CollectionUtils.isNotEmpty(child)) {
    		departments.addAll(child);
    		getDepartmentByPid(child.stream().map(Department::getId).collect(Collectors.toList()), departments);
    	}
    }
}
