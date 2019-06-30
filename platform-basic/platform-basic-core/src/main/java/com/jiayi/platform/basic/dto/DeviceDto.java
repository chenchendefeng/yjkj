package com.jiayi.platform.basic.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.jiayi.platform.basic.entity.Src;

import java.util.Date;

public class DeviceDto {

    private Long pkId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String name;
    private String address;
    private String code;
    private String standardCode;
    private Src src;
    private String srcCode;
    private Long placeId;
    private String placeName;
    private String district;
    private String placeCode;
    private String placeAddress;
    private Integer isOnline;
    private Integer qualify;
    private Integer average;

    private Integer type;
    private String installerName;
    private String installerPhone;
    private Date installAt;
    private Integer vendorId;
    private Long longitude;
    private Long latitude;
    private Date dataEndTime;
    private boolean isHavingData;
    private Integer departmentId;
    private Date createAt;
    private String ipPort;
    private String collect;
    private Long collectionRadius;
    private Long collectionInterval;
    private String dId;
    private String ssId;
    private String authType;// 0认证码认证 1非认证码认证 2其他
    private String authSrc;
    private String installType;//0室内 1室外
    private String internetEnvironment;// 终端上网环境
    private String installFloor;
    private String installRoom;
    private String subwayLineInfo;
    private String subwayVehicleInfo;
    private String subwayCompartmentNum;
    private String subwayCarCode;
    private String subwayStationInfo;
    private String itvAccount;

    private String softwareVersion;
    private String firmwareVersion;
    private String repo;// 特征库

    private Integer model;// 设备型号
    private String mergerName;
    private Integer fixed;

    public DeviceDto(Long pkId, Long id, String name, String address, String code,
                     String standardCode, Src src, Long placeId, String placeName,
                     String district, String placeCode, String placeAddress, Integer isOnline,
                     Integer qualify, Integer average, Integer type, String installerName,
                     String installerPhone, Date installAt, Integer vendorId, Long longitude,
                     Long latitude, Long dataEndTime, Integer departmentId, Date createAt,
                     String ipPort, String collect, Long collectionRadius, Long collectionInterval,
                     String dId, String ssId, String authType, String authSrc,
                     String installType, String internetEnvironment, String installFloor, String installRoom,
                     String subwayLineInfo, String subwayVehicleInfo, String subwayCompartmentNum, String subwayCarCode,
                     String subwayStationInfo, String itvAccount, String softwareVersion, String firmwareVersion,
                     String repo, Integer model, String mergerName, Integer fixed) {
        this.pkId = pkId;
        this.id = id;
        this.name = name;
        this.address = address;
        this.code = code;
        this.standardCode = standardCode;
        this.src = src;
        this.placeId = placeId;
        this.placeName = placeName;
        this.district = district;
        this.placeCode = placeCode;
        this.placeAddress = placeAddress;
        this.isOnline = isOnline;
        this.qualify = qualify;
        this.average = average;
        this.type = type;
        this.installerName = installerName;
        this.installerPhone = installerPhone;
        this.installAt = installAt;
        this.vendorId = vendorId;
        this.longitude = longitude;
        this.latitude = latitude;
        if (dataEndTime == null) {
            this.dataEndTime = null;
        } else {
            this.dataEndTime = new Date(dataEndTime);
        }
        this.departmentId = departmentId;
        this.createAt = createAt;
        this.ipPort = ipPort;
        this.collect = collect;
        this.collectionRadius = collectionRadius;
        this.collectionInterval = collectionInterval;
        this.dId = dId;
        this.ssId = ssId;
        this.authType = authType;
        this.authSrc = authSrc;
        this.installType = installType;
        this.internetEnvironment = internetEnvironment;
        this.installFloor = installFloor;
        this.installRoom = installRoom;
        this.subwayLineInfo = subwayLineInfo;
        this.subwayVehicleInfo = subwayVehicleInfo;
        this.subwayCompartmentNum = subwayCompartmentNum;
        this.subwayCarCode = subwayCarCode;
        this.subwayStationInfo = subwayStationInfo;
        this.itvAccount = itvAccount;
        this.softwareVersion = softwareVersion;
        this.firmwareVersion = firmwareVersion;
        this.repo = repo;
        this.model = model;
        this.mergerName = mergerName;
        this.fixed = fixed;
    }

    public DeviceDto() {
    }

//    public DeviceDto(Long pkId, Long id, String name, String address, String code, String standardCode, Long placeId,
//                     String placeName, String district, String ongplaceCode, String placeAddress, Integer type,
//                     String installerName, String installerPhone, Date installAt, Long vendorId, Long longitude,
//                     Long latitude, Src src, Integer departmentId, Date createAt, String ipPort, String collect) {
//        super();
//        this.pkId = pkId;
//        this.id = id;
//        this.name = name;
//        this.address = address;
//        this.code = code;
//        this.standardCode = standardCode;
//        this.placeId = placeId;
//        this.placeName = placeName;
//        this.district = district;
//        this.placeCode = placeCode;
//        this.placeAddress = placeAddress;
//        this.type = type;
//        this.installerName = installerName;
//        this.installerPhone = installerPhone;
//        this.installAt = installAt;
//        this.vendorId = vendorId;
//        this.longitude = longitude;
//        this.latitude = latitude;
//        this.src = src;
//        this.departmentId = departmentId;
//        this.createAt = createAt;
//        this.ipPort = ipPort;
//        this.collect = collect;
//    }

    public Long getPkId() {
        return pkId;
    }

    public void setPkId(Long pkId) {
        this.pkId = pkId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStandardCode() {
        return standardCode;
    }

    public void setStandardCode(String standardCode) {
        this.standardCode = standardCode;
    }

    public Src getSrc() {
        return src;
    }

    public void setSrc(Src src) {
        this.src = src;
    }

    public String getSrcCode() {
        return srcCode;
    }

    public void setSrcCode(String srcCode) {
        this.srcCode = srcCode;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getPlaceCode() {
        return placeCode;
    }

    public void setPlaceCode(String placeCode) {
        this.placeCode = placeCode;
    }

    public String getPlaceAddress() {
        return placeAddress;
    }

    public void setPlaceAddress(String placeAddress) {
        this.placeAddress = placeAddress;
    }

    public Integer getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(Integer isOnline) {
        this.isOnline = isOnline;
    }

    public Integer getAverage() {
        return average;
    }

    public void setAverage(Integer average) {
        this.average = average;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getInstallerName() {
        return installerName;
    }

    public void setInstallerName(String installerName) {
        this.installerName = installerName;
    }

    public String getInstallerPhone() {
        return installerPhone;
    }

    public void setInstallerPhone(String installerPhone) {
        this.installerPhone = installerPhone;
    }

    public Date getInstallAt() {
        return installAt;
    }

    public void setInstallAt(Date installAt) {
        this.installAt = installAt;
    }

    public Integer getVendorId() {
        return vendorId;
    }

    public void setVendorId(Integer vendorId) {
        this.vendorId = vendorId;
    }

    public Integer getFixed() {
        return fixed;
    }

    public void setFixed(Integer fixed) {
        this.fixed = fixed;
    }

    public void setLongitude(Long longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(Long latitude) {
        this.latitude = latitude;
    }

    public Date getDataEndTime() {
        return dataEndTime;
    }

    public void setDataEndTime(Date dataEndTime) {
        this.dataEndTime = dataEndTime;
    }

    public boolean isHavingData() {
        return isHavingData;
    }

    public void setHavingData(boolean havingData) {
        isHavingData = havingData;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public String getIpPort() {
        return ipPort;
    }

    public void setIpPort(String ipPort) {
        this.ipPort = ipPort;
    }

    public String getCollect() {
        return collect;
    }

    public void setCollect(String collect) {
        this.collect = collect;
    }

    public Long getCollectionRadius() {
        return collectionRadius;
    }

    public void setCollectionRadius(Long collectionRadius) {
        this.collectionRadius = collectionRadius;
    }

    public Long getCollectionInterval() {
        return collectionInterval;
    }

    public void setCollectionInterval(Long collectionInterval) {
        this.collectionInterval = collectionInterval;
    }

    public String getdId() {
        return dId;
    }

    public void setdId(String dId) {
        this.dId = dId;
    }

    public String getSsId() {
        return ssId;
    }

    public void setSsId(String ssId) {
        this.ssId = ssId;
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public String getAuthSrc() {
        return authSrc;
    }

    public void setAuthSrc(String authSrc) {
        this.authSrc = authSrc;
    }

    public String getInstallType() {
        return installType;
    }

    public void setInstallType(String installType) {
        this.installType = installType;
    }

    public String getInternetEnvironment() {
        return internetEnvironment;
    }

    public void setInternetEnvironment(String internetEnvironment) {
        this.internetEnvironment = internetEnvironment;
    }

    public String getInstallFloor() {
        return installFloor;
    }

    public void setInstallFloor(String installFloor) {
        this.installFloor = installFloor;
    }

    public String getInstallRoom() {
        return installRoom;
    }

    public void setInstallRoom(String installRoom) {
        this.installRoom = installRoom;
    }

    public String getSubwayLineInfo() {
        return subwayLineInfo;
    }

    public void setSubwayLineInfo(String subwayLineInfo) {
        this.subwayLineInfo = subwayLineInfo;
    }

    public String getSubwayVehicleInfo() {
        return subwayVehicleInfo;
    }

    public void setSubwayVehicleInfo(String subwayVehicleInfo) {
        this.subwayVehicleInfo = subwayVehicleInfo;
    }

    public String getSubwayCompartmentNum() {
        return subwayCompartmentNum;
    }

    public void setSubwayCompartmentNum(String subwayCompartmentNum) {
        this.subwayCompartmentNum = subwayCompartmentNum;
    }

    public String getSubwayCarCode() {
        return subwayCarCode;
    }

    public void setSubwayCarCode(String subwayCarCode) {
        this.subwayCarCode = subwayCarCode;
    }

    public String getSubwayStationInfo() {
        return subwayStationInfo;
    }

    public void setSubwayStationInfo(String subwayStationInfo) {
        this.subwayStationInfo = subwayStationInfo;
    }

    public String getItvAccount() {
        return itvAccount;
    }

    public void setItvAccount(String itvAccount) {
        this.itvAccount = itvAccount;
    }

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public String getRepo() {
        return repo;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }

    public Integer getModel() {
        return model;
    }

    public void setModel(Integer model) {
        this.model = model;
    }

    public String getMergerName() {
        return mergerName;
    }

    public void setMergerName(String mergerName) {
        this.mergerName = mergerName;
    }

    public Integer getQualify() {
        if (null == qualify) {
            return 2;
        }
        return qualify;
    }

    public void setQualify(Integer qualify) {
        this.qualify = qualify;
    }

    @JsonSerialize(using = ToStringSerializer.class)
    public Long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Long placeId) {
        this.placeId = placeId;
    }

    public Double getLongitude() {
        if (longitude == null) {
            return 0.0;
        } else {
            return longitude / Math.pow(10, 12);
        }
    }

    public Double getLatitude() {
        if (latitude == null) {
            return 0.0;
        } else {
            return latitude / Math.pow(10, 12);
        }
    }
}
