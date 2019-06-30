package com.jiayi.platform.basic.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

/**
 * @author : qinxiaoni
 * @date : 2019-04-25 15:21
 */
@Entity
@Table(name = "t_device_model")
@Getter
@Setter
@ToString
public class DeviceModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private Integer deviceSubType;
    private Integer vendorId;
    private String description;
}
