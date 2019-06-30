package com.jiayi.platform.basic.entity;

/**
 * 对像归属于信息
 * 像MAC地址，有MAC的 所属公司或团体的名称 及公司地址。
 * IMSI 手机卡的唯一标识，包括 所属运营商，运营商所在的地市
 *
 */
public class ObjectOrganizationInfo {

    /**
     * 所属组织名称
     */
    private String organizationName = "";

    /**
     * 归属所在地址
     */
    private String organizationAddress = "";

    /**
     * 其它信息
     */
    private String otherInfo = "";

    /**
     * 取得对像归属地址信息，
     * MAC 对像类型 返回MAC 厂家的注册地址。
     * IMSI 对像类型 返回IMSI 手机号码 所在的省-市。
     * IMEI 返回 可能为null,也可能为 手机厂家地址
     * 车牌 返回 车牌所在的省-市
     * @return
     */
    public String getOrganizationAddress() {
        return organizationAddress;
    }

    public void setOrganizationAddress(String organizationAddress) {
        this.organizationAddress = organizationAddress;
    }

    /**
     * 取得对像归属，
     * MAC 对像类型 返回MAC 所属 厂家或团体机构的名称。
     * IMSI 对像类型 返回IMSI 手机号码 所属的运营商名称如：中国移动、中国联通。
     * IMEI 返回 可能为null,也可能为 手机厂家 名称：列如 小米、华为
     * 车牌 返回 车牌所在的省-市 例如：广东-深圳
     * @return
     */
    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }


    /**
     * 其它的备注信息，
     * 列如 MAC 返回MAC 信号强度
     * @return
     */
    public String getOtherInfo() {
        return otherInfo;
    }

    public void setOtherInfo(String otherInfo) {
        this.otherInfo = otherInfo;
    }
}
