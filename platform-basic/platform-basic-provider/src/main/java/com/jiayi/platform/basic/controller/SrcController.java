package com.jiayi.platform.basic.controller;

import com.jiayi.platform.basic.serviceImpl.SrcService;
import com.jiayi.platform.basic.request.SrcRequest;
import com.jiayi.platform.common.web.dto.JsonObject;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/datasources")
public class SrcController {

	@Autowired
	private SrcService srcService;

	@GetMapping
	@ApiOperation(value = "查询数据源列表")
	public JsonObject<?> findAllSrc(Integer page, Integer size) {
		return new JsonObject<>(srcService.findAllSrc(page, size));
	}

	@GetMapping("/findsrcbytype/{dataType}")
	@ApiOperation(value = "数据接入/分发中数据源下拉列表,dataType：1接入，2分发")
	public JsonObject<?> findSrcByType(@PathVariable Integer dataType) {
		return new JsonObject<>(srcService.findSrcByType(dataType));
	}

	@PostMapping
	@ApiOperation(value = "添加数据源")
	public JsonObject<?> addSrc(@RequestBody @Valid SrcRequest srcRequest) {
		return new JsonObject<>(srcService.addSrc(srcRequest));
	}

	@DeleteMapping("/{id}")
	@ApiOperation(value = "删除数据源")
	public JsonObject<?> deleteSrc(@PathVariable Long id) {
		srcService.deleteSrc(id);
		return new JsonObject<>("");
	}

	@PutMapping("/{id}")
	@ApiOperation(value = "修改数据源")
	public JsonObject<?> modifySrc(@PathVariable Long id, @RequestBody @Valid SrcRequest srcRequest) {
		return new JsonObject<>(srcService.updateSrc(id, srcRequest));
	}

	@GetMapping("/{id}")
	@ApiOperation(value = "获取指定数据源信息")
	public JsonObject<?> findOneSrc(@PathVariable Long id) {
		return new JsonObject<>(srcService.findOneSrc(id));
	}

	@GetMapping("/all")
	@ApiOperation(value = "获取所有数据源")
	public JsonObject<?> findAll() {
		return new JsonObject<>(srcService.findAll());
	}
}
