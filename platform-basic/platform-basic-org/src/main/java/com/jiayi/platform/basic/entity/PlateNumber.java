package com.jiayi.platform.basic.entity;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "t_plate_num_addr")
public class PlateNumber {

    /**
     * 车牌编码
     */
    @Id
    private String code;
    /**
     * 所在省名称
     */
    @Column(name = "province_name")
    private String provinceName;
    /**
     *所在省 全国唯一编码 id
     */
    @Column(name = "province_id")
    private String provinceId;

    /**
     * 所在 城市 ID
     */
    @Column(name = "city_id")
    private String cityId;
    /**
     * 所在 城市名称
     */
    @Column(name = "city_name")
    private String cityName;

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getAddress(){
        if (StringUtils.isEmpty(provinceName) || StringUtils.isEmpty(cityName)){
            return "";
        }else
        {
            return getProvinceName()+"-"+getCityName();
        }
    }

    @Override
    public String toString() {
        return "PlateNumber [Code=" + code + ", city=" + cityName
                +  "]";
    }
}
