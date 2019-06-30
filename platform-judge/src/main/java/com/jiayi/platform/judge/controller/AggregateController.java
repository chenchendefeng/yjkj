package com.jiayi.platform.judge.controller;

import com.jiayi.platform.common.web.dto.JsonObject;
import com.jiayi.platform.judge.common.bean.GeneralRequest;
import com.jiayi.platform.judge.common.bean.PageResult;
import com.jiayi.platform.judge.enums.RequestType;
import com.jiayi.platform.judge.request.*;
import com.jiayi.platform.judge.response.AggregateCollideField;
import com.jiayi.platform.judge.service.AggregateService;
import com.jiayi.platform.judge.service.ImportService;
import com.jiayi.platform.judge.service.JudgeService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 二次碰撞类研判
 * @author : weichengke
 * @date : 2019-04-18 17:05
 */
@RestController
@RequestMapping("aggr")
public class AggregateController {
    @Autowired
    private JudgeService judgeService;
    @Autowired
    private AggregateService aggregateService;
    @Autowired
    private ImportService importService;

    @ApiOperation(value = "二次碰撞", notes = "对已有的查询结果，再次进行交、并、差、异或运算")
    @PostMapping("/aggregate")
    public PageResult<?> aggregate(@RequestBody GeneralRequest<AggregateRequest> request) {
        return judgeService.judge(request.getRequest(), request.getPageRequest(), RequestType.AGGREGATE_COLLISION);
    }

    @ApiOperation(value = "二次碰撞字段信息", notes = "二次碰撞所选结果集的字段信息")
    @PostMapping("/collideField")
    public JsonObject<?> getFields(@RequestBody AggregateCollideFieldRequest request) {
        List<AggregateCollideField> list = aggregateService.getAggregateCollideField(request.getUidList());
        return new JsonObject<>(list);
    }

    @ApiOperation(value = "添加挖掘库到二次碰撞")
    @PostMapping("/mining/{repoId}")
    public JsonObject<?> importMiningRepo(@PathVariable Integer repoId, @RequestBody MiningImportRequest request) {
        return importService.importMiningRepo(repoId, request);
    }

    @ApiOperation(value = "库导入到二次碰撞", notes = "添加常用库或挖掘库到二次碰撞中")
    @PostMapping("/importRepo")
    public JsonObject<?> importRepo(@RequestBody RepoImportRequest request) {
        return importService.importRepo(request);
    }

    @ApiOperation(value = "库导入名称下拉数据")
    @GetMapping("/importRepo/{repoType}")
    public JsonObject<?> getRepoByType(@PathVariable @ApiParam(value = "库类型:1常用库,2挖掘库") Integer repoType) {
        return importService.getRepoByType(repoType);
    }

    @ApiOperation(value = "文件导入到二次碰撞")
    @PostMapping("/file")
    public JsonObject<?> importFile(@RequestBody FileImportRequest request) {
        return new JsonObject<>(importService.importCsv(request));
    }

    @ApiOperation(value = "上传文件")
    @PostMapping("/upload")
    public JsonObject<?> upload(@RequestParam("file") MultipartFile file) {
        return importService.uploadFile(file);
    }

    @ApiOperation(value = "导入文件解析分页")
    @GetMapping("/parsing")
    public JsonObject<?> fileParsing(String fileName, Integer page, Integer size) {
        return new JsonObject<>(importService.fileParsing(fileName, page, size));
    }

    @ApiOperation(value = "下载导入失败的数据")
    @GetMapping("/downloadErrorData/{requestId}")
    public void downloadErrorData(@PathVariable Long requestId, HttpServletResponse response) {
        importService.downloadErrorData(requestId, response);
    }

    @ApiOperation(value = "二次碰撞记录结果导出")
    @PostMapping(value = "/export")
    public void aggregateDownload(@RequestBody QueryResultRequest request, HttpServletResponse response) {
        aggregateService.exportAggregateRecordResult(request, response);
    }
}
