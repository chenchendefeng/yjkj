package com.jiayi.platform.basic.entity;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;

@Entity
@Table(name = "t_phone_home")
public class PhoneHome {
    /**
     * 唯一ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * 手机号码段
     */
    private  long number;
    /**
     * 所在省
     */
    private String province;

    /**
     * 所在城市
     */
    private String city;

    /**
     * 所在运营商
     */
    private String operator;
    /**
     * 所在城市 固定电话区号
     */
    @Column(name = "area_code")
    private String areaCode;

    /**
     * 所在城市邮政编码
     */
    private String postcode;

    /**
     * 城市id
     */
    @Column(name = "city_id")
    private Long cityId;

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    /**
     * 取得所属地址 省-地市，如果为null返回unknown
     * @return “省-城市”
     */
    public String getAddress(){
        if (StringUtils.isEmpty(getProvince())){
            return "";
        }else {
            return getProvince()+ "-"+getCity();
        }
    }

    @Override
    public String toString() {
        return "PhoneHome [number=" + number + ", province=" + province
                + ", city=" + city
                + ", operator=" + operator+ "]";
    }
}
