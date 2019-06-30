package com.jiayi.platform.security.core.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jiayi.platform.common.exception.DBException;
import com.jiayi.platform.security.core.entity.Department;
import com.jiayi.platform.security.core.dao.DepartmentDao;
import com.jiayi.platform.security.core.dao.UserDao;
import com.jiayi.platform.security.core.dto.CaseDir;
import com.jiayi.platform.security.core.dto.DepartDir;
import com.jiayi.platform.security.core.vo.DataTypeEnum;
import com.jiayi.platform.security.core.vo.DepartmentRequest;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class DepartmentService extends CommonService {

    @Autowired
    private DepartmentDao departmentDao;
    @Autowired
    private UserDao userDao;

    private static List<DepartDir> departDirs = null;
    private static long start = 0;

    //    public void findOne() {
//
//        List<Object[]> obj = departmentDao.getDepartmentSimpleTree(0);
//        System.out.println(obj);
//    }
    public static String[] getDepartmentByLeaf(int departmentId) {
        DepartDir departDir = null;
        for (int i = 0; i < departDirs.size(); i++) {
            departDir = departDirs.get(i).findDepartDirById(departmentId);
            if (departDir != null) {
                break;
            }
        }
        Integer[] ids = new Integer[0];
        String[] names = new String[0];
        if (departDir != null && departDir.getElders() != null) {
            List<DepartDir> parentDepartDir = departDir.getElders();
            Integer size = parentDepartDir.size() + 1;
            ids = new Integer[size];
            names = new String[size];
            ids[ids.length - 1] = departDir.getDepartmentId();
            names[names.length - 1] = departDir.getDepartmentName();
            int count = 0;
            for (int i = ids.length - 2; i >= 0; i--) {
                ids[count] = parentDepartDir.get(i).getDepartmentId();
                names[count] = parentDepartDir.get(i).getDepartmentName();
                count++;
            }
        }
        String[] deptTreeInfo = new String[ids.length];
        for (int i = 0; i < ids.length; i++) {
            deptTreeInfo[i] = ids[i] + "," + names[i];
        }
        return deptTreeInfo;
    }

    private List<DepartDir> delDepartmentTree(List<Department> departments) {
        Map<Integer, List<DepartDir>> departmentTreeMap = Maps.newLinkedHashMap();

        if (departments.isEmpty()) {
//        	throw new ArgumentException(ErrorEnum.ARGUMENT_ERROR.message());
            return new ArrayList<>();
        }
        for (Department department : departments) {
            Integer pid = department.getPid();
            Integer tmpDepartmentId = department.getId();
            DepartDir departDir = new DepartDir(tmpDepartmentId, pid, department.getName());
            if (departmentTreeMap.get(pid) == null) {
                List<DepartDir> data = Lists.newArrayList();
                data.add(departDir);
                departmentTreeMap.put(pid, data);
            } else {
                List<DepartDir> tmpDepartDirs = departmentTreeMap.get(pid);
                tmpDepartDirs.add(departDir);
            }
        }
        List<DepartDir> rootDepartDirs = departmentTreeMap.get(departments.get(0).getPid());
        for (Iterator<Integer> iterator = departmentTreeMap.keySet().iterator(); iterator.hasNext(); ) {
            Integer tmpPid = (Integer) iterator.next();
            if (tmpPid == 0) {
                // 根节点
                continue;
            }
            for (DepartDir rootDepartDir : rootDepartDirs) {
                if (tmpPid == rootDepartDir.getDepartmentId()) {// 第一级
                    List<DepartDir> departDirs = departmentTreeMap.get(tmpPid);
                    rootDepartDir.addDepartDirAll(departDirs);
                    departDirs.forEach(a -> {
                        a.setParentDepartDir(rootDepartDir);
                    });
                } else {
                    // 继续查找下面子节点
                    departmentTree(rootDepartDir.getSubDepartDir(), tmpPid, departmentTreeMap.get(tmpPid));
                }
            }
        }
        return rootDepartDirs;
    }

    private void departmentTree(List<DepartDir> departDirs, Integer pid, List<DepartDir> sourceCaseDirs) {
        for (DepartDir departDir : departDirs) {
            if (departDir.getDepartmentId().equals(pid)) {
                departDir.addDepartDirAll(sourceCaseDirs);
                sourceCaseDirs.forEach(a -> {
                    a.setParentDepartDir(departDir);
                });
                break;
            } else {
                departmentTree(departDir.getSubDepartDir(), pid, sourceCaseDirs);
            }
        }
    }

    @PostConstruct
    public void getDepartmentTree() {
        departDirs = this.delDepartmentTree(super.getDepartmentByPid(0));
    }

    public List<DepartDir> getDepartmentTree(Integer departId) {
        long end = System.currentTimeMillis();
        if (departId == 0 && departDirs != null && (end - start) / (1000 * 60) < 10) {
            start = end;
            return departDirs;
        }
        //10分钟重新加载一次
        start = end;
        return this.delDepartmentTree(super.getDepartmentByPid(departId));
    }

    public List<DepartDir> getDepartmentStaffTree(Integer departmentId) {
        // 设置树节点
        List<Department> departments = super.getDepartmentByPid(departmentId);
        List<DepartDir> rootDepartDirs = this.delDepartmentTree(departments);
        List<Integer> departIds = departments.stream().map(a -> a.getId()).collect(Collectors.toList());

        Sort sort = new Sort(new Sort.Order(Direction.DESC, "id"));
        Pageable pageable = new PageRequest(0, 10000, sort);
        Page<CaseDir> page = userDao.getDepartmentStaffTree(departIds, pageable);
        List<CaseDir> list = page.getContent();
        // 设置树叶子节点
        Map<Integer, List<DepartDir>> departmentLeafMap = Maps.newHashMap();
        for (CaseDir caseDir : list) {
//            Integer id = (Integer) objects[0];
//            Integer tmpDepartmentId = (Integer) objects[4];
//            Integer pid = (Integer) objects[5];
//            Object userNameObj = objects[1]==null?"":objects[1];
//            Object nickNameObj = objects[2]==null?"":objects[2];
//            Object roleName = objects[3]==null?"":objects[3];
//            Object departmentName = objects[6]==null?"":objects[6];
//            Object departRolePermissions = objects[7]==null?"":objects[7];
//            CaseDir caseDir = new CaseDir(id, userNameObj.toString(), nickNameObj.toString(), roleName.toString(),
//                    tmpDepartmentId, pid, departmentName.toString(), departRolePermissions.toString());
            if (departmentLeafMap.get(caseDir.getDepartmentId()) == null) {
                List<DepartDir> data = Lists.newArrayList();
                data.add(caseDir);
                departmentLeafMap.put(caseDir.getDepartmentId(), data);
            } else {
                List<DepartDir> tmpCaseDirs = departmentLeafMap.get(caseDir.getDepartmentId());
                tmpCaseDirs.add(caseDir);
            }
        }
        for (DepartDir rootDepartDir : rootDepartDirs) {
            for (Iterator<Integer> iterator = departmentLeafMap.keySet().iterator(); iterator.hasNext(); ) {
                Integer tmpPid = (Integer) iterator.next();
                departmentTree(rootDepartDir.getSubDepartDir(), tmpPid, departmentLeafMap.get(tmpPid));
            }
            if (!departmentLeafMap.isEmpty() && departmentLeafMap.get(rootDepartDir.getDepartmentId()) != null) {
                rootDepartDir.addDepartDirAll(departmentLeafMap.get(rootDepartDir.getDepartmentId()));//设置一级下面的员工
            }
        }
        return rootDepartDirs;
    }

    public Department add(Department department) {
        int count = departmentDao.isNameUsedInPid(department.getName(), department.getPid());
        if (count > 0)
            throw new DBException("部门名称重复");
        if (department.getPid() == 0) {
            count = departmentDao.isUsedInChild(0);
            if (count > 0) {
                throw new DBException("只能设置一个一级部门");
            }
        }
        department = departmentDao.save(department);
        //更新缓存
        getDepartmentTree();
        return department;
    }

    public Department modify(Integer id, DepartmentRequest request) {

        Department department = departmentDao.findById(id).get();
        if (StringUtils.isNotBlank(request.getName()) && !request.getName().equals(department.getName())) {
            int count = departmentDao.isNameUsedInPid(request.getName(), request.getPid());
            if (count > 0)
                throw new DBException("部门名称重复");
        }
        try {
            if (StringUtils.isNotBlank(request.getName()))
                department.setName(request.getName());
            department.setUpdateAt(new Date());
            department = departmentDao.save(department);
        } catch (Exception e) {
            throw new DBException("修改失败", e);
        }
        //更新缓存
        getDepartmentTree();
        return department;
    }

    public void delete(Integer id, boolean isHavePlace) {
        DataTypeEnum message = DataTypeEnum.PLACE;
        int count = 1;
//		if(count == 0) {
//			count = departmentDao.isUsedInDevice(id);
//			message = DataTypeEnum.DEVICE;
//		}
        if (!isHavePlace) {
            count = departmentDao.isUsedInUser(id);
            message = DataTypeEnum.USER;
        }
        if (count == 0) {
            count = departmentDao.isUsedInChild(id);
            message = DataTypeEnum.DEPARTMENT;
        }
        if (count > 0)
            throw new DBException("删除失败，部门已关联" + message.getDescription());
        try {
            departmentDao.deleteById(id);
            //更新缓存
            getDepartmentTree();
        } catch (Exception e) {
            throw new DBException("删除失败", e);
        }
    }
}
