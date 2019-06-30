package com.jiayi.platform.basic.dto;

import com.jiayi.platform.basic.entity.PlaceLabelRelation;
import com.jiayi.platform.basic.entity.PlaceTagRelation;
import com.jiayi.platform.basic.util.PlaceDeviceUtil;
import com.jiayi.platform.security.core.entity.Department;
import com.jiayi.platform.security.core.service.DepartmentService;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class PlaceDetailDto {
    private Long id;
    private String code;//场所编码
    //    private Src src;
    private Long deviceCount;//设备数量
    private Department department;//所属部门
    private Double longitude;
    private Double latitude;
    private String name;//场所名称
    private String openAt;//营业开始时间
    private String closeAt;//营业结束时间
    private String province;
    private String city;
    private String district;//所属区县
    private String address;//场所地址
    private Date createAt;//创建时间
    private Date installAt;//安装时间
    private Integer placeType;// 经营性质：经营，非经营，围栏采集等

    private Set<PlaceTagRelation> placeTagRelation = new HashSet<PlaceTagRelation>();// 场所类型

    private Set<PlaceLabelRelation> placeLabelRelation;// 场所标签
    private String inforMan;// 安装人姓名
    private String inforManTel;//安装人电话
    private String principal;// 法人
    private String principalTel;//法人联系方式
    private String principalCertType;//法人证件类型
    private String principalCertCode;//法人证件号码
    private Integer status;//营业状态 1 : 装机开业在线 2 : 装机开业离线 6 : 勒令停业 10: 其他 12: 暂停营业 14: 预约安装
    private String exitIp;//外网IP
    private String authAccount;//网络认证账号或固定IP地址
    private String netType;//接入方式
    private String producerCode;//服务商代码
    private String departmentNames;

    //-------以下是大数据平台需要的字段
    private Long regionCode;
    private Long subRegionCode;
    private String phone;
    private String certCode;
    private String contactName;
    private String contactPhone;
    private Long contactCertType;
    private String contactCertCode;
    private String street;
    private String block;
    private String road;
    private String srcInfo;
    private String exInfo;
    private Date updateAt;
    private Long gridCode;
    private String placeId;
    private String terminalFactoryOrgCode;

    public String getCityArea() {
        return PlaceDeviceUtil.getCityAreaName(district);
    }

    public Integer[] getDepartmentIds() {
        if (department == null) {
            return null;
        }
        String[] deptInfo = DepartmentService.getDepartmentByLeaf(department.getId());
        String[] names = new String[deptInfo.length];
        Integer[] ids = new Integer[deptInfo.length];
        if (deptInfo.length > 0) {
            for (int i = 0; i < deptInfo.length; i++) {
                String[] info = deptInfo[i].split(",");
                ids[i] = Integer.valueOf(info[0]);
                names[i] = info[1];
            }
        }
        departmentNames = StringUtils.join(names, ",");
        return ids;
    }

    public void setLongitude(Long longitude) {
        double pow = Math.pow(10, 12);
        if (null != longitude)
            this.longitude = longitude / pow;
    }

    public void setLatitude(Long latitude) {
        double pow = Math.pow(10, 12);
        if (null != latitude)
            this.latitude = latitude / pow;
    }
}
