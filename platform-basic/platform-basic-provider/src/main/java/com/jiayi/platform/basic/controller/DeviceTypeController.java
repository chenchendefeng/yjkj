package com.jiayi.platform.basic.controller;

import com.jiayi.platform.basic.serviceImpl.DeviceTypeService;
import com.jiayi.platform.common.web.dto.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/deviceParentTypes")
public class DeviceTypeController {

    @Autowired
    private DeviceTypeService deviceTypeService;

    @GetMapping
    public JsonObject<?> findAllDeviceTypes() {
        return new JsonObject<>(deviceTypeService.findAll());
    }

//    @GetMapping
//    public JsonObject<?> findAllDeviceTypes(Integer page, Integer size) {
//        return new JsonObject<>(deviceTypeService.findAll(page, size));
//    }
//
//    @PostMapping
//    public JsonObject<?> addDeviceTypes(@RequestBody DeviceTypeRequest deviceTypeRequest) {
//        return new JsonObject<>(deviceTypeService.add(deviceTypeRequest));
//    }
//
//    @PutMapping("/{id}")
//    public JsonObject<?> modifyDeviceTypes(@PathVariable int id, @RequestBody DeviceTypeRequest deviceTypeRequest) {
//        deviceTypeService.modify(id, deviceTypeRequest);
//        return new JsonObject<>("");
//    }
//
//    @DeleteMapping("/{id}")
//    public JsonObject<?> deleteDeviceType(@PathVariable int id) {
//        deviceTypeService.delete(id);
//        return new JsonObject<>("");
//    }
//
//    @GetMapping("/{id}")
//    public JsonObject<?> findDeviceType(@PathVariable int id) {
//        return new JsonObject<>(deviceTypeService.findOne(id));
//    }

//    @PutMapping("/updateremark")
//    public JsonObject<?> modifyRemark(@RequestBody ModifyRemark modifyRemark) {
//        deviceTypeService.modifyRemark(modifyRemark);
//        return new JsonObject<>("");
//    }

    @GetMapping("tree")
    public JsonObject tree(){
        return new JsonObject(deviceTypeService.tree());
    }
}
