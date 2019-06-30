package com.jiayi.platform.basic.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.jiayi.platform.security.core.entity.Department;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "t_place")
@Getter
@Setter
@ToString
public class Place implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String code;
//    @ManyToOne
//    @NotFound(action = NotFoundAction.IGNORE)
//    @JoinColumn(name = "src", referencedColumnName = "code")
//    private Src src;
    @Column(name = "region_code")
    private Long regionCode;
    @Column(name = "sub_region_code")
    private Long subRegionCode;
    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "department_id")
    private Department department;
    private Long longitude;
    private Long latitude;
    private String name;
    private String phone;
    @Column(name = "cert_code")
    private String certCode;
    @Column(name = "contact_name")
    private String contactName;
    @Column(name = "contact_phone")
    private String contactPhone;
    @Column(name = "contact_cert_type")
    private Long contactCertType;
    @Column(name = "contact_cert_code")
    private String contactCertCode;
    @Column(name = "open_at")
    private String openAt;
    @Column(name = "close_at")
    private String closeAt;
    private String province;
    private String city;
    private String district;
    private String street;
    private String block;
    private String road;
    private String address;
    @Column(name = "src_info")
    private String srcInfo;
    @Column(name = "ex_info")
    private String exInfo;
    @Column(name = "create_at")
    private Date createAt;
    private Date installAt;
    @Column(name = "update_at")
    private Date updateAt;
    @Column(name = "grid_code")
    private Long gridCode;
    @Column(name = "place_type")
    private Integer placeType;// 经营性质：经营，非经营，围栏采集等

    @OrderBy("id ASC")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "place", fetch = FetchType.EAGER)
    private Set<PlaceTagRelation> placeTagRelation = new HashSet<PlaceTagRelation>();// 场所分类

    @OneToMany(mappedBy = "place",cascade = CascadeType.PERSIST)
    private Set<PlaceLabelRelation> placeLabelRelation;// 场所标签

    private String zip;
    private String principal;// 法人
    private String principalTel;//法人联系方式
    private String inforMan;// 安装人姓名
    private String inforManTel;//安装人电话
    private String inforManEmail;
    private String producerCode;//服务商代码
    private Integer status;
    private Integer endingNumber;
    private Integer serverNumber;
    private String exitIp;
    private String authAccount;
    private String netType;
    private Integer practitionerNumber;
    private String netMonitorDepartment;
    private String netMonitorMan;
    private String netMonitorManTel;
    private String remark;
    private String cityType;
    private String policeCode;
    private String mailAccount;
    private String mobileAccount;
    private String gisXpoint;
    private String gisYpoint;
    private String terminalFactoryOrgCode;
    private String orgCode;
    private String ipType;
    private Integer bandWidth;
    private Integer netLan;
    private Integer netLanTerminal;
    private String isSafe;
    private Integer wifiTerminal;
    private String principalCertType;
    private String principalCertCode;
    private String inforManQq;
    private String personQq;
    private String capType;

    public Place() {
    }

    public Place(Long id, String code) {//, Src src
        this.id = id;
        this.code = code;
//        this.src = src;
    }

    public Place(Long id, String code, String name ,String address) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.address = address;
    }
}
