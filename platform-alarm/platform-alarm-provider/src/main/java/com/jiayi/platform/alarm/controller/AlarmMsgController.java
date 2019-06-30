package com.jiayi.platform.alarm.controller;

import com.jiayi.platform.alarm.dto.AlarmCaseMsgDto;
import com.jiayi.platform.alarm.dto.AlarmCaseMsgSearchVo;
import com.jiayi.platform.alarm.dto.AlarmDistrictMsgDto;
import com.jiayi.platform.alarm.dto.AlarmDistrictMsgSearchVo;
import com.jiayi.platform.alarm.service.AlarmMsgService;
import com.jiayi.platform.common.web.dto.JsonObject;
import com.jiayi.platform.common.web.dto.PageResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/alarmMsgNew")
public class AlarmMsgController {

    @Autowired
    private AlarmMsgService alarmMsgService;

    @GetMapping("/caseAlarmMsg")
    @ApiOperation(value = "查询案件监测预警消息")
    public JsonObject<PageResult<AlarmCaseMsgDto>> findCaseAlarmMsg(@Valid AlarmCaseMsgSearchVo searchVo){
        return new JsonObject(alarmMsgService.findCaseAlarmMsg(searchVo));
    }

//    @PostMapping("/updatAlarmMsg")
//    @ApiOperation(value = "更新预警消息")
//    public JsonObject<?> updateAlarmMsg(@RequestBody List<Long> ids) {
//        alarmMsgService.updateAlarmMsg(ids);
//        return new JsonObject<>("");
//    }

    @PutMapping("/{id}")
    @ApiOperation(value = "修改预警消息已读状态")
    public JsonObject<?> update(@PathVariable Long id){
        alarmMsgService.updateAlarmMsg(id);
        return new JsonObject<>("");
    }

    @GetMapping("/districtAlarmMsg")
    @ApiOperation(value = "查询地域布控预警消息")
    public JsonObject<PageResult<AlarmDistrictMsgDto>> findDistrictAlarmMsg(AlarmDistrictMsgSearchVo searchVo){
        return new JsonObject(alarmMsgService.findDistrictAlarmMsg(searchVo));
    }

    @PostMapping(value = "/recentAlarmMsgs")
    @ApiOperation(value="监控平台查询最近预警消息")
    public JsonObject<?> getRecentAlarmMsgs(Long areaId) {
        JsonObject<?> alarmMsgs = new JsonObject<>(alarmMsgService.getRecentAlarmMsgs(areaId));
        return alarmMsgs;
    }

}
