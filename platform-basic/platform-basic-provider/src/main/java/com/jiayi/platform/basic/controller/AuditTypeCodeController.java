package com.jiayi.platform.basic.controller;

import com.jiayi.platform.basic.serviceImpl.AuditTypeCodeServiceImpl;
import com.jiayi.platform.common.web.dto.JsonObject;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auditTypeCode")
public class AuditTypeCodeController {

    @Autowired
    private AuditTypeCodeServiceImpl auditTypeCodeService;

    @GetMapping("/tree")
    @ApiOperation(value = "审计数据类型")
    public JsonObject tree(@ApiParam(name = "type", value = "BBS,EMAIL,FILE_TRANSFER,INTERNET,SEARCH,VIRTUAL", required = true)String type){
        return new JsonObject(auditTypeCodeService.tree(type));
    }
}
