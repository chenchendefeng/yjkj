package com.jiayi.platform.alarm.controller;

import com.jiayi.platform.alarm.dto.*;
import com.jiayi.platform.alarm.service.AlarmAreaService;
import com.jiayi.platform.common.web.dto.JsonObject;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alarmarea")
public class AlarmAreaController {
    @Autowired
    private AlarmAreaService alarmAreaService;


    @PostMapping(value = "/updateAlarmConfig")
    @ApiOperation(value = "修改布控预警设置")
    public JsonObject<?> updateAlarmConfig(@RequestBody AlarmConfRequest request) {
        alarmAreaService.modConfig(request);
        return new JsonObject<>("");
    }

    @PostMapping(value = "/findAlarmConfig")
    @ApiOperation(value = "获取布控预警设置")
    public JsonObject<?> findAlarmConfig() {
        return new JsonObject<>(alarmAreaService.findAlarmConfig());
    }

    @PostMapping(value = "/add")
    @ApiOperation(value = "添加布控预警区域")
    public JsonObject<?> addAlarmArea(@RequestBody AlarmAreaRequest request) {
        return new JsonObject<>(alarmAreaService.add(request));
    }

    @PostMapping(value = "/addSub")
    @ApiOperation(value = "添加布控预警子区域")
    public JsonObject<?> addAlarmAreaSub(@RequestBody AlarmAreaRequest request) {
        return new JsonObject<>(alarmAreaService.add(request));
    }

    @DeleteMapping(value = "/delete/{id}")
    @ApiOperation(value = "删除布控预警区域")
    public JsonObject<?> deleteAlarmArea(@PathVariable Long id) {
        alarmAreaService.delete(id);
        return new JsonObject<>("");
    }

    @DeleteMapping(value = "/deleteSub/{id}")
    @ApiOperation(value = "删除布控预警子区域")
    public JsonObject<?> deleteAlarmAreaSub(@PathVariable Long id) {
        alarmAreaService.delete(id);
        return new JsonObject<>("");
    }


    @PostMapping(value = "/update/{id}")
    @ApiOperation(value = "修改布控预警区域")
    public JsonObject<?> updateAlarmArea(@PathVariable Long id, @RequestBody AlarmAreaRequest request) {
        return new JsonObject<>(alarmAreaService.update(id, request));
    }

    @PostMapping(value = "/updateSub/{id}")
    @ApiOperation(value = "修改布控预警子区域")
    public JsonObject<?> updateAlarmAreaSub(@PathVariable Long id, @RequestBody AlarmAreaRequest request) {
        return new JsonObject<>(alarmAreaService.update(id, request));
    }


    @PostMapping(value = "/batchUpdate/")
    @ApiOperation(value = "批量修改布控预警区域")
    public JsonObject<?> batchUpdateAlarmArea(@RequestBody AlarmBatchModRequest request) {
        return new JsonObject<>(alarmAreaService.batchUpdate(request));
    }

    @PostMapping(value = "/list")
    @ApiOperation(value = "显示布控预警区域列表:areaId=0所有父区域，传父区域id时，返回该父区域下的所以子区域")
    public JsonObject<?> listAlarmArea(@RequestBody AlarmAreaQueryRequest request) {
        return new JsonObject<>(alarmAreaService.list(request));
    }

    @PostMapping(value = "/listSub")
    @ApiOperation(value = "显示布控预警子区域列表:areaId=0所有父区域，传父区域id时，返回该父区域下的所以子区域")
    public JsonObject<?> listAlarmAreaSub(@RequestBody AlarmAreaQueryRequest request) {
        return new JsonObject<>(alarmAreaService.list(request));
    }

    @PostMapping(value = "/devicesinareas")
    @ApiOperation(value = "返回区域内的设备id集")
    public JsonObject<?> getDevicesInAreas(@RequestBody List<Long> areaIds) {
        return new JsonObject<>(alarmAreaService.getDevicesInAreas(areaIds));
    }

    @PostMapping(value = "/adjustlocation")
    @ResponseBody
    @ApiOperation(value = "更改设备点位置")
    //FIXME 不能用设备id进行修改，需要用pkId
    public JsonObject<?> adjustDeviceLocation(@RequestBody DeviceLocation deviceLocation) {
        alarmAreaService.adjustDeviceLocation(deviceLocation);
        return new JsonObject<>("");
    }

    @PostMapping(value = "/getadjusted")
    @ApiOperation(value = "返回调整后的设备位置,deviceType:1为mac,2为imsi,4为carno")
    public JsonObject<?> getAdjustedDeviceById(String areaId,Integer deviceType) {
        return alarmAreaService.getAdjustedDevices(areaId == null ? null : Long.parseLong(areaId),deviceType);
    }
}
