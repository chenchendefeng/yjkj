package com.jiayi.platform.basic.controller;

import com.jiayi.platform.common.web.dto.JsonObject;
import com.jiayi.platform.security.core.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    @Autowired
    private DepartmentService departmentService;

    @GetMapping("/department/flush")
    public JsonObject flushDepartment(){
        departmentService.getDepartmentTree();
        return new JsonObject("");
    }
}
