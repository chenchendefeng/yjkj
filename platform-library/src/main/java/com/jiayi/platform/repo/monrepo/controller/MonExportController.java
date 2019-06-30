package com.jiayi.platform.repo.monrepo.controller;


import com.jiayi.platform.repo.monrepo.service.MonExportService;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/monexport")
public class MonExportController {
    @Autowired
    private MonExportService monExportService;

    @GetMapping
    @ApiOperation(value = "导出常用库人员与物品列表")
    public void export(Long repoId, Integer status, HttpServletResponse response) {
        monExportService.exportMonObjectAndPerson(repoId,status, response);
    }
}
