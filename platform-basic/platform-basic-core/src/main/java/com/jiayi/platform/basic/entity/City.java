package com.jiayi.platform.basic.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

/**
 * @author : weichengke
 * @date : 2019-03-05 14:40
 */
@Entity
@Table(name = "code_city")
@Getter
@Setter
@ToString
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id           ;
    private String name         ;
    private Long pid          ;
    @Column(name = "short_name")
    private String shortName   ;
    private String level        ;
    @Column(name = "city_code")
    private String cityCode    ;
    @Column(name = "zip_code")
    private String zipCode     ;
    @Column(name = "merger_name")
    private String mergerName  ;
    private String lng          ;
    private String lat          ;
    private String pinyin       ;
}
