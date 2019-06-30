package com.jiayi.platform.basic.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author : weichengke
 * @date : 2019-03-01 10:17
 */
@Entity
@Table(name = "t_device")
@Getter
@Setter
@ToString
public class Device implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pkId;
    private Long id;
    private String src; //code of source
    private String code; //设备编码
    private String standardCode;//自动生成标准编码//todo 这个还需不需要？@chengke
    private String srcOrgCode;
    private Integer type;// 设备子类型
    private Long placeId;
    private Integer vendorId;
    private String placeCode;
    private Date installAt;
    private Long longitude;
    private Long latitude;
    private String name;
    private String address;
    private Date createAt;
    private Date updateAt;
    private Long gridCode;
    private Integer status;
    private String mac;

//    private String limitType;
//    private Long limitCount;

    //    @ManyToOne
//    @NotFound(action = NotFoundAction.IGNORE)
//    @JoinColumn(name = "type", referencedColumnName = "id", insertable = false, updatable = false)
//    private DeviceSubType deviceType;
    //    @ManyToOne
//    @NotFound(action = NotFoundAction.IGNORE)
//    @JoinColumn(name = "place_id", referencedColumnName = "id", insertable = false, updatable = false)
//    @JsonIgnore
//    private Place place;
//    @ManyToOne(fetch = FetchType.EAGER)
//    @NotFound(action = NotFoundAction.IGNORE)
//    @JoinColumn(name = "src", referencedColumnName = "code", insertable = false, updatable = false)
//    @JsonIgnore
//    private Src srcId;
}
