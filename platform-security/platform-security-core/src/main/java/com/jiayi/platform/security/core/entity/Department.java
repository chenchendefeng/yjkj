package com.jiayi.platform.security.core.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "department")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer pid;
    private String name;
    private String code;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_at")
    private Date createAt;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_at")
    private Date updateAt;

}
