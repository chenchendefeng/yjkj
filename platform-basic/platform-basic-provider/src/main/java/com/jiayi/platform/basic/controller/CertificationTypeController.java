package com.jiayi.platform.basic.controller;

import com.jiayi.platform.basic.serviceImpl.CertificationTypeServiceImpl;
import com.jiayi.platform.common.web.dto.JsonObject;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/certificationtype")
public class CertificationTypeController {

    @Autowired
    private CertificationTypeServiceImpl certificationTypeService;

    @GetMapping
    @ApiOperation(value = "认证类型")
    public JsonObject<?> findAll(){
        return new JsonObject<>(certificationTypeService.findAll());
    }
}
