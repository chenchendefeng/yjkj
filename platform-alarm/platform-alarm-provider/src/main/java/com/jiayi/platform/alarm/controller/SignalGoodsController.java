package com.jiayi.platform.alarm.controller;

import com.jiayi.platform.alarm.dto.ConditionDto;
import com.jiayi.platform.alarm.dto.SignalGoodsRequest;
import com.jiayi.platform.alarm.entity.SignalGoods;
import com.jiayi.platform.alarm.service.SignalGoodsService;
import com.jiayi.platform.alarm.updaterepo.service.UpdateRepoService;
import com.jiayi.platform.common.vo.ModifyRemark;
import com.jiayi.platform.common.web.dto.JsonObject;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/signalgoods")
public class SignalGoodsController {

    @Autowired
    private SignalGoodsService signalGoodsService;
    @Autowired
    private UpdateRepoService updateRepoService;

    @GetMapping(value = "/findsignalgoods")
    @ApiOperation(value = "查询布控物品")
    public JsonObject<?> findSignalgoods(@Valid ConditionDto signalGoodsDto) {
        return new JsonObject<>(signalGoodsService.findSignalGoods(signalGoodsDto));
    }

    @PostMapping(value = "/add")
    @ApiOperation(value = "添加布控物品")
    public JsonObject<?> add(@RequestBody @Valid SignalGoodsRequest request) {
        return new JsonObject<>(signalGoodsService.addGoods(request));
    }

    @DeleteMapping(value = "/delete/{id}")
    @ApiOperation(value = "删除布控物品")
    public JsonObject<?> delete(@PathVariable long id) {
        return new JsonObject<>(signalGoodsService.deleteGoods(id));
    }

    @GetMapping(value = "/search/{id}")
    @ApiOperation(value = "查询指定布控物品")
    public JsonObject<SignalGoods> findByIds(@PathVariable long id) {
        return new JsonObject<>(signalGoodsService.findById(id));
    }

    @PostMapping(value = "/update/{id}")
    @ApiOperation(value = "更新布控物品")
    public JsonObject<?> update(@PathVariable long id, @RequestBody @Valid SignalGoodsRequest request) {
        return new JsonObject<>(signalGoodsService.updateGoods(id, request));
    }

//    @GetMapping(value = "/beactive/{id}/{status}")
//    @ApiOperation(value = "更改物品布控状态")
//    public JsonObject<SignalGoods> beActive(@PathVariable long id, @PathVariable int status) {
//        return new JsonObject<>(signalGoodsService.beActive(id, status));
//    }

    @GetMapping(value = "/updatestatus/{id}/{status}")
    @ApiOperation(value = "更改物品布控状态")
    public JsonObject<SignalGoods> updateStatus(@PathVariable long id, @PathVariable @ApiParam(value = "0禁用,1启用") int status) {
        return new JsonObject<>(signalGoodsService.updateStatus(id, status));
    }

    @PutMapping("/updateremark")
    @ApiOperation(value = "修改备注")
    public JsonObject<?> modifyRemark(@RequestBody ModifyRemark modifyRemark) {
        return updateRepoService.editMysqlDescription("bkyj_goods_management", modifyRemark.getRemark(), modifyRemark.getId());
    }
}
