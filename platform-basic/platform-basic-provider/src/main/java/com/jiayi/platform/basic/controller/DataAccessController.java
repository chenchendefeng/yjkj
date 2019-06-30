package com.jiayi.platform.basic.controller;

import com.jiayi.platform.basic.request.DataAccessRequest;
import com.jiayi.platform.basic.serviceImpl.DataAccessService;
import com.jiayi.platform.common.web.dto.JsonObject;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/dataaccesses")
public class DataAccessController {

	@Autowired
	private DataAccessService dataAccessService;

	@GetMapping
	@ApiOperation(value = "查询数据接入列表")
	public JsonObject<?> search(Integer page, Integer size){
		return new JsonObject<>(dataAccessService.search(page, size));
	}

	@PostMapping
	@ApiOperation(value = "添加数据接入")
	public JsonObject<?> add(@RequestBody @Valid DataAccessRequest request){
		return new JsonObject<>(dataAccessService.add(request));
	}

	@DeleteMapping("/{id}")
	@ApiOperation(value = "删除数据接入")
	public JsonObject<?> delete(@PathVariable Integer id){
		dataAccessService.delete(id);
		return new JsonObject<>("");
	}

	@PutMapping("/{id}")
	@ApiOperation(value = "修改数据接入")
	public JsonObject<?> modify(@PathVariable Integer id, @RequestBody @Valid DataAccessRequest request){
		return new JsonObject<>(dataAccessService.modify(id, request));
	}
}
