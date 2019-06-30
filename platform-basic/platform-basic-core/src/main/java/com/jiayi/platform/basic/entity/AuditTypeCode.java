package com.jiayi.platform.basic.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "t_audit_type_code")
@Getter
@Setter
@ToString
public class AuditTypeCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String collectType;
    private Long code;
    private Long parentCode;
    private String english;
    private String chineseSimplified;
    private String chineseTraditional;
}
