package com.jiayi.platform.basic.controller;

import com.jiayi.platform.basic.dto.DistrictDeviceDto;
import com.jiayi.platform.basic.dto.FaultDeviceDto;
import com.jiayi.platform.basic.dto.FaultDevicePageDto;
import com.jiayi.platform.basic.dto.VendorDeviceDto;
import com.jiayi.platform.basic.request.DeviceStatSearchRequest;
import com.jiayi.platform.basic.request.FaultDeviceRequest;
import com.jiayi.platform.basic.serviceImpl.DeviceCountService;
import com.jiayi.platform.common.web.dto.JsonObject;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/devicecounts")
public class DeviceCountController {
    @Autowired
    private DeviceCountService deviceCountService;

    @PostMapping("/district")
    @ApiOperation(value = "按行政区划分")
    public JsonObject<List<DistrictDeviceDto>> findByDistrict(@RequestBody DeviceStatSearchRequest deviceStatSearchRequest) {
        return new JsonObject<>(deviceCountService.getDeviceByCity(deviceStatSearchRequest));
    }
    @PostMapping("/expDistrict")
    @ApiOperation(value = "按行政区划分数据导出")
    public void exportDeviceByDistrict(@RequestBody DeviceStatSearchRequest DeviceStatSearchRequest, HttpServletResponse resp) {
        deviceCountService.exportDeviceByDistrict(DeviceStatSearchRequest,resp);
    }

    @PostMapping("/vendor")
    @ApiOperation(value = "按供应商划分")
    public JsonObject<List<VendorDeviceDto>> findByVendor(@RequestBody DeviceStatSearchRequest deviceStatSearchRequest) {
        return new JsonObject<>(deviceCountService.getDeviceByVender(deviceStatSearchRequest));
    }

    @PostMapping("/expVendor")
    @ApiOperation(value = "按供应商划分数据导出")
    public void exportDeviceByVendor(@RequestBody DeviceStatSearchRequest deviceStatSearchRequest, HttpServletResponse resp) {
        deviceCountService.exportDeviceByVender(deviceStatSearchRequest,resp);
    }

    @PostMapping("/department")
    @ApiOperation(value = "按部门划分")
    public JsonObject<?> findByDepartment(@RequestBody DeviceStatSearchRequest deviceStatSearchRequest) {
        return new JsonObject<>(deviceCountService.getDeviceByDepartment(deviceStatSearchRequest));
    }

    @PostMapping("/expDepartment")
    @ApiOperation(value = "按部门划分数据导出")
    public void exportDeviceByDepartment(@RequestBody DeviceStatSearchRequest deviceStatSearchRequest, HttpServletResponse resp) {
        deviceCountService.exportDeviceByDepartment(deviceStatSearchRequest,resp);
    }

    @PostMapping("/fault")
    @ApiOperation(value = "故障设备统计")
    public JsonObject<FaultDevicePageDto<FaultDeviceDto>> statisticFaultDevice(@RequestBody FaultDeviceRequest faultDeviceRequest) {
        return new JsonObject<>(deviceCountService.getFaultDeviceList(faultDeviceRequest));
    }

}
