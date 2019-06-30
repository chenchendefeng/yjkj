package com.jiayi.platform.security.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "username")
    private String username;
    private String password;
    private String department;
    @ManyToOne(fetch = FetchType.EAGER)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "department_id")
    private Department departmentId;
    private String duties;
    @Column(name = "be_active",
            nullable = false,
            columnDefinition = "INT default 1")
    private int beActive;
    @Column(name = "meta_info")
    private String metaInfo;
    @ManyToOne(fetch = FetchType.EAGER)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "role_id")
    @JsonIgnore
    private Role role;
    private String email;
    private String mobile;
    private String nickname;
    private String sex;
    private Date birthday;
    @Column(name = "portrait_url")
    private String portraitUrl;
    @Column(name = "[option]")
    private String option;
    @Column(name = "be_valid")
    private int beValid = 1;
    @Column(name = "create_at")
    private Date createAt;
    @Column(name = "update_at")
    private Date updateAt;
    private String token;
    @Column(name = "token_expire_at")
    private Date tokeExpireAt;
    @Column(name = "region_code")
    private Integer regionCode;
    private String remark;
    @Transient
    private String realPath;
    @OneToMany(mappedBy = "user")
    private List<UserRole> userRoles;
    @Transient
    private String refreshToken;
}
