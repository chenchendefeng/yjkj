package com.jiayi.platform.repo.monrepo.controller;


import com.jiayi.platform.repo.monrepo.dto.MonPageDto;
import com.jiayi.platform.repo.monrepo.dto.MonRepoDto;
import com.jiayi.platform.repo.monrepo.entity.MonitorRepo;
import com.jiayi.platform.repo.monrepo.service.MonRepoService;
import com.jiayi.platform.repo.monrepo.vo.MonRepoRequest;
import com.jiayi.platform.repo.monrepo.vo.MonRepoSearchVo;
import com.jiayi.platform.security.core.dto.JsonObject;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping ("/monrepo")
public class MonRepoController {
    @Autowired
    private MonRepoService monRepoService;

    @GetMapping
    @ApiOperation(value = "显示常用库列表")
    public JsonObject<MonPageDto<MonRepoDto>> find(MonRepoSearchVo monRepoSearchVo){
        return new JsonObject<>(monRepoService.findMonRepoList(monRepoSearchVo));
    }

    @PostMapping
    @ApiOperation(value = "添加常用库")
    public JsonObject<MonitorRepo> add(@RequestBody @Valid MonRepoRequest monRepoRequest) {
        return new JsonObject<>(monRepoService.addMonRepo(monRepoRequest));
    }

//    @DeleteMapping("/{id}")
//    @Loggable(desc="删除常用库")
//    @RequiresPermissions(ResourceCode.MONREPO_DELETE)
//    public JsonObject<?> delete(@PathVariable Long id) {
//        monRepoService.deleteMonRepo(id);
//        return new JsonObject<>("");
//    }

    @PutMapping("/{id}")
    @ApiOperation(value = "修改常用库")
    public JsonObject<MonitorRepo> modify(@PathVariable Long id, @RequestBody @Valid MonRepoRequest monRepoRequest) {
        return new JsonObject<>(monRepoService.modifyMonRepo(id, monRepoRequest));
    }

}
