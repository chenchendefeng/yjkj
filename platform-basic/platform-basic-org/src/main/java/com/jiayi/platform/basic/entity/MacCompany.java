package com.jiayi.platform.basic.entity;


import javax.persistence.*;

@Entity
@Table(name = "t_mac_company")
public class MacCompany {

    /***
     * 注册为MA-L 在数据库 registry 的值
     */
    public static final String REGISTRY_MA_L_VALUE = "MA-L";
    /***
     * 注册为MA-L 在数据库中 assignment 保存的MAC地址长度
     */
    public static final int MA_L_LEN = 6;

    /***
     * 注册为MA-M 在数据库 registry 的值
     */
    public static final String REGISTRY_MA_M_VALUE = "MA-M";
    /***
     * 注册为MA-M 在数据库中 assignment 保存的MAC地址长度
     */
    public static final int MA_M_LEN = 7;

    /***
     * 注册为MA-M 在数据库 registry 的值
     */
    public static final String REGISTRY_MA_S_VALUE = "MA-S";
    /***
     * 注册为MA-S 在数据库中 assignment 保存的MAC地址长度
     */
    public static final int MA_S_LEN = 9;

    /**
     * 唯一ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * 注册类型
     */
    private String registry;

    /**
     *分配的MAC段 前辍
     */
    private String assignment;

    /**
     * 注册团体、公司名称
     */
    @Column(name = "organization_name")
    private String organizationName;
    /**
     * 注册团体、公司名称 地址
     */
    @Column(name = "organization_address")
    private String organizationAddress;

    /**
     * 注册团体、公司中文名称
     */
    @Column(name = "organization_name_cn")
    private String organizationNameCn;
    /**
     * 注册团体、公司名称中文地址
     */
    @Column(name = "organization_address_cn")
    private String organizationAddressCn;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAssignment() {
        return assignment;
    }

    public void setAssignment(String assignment) {
        this.assignment = assignment;
    }

    public String getOrganizationAddress() {
        return organizationAddress;
    }

    public void setOrganizationAddress(String organizationAddress) {
        this.organizationAddress = organizationAddress;
    }

    public String getOrganizationAddressCn() {
        return organizationAddressCn;
    }

    public void setOrganizationAddressCn(String organizationAddressCn) {
        this.organizationAddressCn = organizationAddressCn;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getOrganizationNameCn() {
        return organizationNameCn;
    }

    public void setOrganizationNameCn(String organizationNameCn) {
        this.organizationNameCn = organizationNameCn;
    }

    public String getRegistry() {
        return registry;
    }

    public void setRegistry(String registry) {
        this.registry = registry;
    }

    @Override
    public String toString() {
        return "MacCompany [mac=" + assignment + ", organization=" + organizationName
                +  "]";
    }
}
