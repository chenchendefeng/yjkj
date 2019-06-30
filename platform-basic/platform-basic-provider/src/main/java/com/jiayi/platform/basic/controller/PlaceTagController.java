package com.jiayi.platform.basic.controller;

import com.jiayi.platform.basic.serviceImpl.PlaceTagService;
import com.jiayi.platform.basic.request.PlaceTagRequest;
import com.jiayi.platform.common.web.dto.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/placetypes")
public class PlaceTagController {

    @Autowired
    private PlaceTagService placeTagService;

    @GetMapping
    public JsonObject<?> findAllPlaceTag(Integer page, Integer size) {
        return new JsonObject<>(placeTagService.findAlltag(page, size));
    }

    @PostMapping
    public JsonObject<?> addSrc(@RequestBody PlaceTagRequest placeTagRequest) {
        return new JsonObject<>(placeTagService.addTag(placeTagRequest));
    }

    @DeleteMapping("/{id}")
    public JsonObject<?> deleteSrc(@PathVariable Long id) {
        placeTagService.deleteTag(id);
        return new JsonObject<>("");
    }

    @PutMapping("/{id}")
    public JsonObject<?> modifySrc(@PathVariable Long id, @RequestBody PlaceTagRequest placeTagRequest) {
        return new JsonObject<>(placeTagService.updatePlaceTag(id, placeTagRequest));
    }

    @GetMapping("/{id}")
    public JsonObject<?> findOnePlaceTag(@PathVariable Long id) {
        return new JsonObject<>(placeTagService.findOneTag(id));
    }

}
