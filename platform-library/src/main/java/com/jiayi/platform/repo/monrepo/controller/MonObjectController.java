package com.jiayi.platform.repo.monrepo.controller;

import com.google.common.collect.Lists;

import com.jiayi.platform.common.exception.DBException;
import com.jiayi.platform.common.web.dto.JsonObject;
import com.jiayi.platform.common.web.util.CsvWriter;
import com.jiayi.platform.common.web.util.ExportUtil;
import com.jiayi.platform.repo.monrepo.service.MonObjectService;
import com.jiayi.platform.repo.monrepo.vo.MonObjectRequest;
import com.jiayi.platform.repo.monrepo.vo.MonObjectSearchVo;
import com.jiayi.platform.repo.monrepo.vo.MonRemarkVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;

@RestController
@RequestMapping ("/monobject")
public class MonObjectController {
    @Autowired
    private MonObjectService monObjectService;

    @GetMapping
    public JsonObject<?> find(@Valid MonObjectSearchVo monObjectSearchVo) {
        return new JsonObject<>(monObjectService.findMonObjectList(monObjectSearchVo));
    }

    @PostMapping
    public JsonObject<?> add(@RequestBody @Valid MonObjectRequest monObjectRequest) {
        return new JsonObject<>(monObjectService.addMonObject(monObjectRequest));
    }

    @DeleteMapping("/{id}")
    public JsonObject<?> delete(@PathVariable Long id) {
        monObjectService.deleteMonObject(id);
        return new JsonObject<>("");
    }

    @PutMapping("/{id}")
    public JsonObject<?> modify(@PathVariable Long id, @RequestBody @Valid MonObjectRequest monObjectRequest) {
        return new JsonObject<>(monObjectService.modifyMonObject(id, monObjectRequest));
    }

    @PostMapping(value = "/impexcel")
    @ResponseBody
    public JsonObject<?> impExcel(@RequestParam("file") MultipartFile file, @RequestParam("repo_id") Long repoId, @RequestParam("user_id") Integer userId) {
        monObjectService.impExcel(file, repoId, userId);
        return new JsonObject<>("");
    }

    @PostMapping(value = "/impcsv")
    @ResponseBody
    public JsonObject<?> impCsv(@RequestParam("file") MultipartFile file, @RequestParam("repo_id") Long repoId, @RequestParam("user_id") Integer userId) {
        monObjectService.impCsv(file, repoId, userId);
        return new JsonObject<>("");
    }

    @PostMapping(value = "/expexcel")
    public void exportExcel(HttpServletResponse resp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        CsvWriter csvWriter = new CsvWriter(stream, ',', Charset.forName("utf-8"));
        String[] row1 = {"手机", "MAC", "DE:12:34:56:AB:78", "蒋丽云", "147258369147852000\t", "12345678901\t", "深圳龙岗大运", "中心城"};
        String[] row2 = {"汽车", "车牌", "粤B12345", "何振华", "440258369147855000\t", "25814736901\t", "深圳市南山区金华大厦223", "金光华"};
        try {
            csvWriter.writeRecord(row1, true);
            csvWriter.writeRecord(row2, true);

            csvWriter.flush();
            csvWriter.close();
            byte[] buffer = stream.toByteArray();
            stream.close();
            String data = Charset.forName("utf-8").decode(ByteBuffer.wrap(buffer)).toString();

            List<String> contents = Lists.newArrayList();
            contents.add(data);
            String fileName = "import_monitorObject_model";
            ExportUtil.doExport(contents, "物品名称,数据类型（MAC/车牌/IMEI/IMSI）,数据值,姓名,证件号码（身份证默认18位，X大写）,手机号码（11位）,住址,备注", fileName, resp);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            throw new DBException("download import-object excel model error", ioe);
        }
    }

    @PutMapping("/updateremark")
    @ApiOperation(value = "修改常用库物品备注")
    public JsonObject<?> modifyMonObject(@RequestBody MonRemarkVo monRemarkVo) {
        monObjectService.modifyMonRepoRemark(monRemarkVo);
        return new JsonObject<>("");
    }
}
