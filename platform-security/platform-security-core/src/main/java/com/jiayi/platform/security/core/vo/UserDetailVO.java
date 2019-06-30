package com.jiayi.platform.security.core.vo;


import com.jiayi.platform.security.core.entity.Department;
import com.jiayi.platform.security.core.entity.Role;
import com.jiayi.platform.security.core.entity.User;
import com.jiayi.platform.security.core.entity.UserRole;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class UserDetailVO {

    private long id;
    private String username;
    private String password;
    private Department department;
    private String duties;
    private int beActive;
    private String metaInfo;
    private String email;
    private String mobile;
    private String nickname;
    private String sex;
    private Date birthday;
    private String portraitUrl;
    private String option;
    private int beValid;
    private Date createAt;
    private Date updateAt;
    private String token;
    private Date tokeExpireAt;
    private Integer regionCode;
    private String remark;
    private String realPath;
    private List<Role> roles;
    private List<String> resources;
    private String refreshToken;

    public UserDetailVO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.department = user.getDepartmentId();
        this.duties = user.getDuties();
        this.beActive = user.getBeActive();
        this.metaInfo = user.getMetaInfo();
        this.email = user.getEmail();
        this.mobile = user.getMobile();
        this.nickname = user.getNickname();
        this.sex = user.getSex();
        this.birthday = user.getBirthday();
        this.portraitUrl = user.getPortraitUrl();
        this.option = user.getOption();
        this.beValid = user.getBeValid();
        this.createAt = user.getCreateAt();
        this.updateAt = user.getUpdateAt();
        this.token = user.getToken();
        this.regionCode = user.getRegionCode();
        this.remark = user.getRemark();
        this.realPath = user.getRealPath();
        this.refreshToken = user.getRefreshToken();

        List<UserRole> rs = user.getUserRoles();
        if (rs != null && (!rs.isEmpty())) {
            this.roles = rs
                    .stream()
                    .map(UserRole::getRole)
                    .collect(Collectors.toList());
        } else this.roles = Collections.emptyList();

        this.resources = new ArrayList<>();
        for (Role r : this.roles) {
            this.resources.addAll(r.getRoleResoures()
                    .stream()
                    .filter(rrs -> (rrs.getResource() != null && rrs.getResource().getId() != null))
                    .map(rrs -> rrs.getResource().getCode())
                    .collect(Collectors.toList()));
        }
    }
}
