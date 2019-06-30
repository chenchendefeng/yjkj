package com.jiayi.platform.alarm.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jiayi.platform.basic.dto.DeviceLocationDetailDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  历史版本与设备类同名 com.jiayi.common.core.report.Device TODO report模块需要
 */
public class AlarmAreaDeviceDto extends BaseObj {
    private long latitude;
    private long longitude;
    private long type;
    private long pkId;
    private long placeId;
    private Map<Long,Long> alarmAreaIdMap = new HashMap<Long,Long>();

    public AlarmAreaDeviceDto(DeviceLocationDetailDto dto){
        if (dto != null){
            setId(dto.getDeviceId());
            setLatitude(dto.getLatitude());
            setLongitude(dto.getLongitude());
            setPkId(dto.getPkId());
            setType(dto.getType());
            setPlaceId(dto.getPlaceId());
            setName(dto.getName());
        }
    }

    public AlarmAreaDeviceDto(AlarmAreaDeviceDto device){
        if (device != null){
            setId(device.getDeviceId());
            setLatitude(device.getLatitude());
            setLongitude(device.getLongitude());
            setPkId(device.getPkId());
            setType(device.getType());
            setPlaceId(device.getPlaceId());
            setName(device.getName());
        }
    }

    public AlarmAreaDeviceDto(){

    }

    public long getLatitude() {
        return latitude;
    }
    public void setLatitude(long latitude) {
        this.latitude = latitude;
    }
    public long getLongitude() {
        return longitude;
    }
    public void setLongitude(long longitude) {
        this.longitude = longitude;
    }

    public long getDeviceId() {
        return getId();
    }

    public void setType(long type) {
        this.type = type;
    }

    public long getType() {
        return type;
    }

    public long getPkId() {
        return pkId;
    }

    public void setPkId(long pkId) {
        this.pkId = pkId;
    }

    public double getFloatLatitude(){
        return  latitude / 1000000000000.0;
    }

    public double getFloatLongitude(){
        return longitude / 1000000000000.0;
    }

    public void setPlaceId(long placeId) {
        this.placeId = placeId;
    }

    public long getPlaceId() {
        return placeId;
    }

    /**
     * 增加预警区域ID
     * @param alarmAreaId
     */
    public void addAlarmAreaId(long alarmAreaId){
        if (alarmAreaId == 0){
            return ;
        }
        Long id = Long.valueOf(alarmAreaId);
        alarmAreaIdMap.put(id,id);
        return;
    }

    /**
     * 取得当前设备所有预警区域
     * @return
     */
    @JsonIgnore
    public List<Long> getAlarmAreaIds(){
        List<Long> list = new ArrayList<Long>();
        alarmAreaIdMap.forEach((key,v)->{
                    list.add(key);
                }
        );
        return list;
    }

}
