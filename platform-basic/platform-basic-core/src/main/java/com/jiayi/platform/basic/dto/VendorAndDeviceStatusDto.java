package com.jiayi.platform.basic.dto;

import com.jiayi.platform.common.exception.DBException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Calendar;
import java.util.Date;

@Getter
@Setter
@Slf4j
public class VendorAndDeviceStatusDto {
    private Integer id;//供应商id
    private String name;//供应商名字
    private long deviceId;
    private Integer deviceType;//设备子类型
    private String deviceTypeName;//设备子类型名称
    private Integer isOnline = 0;//是否在线
    private Integer isQulified = 2;//是否合格
    private Integer isOneWeek = 0;//是否7天上报
    private Integer isOneMonth = 0;//是否一月新增
    private Integer isActive=0;
    private Integer pid;
    private long placeId;
    private String srcAndCode;
    private DeviceStatusInfoDto deviceStatusInfoDto;

    public VendorAndDeviceStatusDto(Object[] obj, boolean isEnter) {
        if (obj[0] != null) {
            Date createAt = (Date) obj[0];
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - 30);
            Date before30Day = calendar.getTime();
            int i = createAt.compareTo(before30Day);
            if (i >= 0) {
                isOneMonth = 1;
            }
        }
        if(isEnter) {
            this.id = Integer.valueOf(obj[1].toString());
            this.name = obj[2].toString();
        }else{
            this.id = 0;
            this.name = "未知";
        }
        this.deviceId = Long.parseLong(obj[3] == null ? "0" : obj[3].toString());
        this.placeId = Long.parseLong(obj[4] == null ? "0" : obj[4].toString());
        if (null != obj[5])
            this.deviceType = Integer.valueOf(obj[5].toString());
        else
            this.deviceType = 0;
        if (obj.length > 6 && obj[6] != null) {
            this.deviceTypeName = obj[6].toString();
        }
        if (obj.length > 7 && obj[7] != null) {
            this.srcAndCode = obj[7].toString();
        }
    }

    public VendorAndDeviceStatusDto(Object[] obj) {
        if (obj[0] != null) {
            Date createAt = (Date) obj[0];
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - 30);
            Date before30Day = calendar.getTime();
            int i = createAt.compareTo(before30Day);
            if (i >= 0) {
                isOneMonth = 1;
            }
        }
        this.id = Integer.valueOf(obj[1].toString());
        this.name = obj[2].toString();
        this.deviceId = Long.parseLong(obj[3] == null ? "0" : obj[3].toString());
        if (null != obj[4]) {
            this.deviceType = Integer.valueOf(obj[4].toString());
        }
        if (obj.length > 5 && obj[5] != null) {
            this.pid = Integer.valueOf(obj[5].toString());
        }
        if (obj.length > 6 && obj[6] != null) {
            this.srcAndCode = obj[6].toString();
        }
    }

    public String getIdAndType() {
        return id + "|" + deviceType;
    }

    public String getIdAndOnline() {
        return id + "|" + isOnline;
    }

    public String getIdAndQulified() {
        return id + "|" + isQulified;
    }

    public String getOnlineQulified() {
        return isOnline + "" + isQulified;
    }

    public String getIdAndOneWeek() {
        return id + "|" + isOneWeek;
    }

    public String getIdAndOneMonth() {
        return id + "|" + isOneMonth;
    }

}
