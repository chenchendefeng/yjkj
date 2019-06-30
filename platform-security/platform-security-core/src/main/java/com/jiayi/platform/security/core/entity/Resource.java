package com.jiayi.platform.security.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "t_resource")
public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String code;
    private Integer parentId;
    private String description;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDate;
    @OneToMany(mappedBy = "resource")
    @JsonIgnore
    private List<RoleResource> roleResoures;
    private String relationIds;
    private Short isDefault;
    private Short versions;

    @Transient
    private List<Resource> children = new ArrayList<>();
}
