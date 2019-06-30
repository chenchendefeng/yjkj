package com.jiayi.platform.basic.controller;


import com.jiayi.platform.basic.request.DeviceRequest;
import com.jiayi.platform.basic.request.DeviceSearchRequest;
import com.jiayi.platform.basic.serviceImpl.DeviceServiceImpl;
import com.jiayi.platform.common.web.dto.JsonObject;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/devices")
public class DeviceController {

    @Autowired
    private DeviceServiceImpl deviceService;

    /**
     * 设备管理
     */
    @GetMapping
    @ApiOperation(value = "设备列表")
    public JsonObject<?> findAll(DeviceSearchRequest deviceSearchRequest) {
        return new JsonObject<>(deviceService.findAllDevice(deviceSearchRequest));
    }

    @PostMapping
    @ApiOperation(value = "添加设备")
    public JsonObject<?> add(@RequestBody @Valid DeviceRequest deviceRequest) {
        return new JsonObject<>(deviceService.addDevice(deviceRequest));
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除设备")
    public JsonObject<?> delete(@PathVariable Long id) {
        deviceService.deleteDevice(id);
        return new JsonObject<>("");
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "修改设备")
    public JsonObject<?> modify(@PathVariable Long id, @RequestBody DeviceRequest deviceRequest) {
    	deviceService.updateDevice(id, deviceRequest);
        return new JsonObject<>("");
    }

    @GetMapping("/download")
    public void download(DeviceSearchRequest request, HttpServletResponse response) {
//    	String token = request.getHeader("Authorization");
//		Long userId = Long.valueOf(JWTUtil.getUserId(token));
        deviceService.downloadDevices(request, response);
    }

    @GetMapping("/{id}")
    public JsonObject<?> findOneDevice(@PathVariable Long id) {
        return new JsonObject<>(deviceService.findByPkId(id));
    }

    @GetMapping("/findOneBySrcAndCode")
    public JsonObject<?> findOneBySrcAndCode(String src, String code){
        return new JsonObject<>(deviceService.findOneBySrcAndCode(src, code));
    }


    @GetMapping(value = "/export")
    @ApiOperation(value = "导出设备详情列表")
    public void exportDevices(DeviceSearchRequest request, HttpServletResponse response) {
        deviceService.exportDevices(request, response);
    }

    @ApiOperation(value = "上传文件解析")
    @PostMapping(value = "/upload")
    public JsonObject<?> upload(@RequestParam("file") MultipartFile file) {
        return new JsonObject<>(deviceService.uploadAndParse(file));
    }

    @ApiOperation(value = "设备导入")
    @GetMapping(value = "/import")
    public JsonObject<?> importCsvFile(String fileName) {
        deviceService.importCsvFile(fileName);
        return new JsonObject<>("");
    }

    @ApiOperation(value = "设备统计")
    @GetMapping("/deviceCount")
    public JsonObject<?> deviceCount() {
        return new JsonObject<>(deviceService.countPlaceAndDevice());
    }


    @ApiOperation(value = "修改设备状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备ID（pkId) ", dataType = "int",required = true) ,
            @ApiImplicitParam(name = "status", value = "状态 (0:删除 | 1:正常 | 2:暂停)", dataType = "int",required = true)
    })
    @PostMapping("/changeStatus")
    public JsonObject<?> changeDeviceStatus(Long deviceId,Integer status) {
        this.deviceService.changeDeviceStatus(deviceId,status);
        return new JsonObject<>("");
    }


}
