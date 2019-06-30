package com.jiayi.platform.alarm.controller;

import com.jiayi.platform.alarm.dto.AlarmDistrictRequest;
import com.jiayi.platform.alarm.dto.AlarmDistrictSearchVo;
import com.jiayi.platform.alarm.service.AlarmDistrictService;
import com.jiayi.platform.alarm.updaterepo.service.UpdateRepoService;
import com.jiayi.platform.common.vo.ModifyRemark;
import com.jiayi.platform.common.web.dto.JsonObject;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/alarmdistrict")
public class AlarmDistrictController {

    @Autowired
    private AlarmDistrictService alarmDistrictService;
    @Autowired
    private UpdateRepoService updateRepoService;

    @GetMapping
    @ApiOperation(value = "查询地域布控")
    public JsonObject<?> find(AlarmDistrictSearchVo alarmDistrictSearchVo) {
        return new JsonObject<>(alarmDistrictService.findAlarmDistrictList(alarmDistrictSearchVo));
    }

    @PostMapping
    @ApiOperation(value = "添加地域布控")
    public JsonObject<?> add(@RequestBody @Valid AlarmDistrictRequest request) {
        return new JsonObject<>(alarmDistrictService.addAlarmDistrict(request));
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "修改地域布控")
    public JsonObject<?> modify(@PathVariable Long id, @RequestBody @Valid AlarmDistrictRequest request) {
        return new JsonObject<>(alarmDistrictService.updateAlarmDistrict(id, request));
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除地域布控")
    public JsonObject<?> delete(@PathVariable Long id) {
        alarmDistrictService.deleteAlarmDistrict(id);
        return new JsonObject<>("");
    }

    @GetMapping(value = "/city")
    @ApiOperation(value = "获取省市的城市信息")
    public JsonObject<?> city() {
        return new JsonObject<>(alarmDistrictService.getCity());
    }

    @GetMapping(value = "/modifystatus/{id}/{status}")
    @ApiOperation(value = "修改布控状态")
    public JsonObject<?> modifyStatus(@PathVariable long id, @PathVariable @ApiParam(value = "0禁用,1启用") int status){
        alarmDistrictService.modifyStatusById(id, status);
        return new JsonObject<>("");
    }

    @PutMapping("/updateremark")
    @ApiOperation(value = "修改备注")
    public JsonObject<?> modifyRemark(@RequestBody ModifyRemark modifyRemark) {
        return updateRepoService.editMysqlDescription("bkyj_alarm_district",modifyRemark.getRemark(),modifyRemark.getId());
    }
}
