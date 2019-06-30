package com.jiayi.platform.basic.controller;

import com.jiayi.platform.basic.serviceImpl.DeviceSubTypeService;
import com.jiayi.platform.basic.request.DeviceSubTypeRequest;
import com.jiayi.platform.common.vo.ModifyRemark;
import com.jiayi.platform.common.web.dto.JsonObject;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/devicetypes")
public class DeviceSubTypeController {

    @Autowired
    private DeviceSubTypeService deviceTypeService;

    @GetMapping
    public JsonObject<?> findAllDeviceSubTypes(Integer page, Integer size) {
        return new JsonObject<>(deviceTypeService.findAll(page, size));
    }

    @PostMapping
    public JsonObject<?> addDeviceSubTypes(@RequestBody DeviceSubTypeRequest deviceTypeRequest) {
        return new JsonObject<>(deviceTypeService.add(deviceTypeRequest));
    }

    @PutMapping("/{id}")
    public JsonObject<?> modifyDeviceSubTypes(@PathVariable int id, @RequestBody DeviceSubTypeRequest deviceTypeRequest) {
        deviceTypeService.modify(id, deviceTypeRequest);
        return new JsonObject<>("");
    }

    @DeleteMapping("/{id}")
    public JsonObject<?> deleteDeviceSubType(@PathVariable int id) {
        deviceTypeService.delete(id);
        return new JsonObject<>("");
    }

    @GetMapping("/{id}")
    public JsonObject<?> findDeviceSubType(@PathVariable int id) {
        return new JsonObject<>(deviceTypeService.findOne(id));
    }

    @PutMapping("/updateremark")
    public JsonObject<?> modifyRemark(@RequestBody ModifyRemark modifyRemark) {
        deviceTypeService.modifyRemark(modifyRemark);
        return new JsonObject<>("");
    }

    @ApiOperation(value = "获取所有设备子类型")
    @GetMapping("/all")
    public JsonObject<?> findAllType() {
        return new JsonObject<>(deviceTypeService.findAll());
    }

    @ApiOperation(value = "获取所有设备类型与对应型号")
    @GetMapping("/findall")
    public JsonObject<?> findAllTypeAndModel() {
        return new JsonObject<>(deviceTypeService.findAllType());
    }

    @ApiOperation(value = "获取所有采集数据类型")
    @GetMapping("/collects")
    public JsonObject<?> findAllCollects() {
        return new JsonObject<>(deviceTypeService.getAllCollect());
    }
}
