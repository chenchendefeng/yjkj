package com.jiayi.platform.repo.minerepo.controller;

import com.google.common.collect.Lists;

import com.jiayi.platform.common.exception.ArgumentException;
import com.jiayi.platform.common.util.JWTUtil;
import com.jiayi.platform.common.util.ThreadPoolUtil;
import com.jiayi.platform.common.web.util.ExportUtil;
import com.jiayi.platform.repo.minerepo.dto.RepoSearchResultDto;
import com.jiayi.platform.repo.minerepo.entity.MiningRepo;
import com.jiayi.platform.repo.minerepo.manager.MiningRepoCacheManager;
import com.jiayi.platform.repo.minerepo.service.MiningRepoService;
import com.jiayi.platform.repo.minerepo.service.MonitorRepoService;
import com.jiayi.platform.repo.minerepo.vo.*;
import com.jiayi.platform.security.core.dto.JsonObject;
import com.jiayi.platform.security.core.enums.MessageCodeEnum;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

@RestController
@RequestMapping("/miningrepo")
public class MiningRepoController {
    private static final Logger log = LoggerFactory.getLogger(MiningRepoController.class);

    @Autowired
    private MiningRepoService mineRepoService;
    @Autowired
    private MonitorRepoService monitorRepoService;
    @Autowired
    private MiningRepoCacheManager miningRepoCacheManager;
    @Autowired
    private HttpServletRequest request;

    @GetMapping
    public JsonObject<?> find(MiningRepoSearchVo mineRepoSearchVo) {
        return new JsonObject<>(mineRepoService.findMineRepoList(mineRepoSearchVo));
    }

    @PutMapping("/{id}")
    public JsonObject<?> modify(@PathVariable Integer id, @RequestBody @Valid MiningRepoRequest mineRepoRequest) {
        return new JsonObject<>(mineRepoService.modifyMineRepo(id, mineRepoRequest));
    }

    @PostMapping("/detail/search")
    public JsonObject<?> findDetail(@RequestBody MiningRepoDetailSearchVO miningRepoDetailSearchVO) {
        return new JsonObject<>(mineRepoService.findMineRepoDetail(miningRepoDetailSearchVO));
    }

    @PostMapping("/{repoId}/detail")
    public JsonObject<?> addDetail(@PathVariable Integer repoId, @RequestBody Map<String, String> params) {
        try {
            mineRepoService.addMineRepoDetail(repoId, params);
        } catch (ArgumentException e) {
            return new JsonObject<>("", MessageCodeEnum.FAILED.getCode(), e.getMessage());
        }
        return new JsonObject<>("");
    }


    @DeleteMapping("/{repoId}/detail")
    public JsonObject<?> deleteDetail(@PathVariable Integer repoId, @RequestBody Map<String, String> params) {
        mineRepoService.deleteMineRepoDetail(repoId, params);
        return new JsonObject<>("");
    }
// FIXME: 2019/4/28 增加到judge模块中
//    @PostMapping("/{repoId}/twocollision")
//    public JsonObject<?> addMiningRepo(@PathVariable Integer repoId, @RequestBody MiningRepoTwoCollisionRequest request) {
//        mineRepoService.addToTwoCollision(repoId, request, null);
//        return new JsonObject<>("");
//    }

    @PostMapping("/{repoId}/import/download")
    public void downloadCSVTemplate(@PathVariable Integer repoId, HttpServletResponse resp) {
        MiningRepo mineRepo = miningRepoCacheManager.getMineReopById(repoId);
        MiningRepoTableDesc tableDesc = mineRepo.getDetailTableDescObj();

        String fileName = "import_" + tableDesc.getName();
        String columnNames = String.join(",", mineRepoService.getMineRepoFieldDescs(tableDesc));
        List<String> contents = Lists.newArrayList();
        List<String[]> contentCols = Lists.newArrayList();
        contents.add(ExportUtil.genContentByStringList(contentCols));
        ExportUtil.doExport(contents, columnNames, fileName, resp);
    }

    @PostMapping("/{repoId}/import")
    public JsonObject importFromCsv(@PathVariable Integer repoId, @RequestParam("file") MultipartFile file) {

        if (!file.getOriginalFilename().endsWith(".csv")) {
            return new JsonObject("", MessageCodeEnum.FAILED.getCode(), "请导入正确的格式(.csv)文件");
        }

        try {
            mineRepoService.importFromCsv(repoId, file);
        } catch (IllegalArgumentException e) {
            return new JsonObject("", MessageCodeEnum.FAILED.getCode(), e.getMessage());
        }
        return new JsonObject("");
    }

    @GetMapping("/search")
    @ApiOperation(value="搜索库（常用库和数据挖掘库）中数据")
    public JsonObject search(@Valid RepoSearchVo searchVo){
        String token = request.getHeader("Authorization");
        String userId = JWTUtil.getUserId(token);
        List<RepoSearchResultDto> list = new ArrayList<>();
        List<Future<RepoSearchResultDto>> futurelist = new ArrayList<Future<RepoSearchResultDto>>();
        if(searchVo.getRepoType() == 20 || searchVo.getRepoType() == 21){
            futurelist.add(ThreadPoolUtil.getInstance().submit(() -> monitorRepoService.search(searchVo, Long.valueOf(userId))));
        }
        if(searchVo.getRepoType() == 20 || searchVo.getRepoType() == 22){
            futurelist.add(ThreadPoolUtil.getInstance().submit(() -> mineRepoService.search(searchVo)));
        }
        futurelist.forEach((v)->{
            try {
                list.add(v.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return new JsonObject(list);
    }
}
