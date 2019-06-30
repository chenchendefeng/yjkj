package com.jiayi.platform.basic.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author : weichengke
 * @date : 2019-03-01 10:14
 */
@Data
@Entity
@Table(name = "t_device_sub_type")
@Getter
@Setter
@ToString
public class DeviceSubType implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String dataType;// 多个采集数据类型的集合
    private String description;
    @Column(name = "device_type")
    private Integer deviceType;// 设备主类型
    @Column(name = "create_date")
    private Date createDate;
    @Column(name = "update_date")
    private Date updateDate;
//    private String code;

}
