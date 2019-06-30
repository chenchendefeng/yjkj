package com.jiayi.platform.basic.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

public class DepartDir {
    private Integer departmentId;
    private Integer pid;
    private String departmentName;
    @JsonIgnore
    private DepartDir parentDepartDir;
    private List<DepartDir> subDepartDir = Lists.newArrayList();
    public DepartDir() {
    }
    /* 插入一个child节点到当前节点中 */
    public void addChildNode(DepartDir DepartDir) {
        subDepartDir.add(DepartDir);
    }

    public boolean isLeaf() {
        if (subDepartDir == null) {
            return true;
        } else {
            if (subDepartDir.isEmpty()) {
                return true;
            } else {
                return false;
            }
        }
    }

    /* 返回当前节点的父辈节点集合 */
    @JsonIgnore
    public List<DepartDir> getElders() {
        List<DepartDir> elderList = new ArrayList<DepartDir>();
        DepartDir parentNode = this.getParentDepartDir();
        if (parentNode == null) {
            return elderList;
        } else {
            elderList.add(parentNode);
            elderList.addAll(parentNode.getElders());
            return elderList;
        }
    }

    /* 返回当前节点的晚辈集合 */
    @JsonIgnore
    public List<DepartDir> getJuniors() {
        List<DepartDir> juniorList = new ArrayList<DepartDir>();
        List<DepartDir> subDepartDir = this.getSubDepartDir();
        if (subDepartDir == null) {
            return juniorList;
        } else {
            int childNumber = subDepartDir.size();
            for (int i = 0; i < childNumber; i++) {
                DepartDir junior = subDepartDir.get(i);
                juniorList.add(junior);
                juniorList.addAll(junior.getJuniors());
            }
            return juniorList;
        }
    }

    /* 找到一颗树中某个节点 */
    public DepartDir findDepartDirById(int id) {
        if (this.departmentId == id)
            return this;
        if (subDepartDir.isEmpty() || subDepartDir == null) {
            return null;
        } else {
            int childNumber = subDepartDir.size();
            for (int i = 0; i < childNumber; i++) {
                DepartDir child = subDepartDir.get(i);
                DepartDir resultNode = child.findDepartDirById(id);
                if (resultNode != null) {
                    return resultNode;
                }
            }
            return null;
        }
    }

    /* 遍历一棵树，层次遍历 */
    public void traverse() {
        if (departmentId < 0)
            return;
        print(departmentId+"");
        if (subDepartDir == null || subDepartDir.isEmpty())
            return;
        int childNumber = subDepartDir.size();
        for (int i = 0; i < childNumber; i++) {
            DepartDir child = subDepartDir.get(i);
            child.traverse();
        }
    }

    /* 删除节点和它下面的晚辈 */
    public void deleteNode() {
        DepartDir parentNode = this.getParentDepartDir();
        int id = this.getDepartmentId();

        if (parentNode != null) {
            parentNode.deleteChildNode(id);
        }
    }

    /* 删除当前节点的某个子节点 */
    public void deleteChildNode(int childId) {
        List<DepartDir> subDepartDir = getSubDepartDir();
        int childNumber = subDepartDir.size();
        for (int i = 0; i < childNumber; i++) {
            DepartDir child = subDepartDir.get(i);
            if (child.getDepartmentId() == childId) {
                subDepartDir.remove(i);
                return;
            }
        }
    }
    public void print(String content) {
        System.out.println(content);
    }
    public DepartDir(Integer departmentId, Integer pid, String departmentName) {
        this.departmentId = departmentId;
        this.pid = pid;
        this.departmentName = departmentName;
    }
    public void addDepartDir(DepartDir departDir) {
        subDepartDir.add(departDir);
    }
    
    public void addDepartDirAll(List<DepartDir> departDirs) {
    	subDepartDir.addAll(departDirs);
    }
    public Integer getDepartmentId() {
        return departmentId;
    }
    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }
    public String getDepartmentName() {
        return departmentName;
    }
    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
    public Integer getPid() {
        return pid;
    }
    public void setPid(Integer pid) {
        this.pid = pid;
    }
    public List<DepartDir> getSubDepartDir() {
		return subDepartDir;
	}
	public void setSubDepartDir(List<DepartDir> subDepartDir) {
		this.subDepartDir = subDepartDir;
	}
    @JsonIgnore
    public DepartDir getParentDepartDir() {
        return parentDepartDir;
    }

    public void setParentDepartDir(DepartDir parentDepartDir) {
        this.parentDepartDir = parentDepartDir;
    }
}
