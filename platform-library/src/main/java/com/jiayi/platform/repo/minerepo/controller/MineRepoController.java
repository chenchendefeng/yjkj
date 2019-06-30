package com.jiayi.platform.repo.minerepo.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.jiayi.platform.common.web.dto.JsonObject;
import com.jiayi.platform.common.web.util.ExportUtil;
import com.jiayi.platform.repo.minerepo.dto.MiningRepoTwoCollisionRequest;
import com.jiayi.platform.repo.minerepo.entity.MineRepo;
import com.jiayi.platform.repo.minerepo.service.MineRepoService;
import com.jiayi.platform.repo.minerepo.vo.MineRepoRequest;
import com.jiayi.platform.repo.minerepo.vo.MineRepoSearchVo;
import com.jiayi.platform.repo.minerepo.vo.MineRepoTableDesc;
import com.jiayi.platform.repo.minerepo.vo.MiningRepoDetailSearchVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping ("/miningrepo")
@Slf4j
public class MineRepoController {

    @Autowired
    private MineRepoService mineRepoService;

    @GetMapping
    public JsonObject<?> find(MineRepoSearchVo mineRepoSearchVo) {
        return new JsonObject<>(mineRepoService.findMineRepoList(mineRepoSearchVo));
    }

    @PutMapping ("/{id}")
    public JsonObject<?> modify(@PathVariable Integer id, @RequestBody @Valid MineRepoRequest mineRepoRequest) {
        return new JsonObject<>(mineRepoService.modifyMineRepo(id, mineRepoRequest));
    }

    @PostMapping("/detail/search")
    public JsonObject<?> findDetail(@RequestBody MiningRepoDetailSearchVO miningRepoDetailSearchVO) {
        return new JsonObject<>(mineRepoService.findMineRepoDetail(miningRepoDetailSearchVO));
    }

    @PostMapping("/{repoId}/detail")
    public JsonObject<?> addDetail(@PathVariable Integer repoId, @RequestBody Map<String, String> params) {
        mineRepoService.addMineRepoDetail(repoId, params);
        return new JsonObject<>("");
    }


    @DeleteMapping("/{repoId}/detail")
    public JsonObject<?> deleteDetail(@PathVariable Integer repoId, @RequestBody Map<String, String> params) {
        mineRepoService.deleteMineRepoDetail(repoId, params);
        return new JsonObject<>("");
    }
// FIXME: 2019/4/28 放到judge工程中去
//    @PostMapping("/{repoId}/twocollision")
//    public JsonObject<?> addMiningRepo(@PathVariable Integer repoId, @RequestBody MiningRepoTwoCollisionRequest request) {
//        mineRepoService.addToTwoCollision(repoId, request);
//        return new JsonObject<>("");
//    }

    @PostMapping("/{repoId}/import/download")
    public void downloadCSVTemplate(@PathVariable Integer repoId, HttpServletResponse resp) {
        MineRepo mineRepo = mineRepoService.getMineReopById(repoId);
        MineRepoTableDesc tableDesc = JSON.parseObject(mineRepo.getDetailTableDesc(), MineRepoTableDesc.class);

        String fileName = "import_" + tableDesc.getName();
        String columnNames = String.join(",", getMineRepoFieldDescs(tableDesc));
        List<String> contents = Lists.newArrayList();
        List<String[]> contentCols = Lists.newArrayList();
        contents.add(ExportUtil.genContentByStringList(contentCols));
        ExportUtil.doExport(contents, columnNames, fileName, resp);
    }

    @PostMapping("/{repoId}/import")
    public void importFromCsv(@PathVariable Integer repoId, @RequestParam("file") MultipartFile file) {
        mineRepoService.importFromCsv(repoId, file);
    }

    private List<String> getMineRepoFieldDescs(MineRepoTableDesc tableDesc) {
        List<MineRepoTableDesc.FieldDesc> fields = tableDesc.getFields();
        List<String> descs = Lists.newArrayList();
        for (MineRepoTableDesc.FieldDesc f : fields) {
            if (StringUtils.isNotBlank(f.getDesc()))
                descs.add(f.getDesc());
        }
        return descs;
    }

    @GetMapping("/{repoId}/export")
    @ApiOperation (value = "导出挖掘库详情")
    public void exportDetail(@PathVariable Integer repoId, HttpServletResponse resp) {
        mineRepoService.exportMineRepoDetail(repoId, resp);
    }
}
