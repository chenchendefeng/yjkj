package com.jiayi.platform.basic.controller;

import com.jiayi.platform.basic.request.GeneratePlaceCodeRequest;
import com.jiayi.platform.basic.request.PlaceRequest;
import com.jiayi.platform.basic.request.PlaceSearchRequest;
import com.jiayi.platform.basic.serviceImpl.PlaceServiceImpl;
import com.jiayi.platform.common.web.dto.JsonObject;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/places")
public class PlaceController {
    //	@Autowired
//	private HttpServletRequest request;
    @Autowired
    private PlaceServiceImpl placeService;

    @ApiOperation(value = "查询场所")
    @GetMapping
    public JsonObject<?> findAll(PlaceSearchRequest placeSearchRequest) {
        return new JsonObject<>(placeService.findAllPlace(placeSearchRequest));
    }

    @GetMapping("/findByTypeAndValue")
    @ApiOperation(value = "type:1场所名称,2场所地址")
    public JsonObject findByTypeAndValue(String type, String value) {
        return new JsonObject(placeService.findByTypeAndValue(type, value));
    }

    @ApiOperation(value = "添加场所")
    @PostMapping
    public JsonObject<?> add(@RequestBody @Valid PlaceRequest placeRequest) {
        return new JsonObject<>(placeService.addPlace(placeRequest));
    }

    @ApiOperation(value = "删除场所")
    @DeleteMapping("/{id}")
    public JsonObject<?> delete(@PathVariable Long id) {
        placeService.deletePlace(id);
        return new JsonObject<>("");
    }

    @ApiOperation(value = "修改场所")
    @PutMapping("/{id}")
    public JsonObject<?> modify(@PathVariable Long id, @RequestBody PlaceRequest placeRequest) {
        return new JsonObject<>(placeService.updatePlace(id, placeRequest));
    }

    @ApiOperation(value = "查询某场所的信息")
    @GetMapping("/{id}")
    public JsonObject<?> findOne(@PathVariable String id) {
        return new JsonObject<>(placeService.findOne(Long.valueOf(id)));
    }

    @GetMapping(value = "/download")
    public void download(PlaceSearchRequest placeSearchRequest, HttpServletResponse response) {
//    	String token = request.getHeader("Authorization");
//		Long userId = Long.valueOf(JWTUtil.getUserId(token));
        placeService.download(placeSearchRequest, response);
    }

    @GetMapping(value = "/city")
    public JsonObject<?> city() {
        return new JsonObject<>(placeService.getCity());
    }

    @ApiOperation(value = "导出场所详情")
    @GetMapping(value = "/export")
    public void exportPlaceList(PlaceSearchRequest placeSearchRequest, HttpServletResponse response) {
        placeService.exportPlaceList(placeSearchRequest, response);
    }

    @ApiOperation(value = "生成场所编码")
    @PostMapping("/getPlaceCode")
    public JsonObject<?> getPlaceCode(@RequestBody GeneratePlaceCodeRequest generatePlaceCodeRequest) {
        return new JsonObject<>(placeService.generatePlaceCode(generatePlaceCodeRequest.getDistrict(), generatePlaceCodeRequest.getPlaceTags(), generatePlaceCodeRequest.getPlaceType()));
    }

    @ApiOperation(value = "上传文件解析")
    @PostMapping(value = "/upload")
    public JsonObject<?> upload(@RequestParam("file") MultipartFile file) {
        return new JsonObject<>(placeService.uploadAndParse(file));
    }

    @ApiOperation(value = "场所导入")
    @GetMapping(value = "/import")
    public JsonObject<?> importCsvFile(String fileName) {
        placeService.importPlaces(fileName);
        return new JsonObject<>("");
    }

    @GetMapping(value = "/isHavePlace")
    public JsonObject<?> isHavePlace(Integer deptId) {
        return new JsonObject<>(placeService.isHavePlace(deptId));
    }

    @GetMapping("/certType")
    @ApiOperation(value = "有效证件类型")
    public JsonObject<?> findAllCertType() {
        return new JsonObject<>(placeService.findAllCertType());
    }
}
