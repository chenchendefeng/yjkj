package com.jiayi.platform.basic.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.jiayi.platform.basic.entity.PlaceLabelRelation;
import com.jiayi.platform.basic.entity.PlaceTagRelation;
import com.jiayi.platform.basic.entity.Src;
import com.jiayi.platform.basic.util.PlaceDeviceUtil;
import com.jiayi.platform.security.core.entity.Department;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

// TODO 需要和前端核对哪些字段需要哪些字段不需要
public class PlaceDto {
    private Long id;
    private String code;
    private Src src;
    private Long regionCode;
    private Long subRegionCode;
    private Department department;
    private Double longitude;
    private Double latitude;
    private String name;
    private String phone;
    private String certCode;
    private String contactName;
    private String contactPhone;
    private Long contactCertType;
    private String contactCertCode;
    private String openAt;
    private String closeAt;
    private String province;
    private String city;
    private String district;
    private String street;
    private String block;
    private String road;
    private String address;
    private String srcInfo;
    private String exInfo;
    private Date createAt;
    private Date updateAt;
    private Long gridCode;
    private Integer placeType;
    public Integer[] departmentIds;
    private Long deviceCount;

    private Set<PlaceTagRelation> placeTagRelation = new HashSet<PlaceTagRelation>();
    private Set<PlaceLabelRelation> placeLabelRelation;

    @JsonSerialize(using = ToStringSerializer.class)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Src getSrc() {
        return src;
    }

    public void setSrc(Src src) {
        this.src = src;
    }

    public Long getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(Long regionCode) {
        this.regionCode = regionCode;
    }

    public Long getSubRegionCode() {
        return subRegionCode;
    }

    public void setSubRegionCode(Long subRegionCode) {
        this.subRegionCode = subRegionCode;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCertCode() {
        return certCode;
    }

    public void setCertCode(String certCode) {
        this.certCode = certCode;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public Long getContactCertType() {
        return contactCertType;
    }

    public void setContactCertType(Long contactCertType) {
        this.contactCertType = contactCertType;
    }

    public String getContactCertCode() {
        return contactCertCode;
    }

    public void setContactCertCode(String contactCertCode) {
        this.contactCertCode = contactCertCode;
    }

    public String getOpenAt() {
        return openAt;
    }

    public void setOpenAt(String openAt) {
        this.openAt = openAt;
    }

    public String getCloseAt() {
        return closeAt;
    }

    public void setCloseAt(String closeAt) {
        this.closeAt = closeAt;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getRoad() {
        return road;
    }

    public void setRoad(String road) {
        this.road = road;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSrcInfo() {
        return srcInfo;
    }

    public void setSrcInfo(String srcInfo) {
        this.srcInfo = srcInfo;
    }

    public String getExInfo() {
        return exInfo;
    }

    public void setExInfo(String exInfo) {
        this.exInfo = exInfo;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }

    public Long getGridCode() {
        return gridCode;
    }

    public void setGridCode(Long gridCode) {
        this.gridCode = gridCode;
    }

    public Integer getPlaceType() {
        return placeType;
    }

    public void setPlaceType(Integer placeType) {
        this.placeType = placeType;
    }

    public Set<PlaceTagRelation> getPlaceTagRelation() {
        return placeTagRelation;
    }

    public void setPlaceTagRelation(Set<PlaceTagRelation> placeTagRelation) {
        this.placeTagRelation = placeTagRelation;
    }

    public String getCityArea() {
        return PlaceDeviceUtil.getCityAreaName(district);
    }

    public Long getDeviceCount() {
        return deviceCount;
    }

    public void setDeviceCount(Long deviceCount) {
        this.deviceCount = deviceCount;
    }

    public Set<PlaceLabelRelation> getPlaceLabelRelation() {
        return placeLabelRelation;
    }

    public void setPlaceLabelRelation(Set<PlaceLabelRelation> placeLabelRelation) {
        this.placeLabelRelation = placeLabelRelation;
    }

    public Integer[] getDepartmentIds() {
        return departmentIds;
    }

    public void setDepartmentIds(Integer[] departmentIds) {
        this.departmentIds = departmentIds;
    }
}
