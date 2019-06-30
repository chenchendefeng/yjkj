package com.jiayi.platform.security.core.service;

import com.jiayi.platform.common.exception.DBException;
import com.jiayi.platform.common.exception.ValidException;
import com.jiayi.platform.security.core.entity.Department;
import com.jiayi.platform.security.core.entity.Role;
import com.jiayi.platform.security.core.entity.User;
import com.jiayi.platform.security.core.entity.UserRole;
import com.jiayi.platform.security.core.dao.DepartmentDao;
import com.jiayi.platform.security.core.dao.UserDao;
import com.jiayi.platform.security.core.dao.UserRoleDao;
import com.jiayi.platform.security.core.dto.PageResult;
import com.jiayi.platform.security.core.dto.PageableRequest;
import com.jiayi.platform.security.core.util.FileUtil;
import com.jiayi.platform.security.core.util.Md5Util;
import com.jiayi.platform.security.core.vo.PasswordRequest;
import com.jiayi.platform.security.core.vo.PersonalInfo;
import com.jiayi.platform.security.core.vo.UserRequest;
import com.jiayi.platform.security.core.vo.UserSearchVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Transactional
@Service
public class UserService extends CommonService{
	protected static final Logger log = LoggerFactory.getLogger(UserService.class);
	@Autowired
	private UserDao userDao;
	@Autowired
	private DepartmentDao departmentDao;
	@Autowired
	private UserRoleDao userRoleDao;

	@Value("${platform-source.file-portrait-prefix}")
	private String remotePrefix;

	public void logout(long id) {
		User user = userDao.findById(id).get();
		try {
			user.setToken(null);
			user = userDao.save(user);
		} catch (Exception e) {
			log.error("退出失败", e);
		}
	}

	public User getUserInfo(long id) {
		User user = userDao.findById(id).get();
		if(StringUtils.isNotBlank(user.getPortraitUrl()))
			user.setRealPath(remotePrefix + user.getPortraitUrl());
		return user;
	}

	public PageResult<User> search(Long userId, UserSearchVo userSearchVo) {
		if(userSearchVo.getPage() == null)
			userSearchVo.setPage(0);
		if(userSearchVo.getSize() == null)
			userSearchVo.setSize(10);
		Integer departmentId = userDao.findDeptsByUserId(userId);
		List<Integer> deptsId = super.getDeptIdsByPid(departmentId);

		Sort sort = new Sort(Sort.Direction.DESC, "id");
		Pageable pageable = new PageableRequest<>(userSearchVo.getPage(), userSearchVo.getSize(), sort);
		Specification<User> specification = new Specification<User>() {
			@Override
			public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> list = new ArrayList<>();
				list.add(cb.equal(root.get("beValid"), 1));
				if(StringUtils.isNotBlank(userSearchVo.getUsername())) {
					list.add(cb.like(root.get("username"), "%" + userSearchVo.getUsername().trim() + "%"));
				}
				if(StringUtils.isNotBlank(userSearchVo.getNikename())) {
					list.add(cb.like(root.get("nickname"), "%" + userSearchVo.getNikename().trim() + "%"));
				}
				if (deptsId != null && deptsId.size() > 0) {
					Predicate predicate = cb.or(root.get("departmentId").get("id").in(deptsId), cb.isNull(root.get("departmentId").get("id")));
					list.add(predicate);
				} else {
					list.add(cb.isNull(root.get("departmentId").get("id")));
				}
				return cb.and(list.toArray(new Predicate[0]));
			}
		};
		Page<User> pageResult = userDao.findAll(specification, pageable);
		return new PageResult<>(pageResult.getContent(), pageResult.getTotalElements(), userSearchVo.getPage(),
				pageResult.getContent().size());
	}

	public User add(UserRequest userRequest) {
		int count = userDao.isUserExist(userRequest.getUsername());
		if(count > 0)
			throw new ValidException("警号已存在");
		User user = userRequest.toEntity();
		if (userRequest.getDepartmentId() != null) {
			Department department = departmentDao.findById(userRequest.getDepartmentId()).get();
			user.setDepartmentId(department);
		}
		user = userDao.save(user);
		saveUserRole(user, userRequest.getRoleIds());
		return user;
	}

	private void saveUserRole(User user, List<Long> roleIds) {
		List<UserRole> userRoleList = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(roleIds)) {
            for (int i = 0; i < roleIds.size(); i++) {
                UserRole userRole = new UserRole();
                Role role = new Role();
                role.setId(roleIds.get(i));
                userRole.setRole(role);
                userRole.setUser(user);
                userRoleList.add(userRole);
            }
        }
		user.setUserRoles(userRoleList);
		userRoleDao.saveAll(userRoleList);
	}

	public User modify(Long id, UserRequest userRequest) {
		User before = userDao.findById(id).get();
		if(StringUtils.isNotBlank(userRequest.getUsername()) && !userRequest.getUsername().equals(before.getUsername())) {
			int count = userDao.isUserExist(userRequest.getUsername());
			if(count > 0)
				throw new ValidException("警号已存在");
		}
		String fileName = before.getPortraitUrl();
		this.setUser(before, userRequest);
		before = userDao.save(before);
//		userRoleDao.deleteByUserId(id);//直接删有问题,暂时先查询对象后删除
		List<UserRole> ur = userRoleDao.findByUserId(id);
		if(CollectionUtils.isNotEmpty(ur))
			userRoleDao.deleteAll(ur);
		saveUserRole(before, userRequest.getRoleIds());
		if (StringUtils.isNotBlank(userRequest.getPortraitUrl()) && StringUtils.isNotBlank(fileName)) {
			FileUtil.deleteFile(fileName);
		}
		return before;
	}

	private void setUser(User before, UserRequest userRequest) {
		if (userRequest.getUsername() != null)
			before.setUsername(userRequest.getUsername());
		if (userRequest.getPassword() != null)
			before.setPassword(Md5Util.getEncryptedPwd(userRequest.getPassword()));
		if (userRequest.getDepartmentId() != null)
			before.setDepartmentId(departmentDao.findById(userRequest.getDepartmentId()).get());
		if (userRequest.getDuties() != null)
			before.setDuties(userRequest.getDuties());
		if (userRequest.getNickname() != null)
			before.setNickname(userRequest.getNickname());
		if (userRequest.getSex() != null)
			before.setSex(userRequest.getSex());
		if (userRequest.getBirthday() != null)
			before.setBirthday(userRequest.getBirthday());
		if (userRequest.getPortraitUrl() != null)
			before.setPortraitUrl(userRequest.getPortraitUrl());
		if (userRequest.getMobile() != null)
			before.setMobile(userRequest.getMobile());
		if (userRequest.getEmail() != null)
			before.setEmail(userRequest.getEmail());
		if (userRequest.getBeActive() != null)
			before.setBeActive(userRequest.getBeActive());
		if (userRequest.getRegionCode() != null)
			before.setRegionCode(userRequest.getRegionCode());
		if (userRequest.getOption() != null)
			before.setOption(userRequest.getOption());
		if (userRequest.getMetaInfo() != null)
			before.setMetaInfo(userRequest.getMetaInfo());
		if (userRequest.getRemark() != null)
			before.setRemark(userRequest.getRemark());
		before.setUpdateAt(new Date());
	}

	public void delete(Long id) {
		try {
//			userRoleDao.deleteByUserId(id);//直接删有问题,暂时先查询对象后删除
			List<UserRole> ur = userRoleDao.findByUserId(id);
			if(CollectionUtils.isNotEmpty(ur))
				userRoleDao.deleteAll(ur);
			userDao.deleteById(id);
		} catch (Exception e) {
			throw new DBException("删除失败", e);
		}
	}

	public void modifypwd(Long userId, PasswordRequest passwordRequest) {
		User user = userDao.findById(userId).get();
		if (user == null)
			throw new ValidException("用户不存在");
		if (!user.getPassword().equals(Md5Util.getEncryptedPwd(passwordRequest.getOldPassword())))
			throw new ValidException("原始密码错误");
		user.setPassword(Md5Util.getEncryptedPwd(passwordRequest.getNewPassword()));
		userDao.save(user);
	}

	public void updatePortrait(String uploadPath, String portraitUrl, Long userId) {
		try {
			User user = userDao.findById(userId).get();
			String fileName = user.getPortraitUrl();
			user.setPortraitUrl(portraitUrl);
			userDao.save(user);
			if(StringUtils.isNotBlank(fileName))
				FileUtil.deleteFile(uploadPath + fileName);
		} catch (Exception e) {
			throw new DBException("修改头像失败", e);
		}
	}

	public void saveUserInfo(Long userId, PersonalInfo info) {
		User user = userDao.findById(userId).get();
		try {
			if(StringUtils.isNotBlank(info.getNickname()))
				user.setNickname(info.getNickname());
			if(StringUtils.isNotBlank(info.getSex()))
				user.setSex(info.getSex());
			if(info.getMobile() != null)
				user.setMobile(info.getMobile());
			if(info.getEmail() != null)
				user.setEmail(info.getEmail());
			if(StringUtils.isNotBlank(info.getPortraitUrl()))
				user.setPortraitUrl(info.getPortraitUrl());
			userDao.save(user);
		} catch (Exception e) {
			throw new DBException("保存失败", e);
		}
	}

	public void editDescription(String remark, Integer id) {
		userDao.updateRemark(remark,id);
	}
}
