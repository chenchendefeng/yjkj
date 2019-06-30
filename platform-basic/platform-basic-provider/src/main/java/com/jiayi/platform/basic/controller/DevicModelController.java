package com.jiayi.platform.basic.controller;

import com.jiayi.platform.basic.request.DeviceModelRequest;
import com.jiayi.platform.basic.serviceImpl.DeviceModelService;
import com.jiayi.platform.common.web.dto.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/deviceModels")
public class DevicModelController {
    @Autowired
    private DeviceModelService deviceModelService;

    @GetMapping
    public JsonObject<?> findAllDeviceModels(Integer page, Integer size) {
        return new JsonObject<>(deviceModelService.findAll(page, size));
    }

    @PostMapping
    public JsonObject<?> addDeviceModels(@RequestBody DeviceModelRequest deviceModelRequest) {
        return new JsonObject<>(deviceModelService.add(deviceModelRequest));
    }

    @PutMapping("/{id}")
    public JsonObject<?> modifyDeviceModels(@PathVariable int id, @RequestBody DeviceModelRequest deviceModelRequest) {
        deviceModelService.modify(id, deviceModelRequest);
        return new JsonObject<>("");
    }

    @DeleteMapping("/{id}")
    public JsonObject<?> deleteDeviceModel(@PathVariable int id) {
        deviceModelService.delete(id);
        return new JsonObject<>("");
    }

    @GetMapping("/{id}")
    public JsonObject<?> findDeviceModel(@PathVariable int id) {
        return new JsonObject<>(deviceModelService.findOne(id));
    }
}
