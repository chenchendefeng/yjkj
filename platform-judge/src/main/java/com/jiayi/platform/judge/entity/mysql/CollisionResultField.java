package com.jiayi.platform.judge.entity.mysql;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@ToString
@Entity
@Table(name = "collision_result_field")
public class CollisionResultField {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "field_name")
    private String fieldName;
    @Column(name = "field_desc")
    private String fieldDesc;
    @Column(name = "field_type")
    private String fieldType;
    @Column(name = "request_tmpl_id")
    private Long requestTmplId;
    @Column(name = "request_type")
    private String requestType;
}
