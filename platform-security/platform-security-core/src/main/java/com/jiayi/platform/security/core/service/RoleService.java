package com.jiayi.platform.security.core.service;

import com.jiayi.platform.common.exception.ValidException;
import com.jiayi.platform.security.core.entity.Resource;
import com.jiayi.platform.security.core.entity.Role;
import com.jiayi.platform.security.core.entity.RoleResource;
import com.jiayi.platform.security.core.dao.ResourceDao;
import com.jiayi.platform.security.core.dao.RoleDao;
import com.jiayi.platform.security.core.dao.RoleResourceDao;
import com.jiayi.platform.security.core.dto.PageResult;
import com.jiayi.platform.security.core.dto.RoleDto;
import com.jiayi.platform.security.core.vo.RoleRequest;
import com.jiayi.platform.security.core.vo.RoleSearchVO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;
import java.util.stream.Collectors;

@Transactional
@Service
public class RoleService {
    @Autowired
    private RoleDao roleDao;

    @Autowired
    private ResourceDao resourceDao;

    @Autowired
    private RoleResourceDao roleResourceDao;

    public Role addRole(RoleRequest roleRequest) {
    	int count = roleDao.isNameUserd(roleRequest.getName());
    	if(count > 0)
    		throw new ValidException("角色名重复");
        Role role = new Role();
        role.setName(roleRequest.getName());
        role.setDescription(roleRequest.getDescription());
        role.setIsValid("1");
        Date now  = new Date();
        role.setCreateDate(now);
        role.setUpdateDate(now);

        roleDao.save(role);

        saveRoleResources(role, roleRequest);

        return role;
    }

    public void deleteRoleById(Long roleId) {
    	int count = roleDao.isUsedInUser(roleId);
    	if(count > 0)
    		throw new ValidException("删除失败，角色已关联用户");
        roleResourceDao.deleteByRoleId(roleId);
        roleDao.deleteById(roleId);
    }

    public void modifyRole(Long roleId, RoleRequest roleRequest) {
        Role role = roleDao.findById(roleId).get();
        if(StringUtils.isNotBlank(roleRequest.getName()) && !roleRequest.getName().equals(role.getName())) {
        	int count = roleDao.isNameUserd(roleRequest.getName());
        	if(count > 0)
        		throw new ValidException("角色名重复");
        }
        role.setName(roleRequest.getName());
        role.setDescription(roleRequest.getDescription());
        role.setUpdateDate(new Date());
        roleDao.save(role);

        if(roleRequest.getResourceIds() != null) {
	        roleResourceDao.deleteByRoleId(roleId);
	        saveRoleResources(role, roleRequest);
        }
    }

    private void saveRoleResources(Role role, RoleRequest roleRequest) {
        Set<Integer> resourceIds = new HashSet<>();
        if (roleRequest.getResourceIds() != null) {
            resourceIds.addAll(roleRequest.getResourceIds());
        }

        if (CollectionUtils.isEmpty(resourceIds)) {
            Set<Resource> defaultResources = resourceDao.findByIsDefault((short) 1);
            Set<Integer> defaultResourceIds = new HashSet<>();
            if (CollectionUtils.isNotEmpty(defaultResources)) {
                defaultResourceIds.addAll(defaultResources.stream()
                        .map(Resource::getId).filter(i -> i != 0)
                        .collect(Collectors.toSet()));
                defaultResourceIds.addAll(defaultResources.stream()
                        .map(Resource::getParentId)
                        .filter(i -> i != 0)
                        .collect(Collectors.toSet()));
            }
            resourceIds.addAll(defaultResourceIds);
        }

        if (CollectionUtils.isNotEmpty(resourceIds)) {
            resourceIds.addAll(findRelaitonResourceIds(resourceIds));
        }
        List<RoleResource> roleResourceList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(resourceIds)) {
            for (Integer resourceId : resourceIds) {
                RoleResource roleResource = new RoleResource();
                Resource resource = new Resource();
                resource.setId(resourceId);
                roleResource.setResource(resource);
                roleResource.setRole(role);
                roleResourceList.add(roleResource);
            }
        }
        role.setRoleResoures(roleResourceList);

        roleResourceDao.saveAll(roleResourceList);
    }

    private List<Integer> findRelaitonResourceIds(Set<Integer> resourceIds) {
        if (CollectionUtils.isEmpty(resourceIds)) {
            return Collections.emptyList();
        }

        List<Resource> relations = resourceDao.findAllById(resourceIds);
        if (CollectionUtils.isEmpty(relations)) {
            return Collections.emptyList();
        }

        List<Integer> relationIds = relations.stream().map(r -> r.getRelationIds().split(","))
                .flatMap(Arrays::stream).distinct().map(Integer::valueOf)
                .filter(r -> r != 0).collect(Collectors.toList());
        return relationIds;
    }

    public PageResult<RoleDto> search(RoleSearchVO roleSearchVO) {

        Specification<Role> specification = new Specification<Role>() {
            @Override
            public Predicate toPredicate(Root<Role> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (StringUtils.isNotBlank(roleSearchVO.getName())) {
                    list.add(cb.like(root.get("name"), "%" + roleSearchVO.getName().trim() + "%"));
                }
                return cb.and(list.toArray(new Predicate[]{}));
            }
        };
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Sort.Direction.DESC, "createDate"));

        Pageable pageable = new PageRequest(roleSearchVO.getPage(), roleSearchVO.getSize(), new Sort(orders));

        Page<Role> pageResult = roleDao.findAll(specification, pageable);
        List<Role> roles = pageResult.getContent();
        List<RoleDto> roleDtos = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(roles)) {
            for (Role role : roles) {
                RoleDto roleDto = new RoleDto(role);
                List<RoleResource> roleResources = role.getRoleResoures();
                List<Integer> resourceIds = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(roleResources)) {
                    resourceIds = roleResources.stream().map(r -> r.getResource().getId()).collect(Collectors.toList());
                }
                roleDto.setResourceIds(resourceIds);
                roleDtos.add(roleDto);
            }
        }
        return new PageResult<>(roleDtos, pageResult.getTotalElements(), pageResult.getNumber(), pageResult.getSize());
    }
}
