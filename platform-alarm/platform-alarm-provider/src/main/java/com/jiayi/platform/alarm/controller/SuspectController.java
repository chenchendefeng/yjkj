package com.jiayi.platform.alarm.controller;

import com.jiayi.platform.alarm.dto.ConditionDto;
import com.jiayi.platform.alarm.dto.SuspectsRequest;
import com.jiayi.platform.alarm.entity.Suspects;
import com.jiayi.platform.alarm.service.SuspectsService;
import com.jiayi.platform.alarm.updaterepo.service.UpdateRepoService;
import com.jiayi.platform.common.vo.ModifyRemark;
import com.jiayi.platform.common.web.dto.JsonObject;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/suspects")
public class SuspectController {

    @Autowired
    private SuspectsService suspectService;
    @Autowired
    private UpdateRepoService updateRepoService;

    @GetMapping(value = "/findsuspects")
    @ApiOperation(value = "查询布控人员")
    public JsonObject<?> findSuspects(@Valid ConditionDto conditionDto) {
        return new JsonObject<>(suspectService.findSuspects(conditionDto));
    }

    @PostMapping(value = "/add")
    @ApiOperation(value = "添加布控人员")
    public JsonObject<Suspects> add(@RequestBody @Valid SuspectsRequest request) {
        return new JsonObject<>(suspectService.addSuspect(request));
    }

    @DeleteMapping(value = "/delete/{id}")
    @ApiOperation(value = "删除布控人员")
    public JsonObject delete(@PathVariable long id) {
        return new JsonObject<>(suspectService.deleteSuspect(id));
    }

    @GetMapping(value = "/search/{id}")
    @ApiOperation(value = "查询指定布控人员")
    public JsonObject<Suspects> findByIds(@PathVariable long id) {
        return new JsonObject<>(suspectService.findSuspectById(id));
    }

    @PostMapping(value = "/update/{id}")
    @ApiOperation(value = "更新布控人员")
    public JsonObject<Suspects> update(@PathVariable long id, @RequestBody SuspectsRequest request) {
        return new JsonObject<>(suspectService.updateSuspect(id, request));
    }

    @PutMapping("/updateremark")
    @ApiOperation(value = "修改备注")
    public JsonObject<?> modifyRemark(@RequestBody ModifyRemark modifyRemark) {
        return updateRepoService.editMysqlDescription("bkyj_suspects", modifyRemark.getRemark(), modifyRemark.getId());
    }
}
