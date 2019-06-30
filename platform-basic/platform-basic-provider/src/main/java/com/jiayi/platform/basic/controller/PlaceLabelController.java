package com.jiayi.platform.basic.controller;

import com.jiayi.platform.basic.request.PageSearchRequest;
import com.jiayi.platform.basic.request.PlaceLabelRequest;
import com.jiayi.platform.basic.serviceImpl.PlaceLabelService;
import com.jiayi.platform.basic.dto.PlaceLabelDto;
import com.jiayi.platform.common.web.dto.JsonObject;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/placelabel")
public class PlaceLabelController {

    @Autowired
    private PlaceLabelService placeLabelService;

    @GetMapping
    @ApiOperation(value = "场所标签分页列表")
    public JsonObject<?> find(PageSearchRequest searchVo){
        return new JsonObject<>(placeLabelService.findPlaceLabelList(searchVo));
    }

    @GetMapping("/labeltree")
    public JsonObject<?> tree(){
        return new JsonObject<>(placeLabelService.tree());
    }

    @PostMapping
    @ApiOperation(value = "添加场所标签")
    public JsonObject<?> add(@RequestBody @Valid PlaceLabelRequest request){
        return new JsonObject<>(placeLabelService.addPlaceLabel(request));
    }

    @GetMapping("/{pcode}")
    @ApiOperation(value = "查询父级标签下的子标签")
    public JsonObject<?> findByPcode(@PathVariable String pcode){
        return new JsonObject<>(placeLabelService.findByPcode(pcode));
    }

    @GetMapping("/download")
    @ApiOperation("下载场所标签")
    public void download(HttpServletResponse response){
        placeLabelService.download(response);
    }

    @PutMapping("/updateremark")
    @ApiOperation("修改标签备注")
    public JsonObject<?> updateRemarkByCode(@RequestBody @Valid PlaceLabelDto placeLabelDto){
        placeLabelService.updateRemarkByCode(placeLabelDto);
        return new JsonObject<>("");
    }

}
