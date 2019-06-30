package com.jiayi.platform.basic.controller;

import com.jiayi.platform.basic.request.DeviceMapRequest;
import com.jiayi.platform.basic.serviceImpl.DeviceMapService;
import com.jiayi.platform.basic.vo.DeviceVo;
import com.jiayi.platform.common.web.dto.JsonObject;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/map")
public class DeviceMapController {

    @Autowired
    private DeviceMapService deviceMapService;

    @ApiOperation(value = "按设备采集数据类型查询设备")
    @GetMapping(value = "/devices/{userId}/{type}")
    public JsonObject<?> getByCollectType(@ApiParam(name = "userId", value = "用户id", required = true) @PathVariable Long userId,
                                          @ApiParam(name = "type", value = "0:ALL,1:mac,2:car,3:imsi,4:imei", required = true) @PathVariable Integer type) {
        return new JsonObject<>(deviceMapService.queryAllDevice(userId, type));
    }

    @ApiOperation(value = "按设备类型查询设备")
    @PostMapping(value = "/getbydevicetype")
    public JsonObject<?> getByDeviceSubType(@RequestBody DeviceMapRequest request) {
        return new JsonObject<>(deviceMapService.queryByDeviceSubType(request));
    }

    @ApiOperation(value = "高级搜索中的地图设备点(审计数据类型的设备)")
    @GetMapping("/auditdevices")
    public JsonObject<List<DeviceVo>> searchAuditDevices(){
        return new JsonObject<>(deviceMapService.searchAuditDevices());
    }
}
