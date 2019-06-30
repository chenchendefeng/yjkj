package com.jiayi.platform.security.core.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "user_depart_role")
public class UserDepartmentRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private Integer userId;
    @Column(name = "department_id")
    private Integer departmentId;
    private String permissions;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_at")
    private Date createAt;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updateAt")
    private Date updateAt;
}
