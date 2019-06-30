package com.jiayi.platform.basic.controller;

import com.jiayi.platform.basic.request.DataDistributionRequest;
import com.jiayi.platform.basic.serviceImpl.DataDistributionService;
import com.jiayi.platform.common.web.dto.JsonObject;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/datadistributions")
public class DataDistributionController {

	@Autowired
	private DataDistributionService dataDistributionService;

	@GetMapping
	@ApiOperation(value = "查询数据分发列表")
	public JsonObject<?> search(Integer page, Integer size){
		return new JsonObject<>(dataDistributionService.search(page, size));
	}

	@PostMapping
	@ApiOperation(value = "添加数据分发")
	public JsonObject<?> add(@RequestBody @Valid DataDistributionRequest request){
		return new JsonObject<>(dataDistributionService.add(request));
	}

	@DeleteMapping("/{id}")
	@ApiOperation("删除数据分发")
	public JsonObject<?> delete(@PathVariable Integer id){
		dataDistributionService.delete(id);
		return new JsonObject<>("");
	}

	@PutMapping("/{id}")
	@ApiOperation(value = "修改数据分发")
	public JsonObject<?> modify(@PathVariable Integer id, @RequestBody DataDistributionRequest request){
		return new JsonObject<>(dataDistributionService.modify(id, request));
	}
}
