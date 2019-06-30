package com.jiayi.platform.alarm.controller;

import com.jiayi.platform.alarm.dto.AlarmStrategyRequest;
import com.jiayi.platform.alarm.dto.ConditionDto;
import com.jiayi.platform.alarm.entity.AlarmStrategy;
import com.jiayi.platform.alarm.service.AlarmStrategyService;
import com.jiayi.platform.alarm.updaterepo.service.UpdateRepoService;
import com.jiayi.platform.common.vo.ModifyRemark;
import com.jiayi.platform.common.web.dto.JsonObject;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/alarmstrategy")
public class AlarmStrategyController {

	@Autowired
	private AlarmStrategyService alarmStrategyService;
	@Autowired
	private UpdateRepoService updateRepoService;

	@GetMapping(value = "/findalarmstrategy")
	@ApiOperation(value="查询布控策略")
	public JsonObject<?> findAlarmstrategy(@Valid ConditionDto conditionDto) {
		return new JsonObject<>(alarmStrategyService.findAlarmstrategy(conditionDto));
	}

	@PostMapping(value = "/add")
	@ApiOperation(value="添加布控策略")
	public JsonObject<?> add(@RequestBody @Valid AlarmStrategyRequest request) {
		return new JsonObject<>(alarmStrategyService.add(request));
	}

	@DeleteMapping(value = "/delete/{id}")
    @ApiOperation(value="删除布控策略")
	public JsonObject<?> delete(@PathVariable long id) {
		return new JsonObject<>(alarmStrategyService.delete(id));
	}

	@GetMapping(value = "/search/{id}")
    @ApiOperation(value="查询指定布控策略")
	public JsonObject<?> search(@PathVariable long id) {
		return new JsonObject<>(alarmStrategyService.findById(id));
	}

	@PostMapping(value = "/update/{id}")
    @ApiOperation(value="更新布控策略")
	public JsonObject<?> update(@PathVariable long id, @RequestBody AlarmStrategyRequest request) {
		return new JsonObject<>(alarmStrategyService.update(id, request));
	}

	@GetMapping(value = "/changestatus/{id}/{status}")
    @ApiOperation(value="更改布控策略状态")
	public JsonObject<?> changeStatus(@PathVariable long id, @PathVariable @ApiParam(value = "0禁用、1启用") int status) {
		alarmStrategyService.changeStatus(id, status);
		return new JsonObject<>("");
	}

	@PutMapping("/updateremark")
	@ApiOperation(value="修改备注")
	public JsonObject<?> modifyRemark(@RequestBody ModifyRemark modifyRemark) {
		return updateRepoService.editMysqlDescription("bkyj_alarm_strategy",modifyRemark.getRemark(),modifyRemark.getId());
	}
	// @GetMapping(value = "/strategys/{caseId}/{pageNo}/{pageSize}")
	// @ResponseBody
	// public JsonObject<?> alarmStrategys(@PathVariable String caseId,
	// @PathVariable Integer pageNo,
	// @PathVariable Integer pageSize) {
	// return alarmStrategyService.findAll(caseId, pageNo, pageSize);
	// }
}
