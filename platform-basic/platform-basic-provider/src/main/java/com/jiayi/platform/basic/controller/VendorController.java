package com.jiayi.platform.basic.controller;

import javax.validation.Valid;

import com.jiayi.platform.basic.serviceImpl.VendorService;
import com.jiayi.platform.basic.request.VendorRequest;
import com.jiayi.platform.basic.request.VendorSearchRequest;
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
@RequestMapping("/vendor")
public class VendorController {

    @Autowired
    private VendorService vendorService;

    @ApiOperation(value = "查询供应商")
    @GetMapping
    public JsonObject<?> findAllVendor(VendorSearchRequest vendorSearchRequest) {
        return new JsonObject<>(vendorService.findAllVendor(vendorSearchRequest));
    }

    @ApiOperation(value = "添加供应商")
    @PostMapping
    public JsonObject<?> add(@RequestBody @Valid VendorRequest vendorRequest) {
        return new JsonObject<>(vendorService.addVendor(vendorRequest));
    }

    @ApiOperation(value = "删除供应商")
    @DeleteMapping("/{id}")
    public JsonObject<?> delete(@PathVariable Integer id) {
        vendorService.deleteVendor(id);
        return new JsonObject<>("");
    }

    @ApiOperation(value = "修改供应商")
    @PutMapping("/{id}")
    public JsonObject<?> modify(@PathVariable Integer id, @RequestBody VendorRequest vendorRequest) {
        vendorService.updateVendor(id, vendorRequest);
        return new JsonObject<>("");
    }

    @ApiOperation(value = "获取所有供应商")
    @GetMapping("/all")
    public JsonObject<?> findAllVendor() {
        return new JsonObject<>(vendorService.findAll());
    }

    @PutMapping("/updateremark")
    @ApiOperation(value = "修改供应商备注")
    public JsonObject<?> modifyRemark(@RequestBody ModifyRemark modifyRemark) {
        vendorService.updateVendorExInfo(modifyRemark.getId(), modifyRemark.getRemark());
        return new JsonObject<>("");
    }
}
