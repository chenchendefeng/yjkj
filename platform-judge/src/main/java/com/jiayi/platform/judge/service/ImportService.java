package com.jiayi.platform.judge.service;

import au.com.bytecode.opencsv.CSVReader;
import com.jiayi.platform.common.exception.ArgumentException;
import com.jiayi.platform.common.exception.DBException;
import com.jiayi.platform.common.exception.ServiceException;
import com.jiayi.platform.common.exception.ValidException;
import com.jiayi.platform.common.util.JsonUtils;
import com.jiayi.platform.common.util.MacUtil;
import com.jiayi.platform.common.web.dto.JsonObject;
import com.jiayi.platform.common.web.enums.MessageCodeEnum;
import com.jiayi.platform.common.web.util.ExportUtil;
import com.jiayi.platform.judge.dao.impala.FileImportDao;
import com.jiayi.platform.judge.entity.mysql.QueryHistory;
import com.jiayi.platform.judge.enums.FileImportTypeEnum;
import com.jiayi.platform.judge.enums.JudgeStatus;
import com.jiayi.platform.judge.enums.RequestType;
import com.jiayi.platform.judge.enums.ResultFieldEnum;
import com.jiayi.platform.judge.manage.RequestHistoryManager;
import com.jiayi.platform.judge.manage.SaveResultFieldManager;
import com.jiayi.platform.judge.query.FileImportQuery;
import com.jiayi.platform.judge.request.*;
import com.jiayi.platform.judge.response.FileImportResponse;
import com.jiayi.platform.judge.response.FileParsingResponse;
import com.jiayi.platform.library.minerepo.entity.MiningRepo;
import com.jiayi.platform.library.minerepo.manager.MiningRepoCacheManager;
import com.jiayi.platform.library.minerepo.service.MiningRepoService;
import com.jiayi.platform.library.minerepo.vo.MiningRepoTableDesc;
import com.jiayi.platform.library.monrepo.dao.MonRepoDao;
import com.jiayi.platform.library.monrepo.service.MonRepoService;
import com.jiayi.platform.security.core.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.hibernate.internal.util.collections.ArrayHelper.isAllTrue;

@Service
@Slf4j
public class ImportService {

    @Autowired
    private MiningRepoService miningRepoService;
    @Autowired
    private MonRepoService monRepoService;
    @Autowired
    private MiningRepoCacheManager miningRepoCacheManager;
    @Autowired
    private RequestHistoryManager requestHistoryManager;
    @Autowired
    private SaveResultFieldManager saveResultFieldManager;
    @Autowired
    private FileImportDao fileImportDao;
    @Autowired
    private MonRepoDao monRepoDao;

    @Value("${fileimport.upload.path:D://fileimport/upload/}")
    private String uploadPath;
    @Value("${fileimport.errordata.upload.path:D://fileimport/errordata}")
    private String errorFilePath;
    @Value("${fileimport.errordata.validdate:30}")
    private Double validDate;
    private static final String[] IMPORT_DATE_PATTERNS = new String[]{"yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-mm-dd HH:mm",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm"};

    public JsonObject<?> importMiningRepo(Integer repoId, MiningImportRequest request) {
        if (requestHistoryManager.existsResultName(request.getCaseId(), request.getResultName())) {
            throw new ValidException("结果集名称已存在，请更换名称");
        }
        MiningRepo miningRepo = miningRepoCacheManager.getMineReopById(repoId);
        MiningRepoTableDesc tableDesc = miningRepo.getDetailTableDescObj();
        long resultCount = miningRepoService.countMiningRepoDetail(tableDesc, request.getObjTypes(), request.getStartTime(), request.getEndTime());
        if(resultCount <= 0){
            throw new ValidException("数据挖掘库中内容为空，无法导入");
        }
        MiningQueryHistoryParam historyParam = new MiningQueryHistoryParam();
        historyParam.setRepoType(2);
        historyParam.setStartTime(request.getStartTime());
        historyParam.setEndTime(request.getEndTime());
        historyParam.setRepoId(repoId);
        historyParam.setObjTypes(request.getObjTypes());
        historyParam.setRepoName(miningRepo.getRepoName());

        QueryHistory queryHistory =
                requestHistoryManager.saveQueryHistory(historyParam, RequestType.MINING_REPO.typeName(), JudgeStatus.SUCCEED, resultCount);
        requestHistoryManager.saveRequestHistory(request, queryHistory.getId(), request.getResultName());
        return new JsonObject<>("");
    }

    public JsonObject<?> importRepo(RepoImportRequest request){
        if (requestHistoryManager.existsResultName(request.getCaseId(), request.getResultName())) {
            throw new ValidException("结果集名称已存在，请更换名称");
        }
        if(request.getRepoType() == 1){
            Long count = monRepoDao.countMonitorObjectByRepoId(request.getRepoId());
            if(count <= 0){
                throw new ValidException("常用库中无物品信息，无法导入");
            }
            String repoName = monRepoDao.findRepoNameById(request.getRepoId());
            // 将request转化为存到数据库的param
            MonitorRepoParam monitorRepoParam = new MonitorRepoParam();
            monitorRepoParam.setRepoId(request.getRepoId());
            monitorRepoParam.setRepoName(repoName);
            monitorRepoParam.setRepoType(request.getRepoType());
            monitorRepoParam.setObjTypes(ResultFieldEnum.getResultInfoList(RequestType.REPO_IMPORT.typeName(), null)
                    .stream().map(ResultFieldEnum::resultDesc).collect(Collectors.toList()));

            QueryHistory queryHistory =
                    requestHistoryManager.saveQueryHistory(monitorRepoParam, RequestType.REPO_IMPORT.typeName(), JudgeStatus.SUCCEED, count);
            requestHistoryManager.saveRequestHistory(request, queryHistory.getId(), request.getResultName());
        } else if (request.getRepoType() == 2) {
            MiningRepo miningRepo = miningRepoCacheManager.getMineReopById(request.getRepoId().intValue());
            MiningRepoTableDesc tableDesc = miningRepo.getDetailTableDescObj();
            List<String> objTypes = Arrays.asList("mac", "carno", "imei", "imsi");
            long resultCount = miningRepoService.countMiningRepoDetail(tableDesc, objTypes, null, null);
            if(resultCount <= 0){
                throw new ValidException("数据挖掘库中内容为空，无法导入");
            }
            MiningQueryHistoryParam historyParam = new MiningQueryHistoryParam();
            historyParam.setRepoType(request.getRepoType());
            historyParam.setRepoId(miningRepo.getId().intValue());
            historyParam.setObjTypes(objTypes);
            historyParam.setRepoName(miningRepo.getRepoName());

            QueryHistory queryHistory =
                    requestHistoryManager.saveQueryHistory(historyParam, RequestType.MINING_REPO.typeName(), JudgeStatus.SUCCEED, resultCount);
            requestHistoryManager.saveRequestHistory(request, queryHistory.getId(), request.getResultName());
        } else {
            return new JsonObject<>(null, MessageCodeEnum.FAILED.getCode(), "invalid repoType request");
        }
        return new JsonObject<>("");
    }

    public JsonObject<?> getRepoByType(Integer repoType) {
        if (repoType == 1) {
            return new JsonObject<>(monRepoService.getRepoByUserId());
        } else if (repoType == 2) {
            return new JsonObject<>(miningRepoService.getRepoByType());
        } else {
            return new JsonObject<>(null, MessageCodeEnum.FAILED.getCode(), "repoType request error");
        }
    }

    public JsonObject<?> uploadFile(MultipartFile file) {
        if (file == null || file.getOriginalFilename() == null || !file.getOriginalFilename().endsWith(".csv")) {
            return new JsonObject<>("", MessageCodeEnum.FAILED.getCode(), "请上传CSV格式的文件");
        }
        String fileName = file.getOriginalFilename();
        try {
            File filePath = new File(uploadPath);
            if (!filePath.exists()) {
                filePath.mkdirs();
            }
            File dest = new File(uploadPath + File.separator + fileName);
//            if(dest.exists()){
//                return new JsonObject("", MessageCodeEnum.FAILED.getCode(), "文件名重复");
//            }
            file.transferTo(dest);
        } catch (Exception e) {
            throw new ServiceException("file upload error");
        }
        return new JsonObject<>(fileName); // 返回保存的文件名
    }

    public FileParsingResponse fileParsing(String fileName, Integer page, Integer size) {
        String filePath = uploadPath + File.separator + fileName;
        List<String[]> contents = getContents(filePath);
        String[] headers = contents.remove(0);
        boolean[] isNotEmpty = new boolean[headers.length];
        int start = page * size;
        int count = contents.size();
        int end = count > start + size ? start + size : count;
        if (contents.size() <= 0 || start > end) {
            return new FileParsingResponse(new String[0], isNotEmpty, Collections.emptyList(), 0L, 0, 0);
        }
        for (String[] content : contents) { // 文件列判空
            for (int i = 0; i < content.length; i++) {
                if (StringUtils.isNotEmpty(content[i]) && !isNotEmpty[i]) {
                    isNotEmpty[i] = true;
                }
            }
            if (isAllTrue(isNotEmpty)) {
                break;
            }
        }
        List<String[]> result = contents.subList(start, end);
        return new FileParsingResponse(headers, isNotEmpty, result, (long) count, page, result.size());
    }

    public void downloadErrorData(Long requestId, HttpServletResponse response) {
        QueryHistory queryHistory = requestHistoryManager.getQueryHistoryByRequestId(requestId);
        String param = queryHistory.getRequestParameter();
        FileImportParam obj;
        try {
            obj = JsonUtils.parse(param, FileImportParam.class);
        } catch (IOException e) {
            throw new ArgumentException("invalid request parameter", e);
        }
        if (obj.getTime() != null) {
            Instant now = Instant.now();
            long minutes = (long) (validDate * 24 * 60);
            Long earliestTime = now.minus(Duration.ofMinutes(minutes)).toEpochMilli();
            if (obj.getTime() < earliestTime) {
                throw new ValidException("已超过" + validDate + "天无法下载");
            }
        }
        if (obj.getErrorFilePath() != null) {
            File file = new File(errorFilePath + File.separator + obj.getErrorFilePath());
            if (file.exists()) {
                ExportUtil.doExport(file, response);
            } else {
                throw new ValidException("文件不存在或丢失");
            }
        } else {
            throw new ValidException("没有失败数据文件");
        }
    }

    /**
     * csv文件导入
     */
    public FileImportResponse importCsv(FileImportRequest request) {
        if (requestHistoryManager.existsResultName(request.getCaseId(), request.getResultName())) {
            throw new ValidException("结果集名称已存在，请更换名称");
        }
        String filePath = uploadPath + File.separator + request.getFileName();
        List<String[]> contents = getContents(filePath); // 获取导入文件所有内容
        List<FileObjectTypeInfo> list = request.getObjectTypes();
        List<Integer> indexes = list.stream().filter(FileObjectTypeInfo::isImport)
                .map(FileObjectTypeInfo::getIndex).sorted().collect(Collectors.toList()); // 文件需要导入的title排序下标
        if (CollectionUtils.isEmpty(indexes)) {
            throw new ArgumentException("未选择导入的数据列");
        }
        Map<Integer, FileObjectTypeInfo> objectTypeMap = list.stream()
                .collect(Collectors.toMap(FileObjectTypeInfo::getIndex, Function.identity(), (k1, k2) -> k2)); // 所有title下标对应数据类型
        // 导入文件的title对应写入collision_result_file_import字段 和 写结果集动态sql
        Map<String, FileObjectTypeInfo> resultFieldsMap = new LinkedHashMap<>(); // 导入部分title
        for (int i = 0; i < indexes.size(); i++) {
            FileObjectTypeInfo fileObjectTypeInfo = objectTypeMap.get(indexes.get(i));
            Integer typeId = fileObjectTypeInfo.getFieldType();
            fileObjectTypeInfo.setType(FileImportTypeEnum.getTypeById(typeId).type()); // 设置该数据对应的类型
            resultFieldsMap.put(ResultFieldEnum.OBJECT_VALUE.resultName() + (i + 1), fileObjectTypeInfo);
        }
        List<String> names = resultFieldsMap.values().stream().map(FileObjectTypeInfo::getFieldDesc).collect(Collectors.toList()); // 需要导入的title

        FileImportParam param = new FileImportParam(names, 0, 0);
        QueryHistory queryHistory = requestHistoryManager.saveQueryHistory(param, RequestType.FILE_IMPORT.typeName(), JudgeStatus.CALCULATING);
        saveResultFieldManager.saveFileImportResultFields(resultFieldsMap, queryHistory.getId()); // 写入kudu字段与文件title对应关系
        long requestId = requestHistoryManager.saveRequestHistory(request, queryHistory.getId(), request.getResultName()); // 保存二次碰撞记录
        try {
            param = batchInsert(queryHistory.getId(), contents, indexes, resultFieldsMap, request.getResultName());//批量写入结果集
            param.setTitle(names);
            requestHistoryManager.updateQueryHistory(param, queryHistory.getId(), param.getSuccessNum().longValue(), JudgeStatus.SUCCEED); // 更新状态
        } catch (Exception e) {
            requestHistoryManager.updateQueryStatus(queryHistory.getId(), JudgeStatus.FAILED);
            log.error("error reading csv file content", e);
            throw new DBException("error reading csv file content");
        } finally {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        }
        return new FileImportResponse(requestId, param.getSuccessNum(), param.getErrorNum());
    }

    /**
     * 批量写入结果集，返回写入结果对象
     *
     * @return
     */
    private FileImportParam batchInsert(Long queryId, List<String[]> contents, List<Integer> indexes,
                                         Map<String, FileObjectTypeInfo> resultFieldsMap, String resultName) {
        String[] headers = contents.remove(0);
        List<String> fields = new ArrayList<>(resultFieldsMap.keySet());//写入kudu的字段名
        List<String[]> errorData = new ArrayList<>();//错误数据写入文件

        int count = 0;//导入成功数量

        FileImportQuery query = new FileImportQuery();
        query.setFieldNames(fields);
        query.setUid(queryId);

        List<List<String>> data = new ArrayList<>();
        for (String[] content : contents) {
            List<String> rowValues = new ArrayList<>();
            boolean flag = true;
            try {
                int j = 1;
                for (Integer index : indexes) { // 需要导入title的下标
                    String value = content[index - 1];
                    FileObjectTypeInfo fileObjectTypeInfo = resultFieldsMap.get(ResultFieldEnum.OBJECT_VALUE.resultName() + j); // 获取该字段数据类型

                    if ("String".equalsIgnoreCase(fileObjectTypeInfo.getType())) {
                        if (fileObjectTypeInfo.getFieldType().equals(FileImportTypeEnum.MAC.id())) {
                            value = MacUtil.toTrimMac(value);
                        }
                    } else if ("Bigint".equalsIgnoreCase(fileObjectTypeInfo.getType())) {
                        if (fileObjectTypeInfo.getFieldType().equals(FileImportTypeEnum.DATE.id())) {//日期
                            if (StringUtils.isNotBlank(value))
                                value = "" + DateUtils.parseDateStrictly(value, IMPORT_DATE_PATTERNS).getTime();
                        } else if (fileObjectTypeInfo.getFieldType().equals(FileImportTypeEnum.FIGURE.id())) {
                            if (!StringUtils.isNumeric(value)) {
                                throw new ArgumentException("数字校验失败");
                            }
                        }
                    }
                    rowValues.add(value);
                    j++;
                }
                if (isAllEmptyValue(rowValues)) {//判断是否为空行
                    flag = false;
                }
            } catch (Exception e) {
                errorData.add(content);//记录错误数据
                flag = false;
            }
            if (flag) {//正确数据才计数
                data.add(rowValues);
                count++;
            }
        }
        try {
            if (count != 0) {
                query.setContents(data);
                fileImportDao.insertFileImport(query);
            }
            FileImportParam param = new FileImportParam(null, count, errorData.size());
            if (CollectionUtils.isNotEmpty(errorData)) {
                String fileName = resultName + ".csv";//错误数据的文件使用结果集名称
                String dir = FileUtil.generateNormalFilePath();
                String errorFilePath0 = errorFilePath + File.separator + dir;
                List<String> result = Arrays.asList(ExportUtil.genContentByStringList(errorData));
                FileUtil.writeCsvFileToPath(result, StringUtils.join(headers, ","), fileName, errorFilePath0);//错误数据写入文件

                param.setErrorFilePath(dir + File.separator + fileName);
                param.setTime(System.currentTimeMillis());
            }
            return param;//返回的对象作为query_history表中request_parameter字段的内容
        } catch (Exception e) {
            throw new RuntimeException("file import insert error", e);
        }
    }

    private boolean isAllEmptyValue(List<String> values) {
        for (String value : values) {
            if (StringUtils.isNotEmpty(value)) {
                return false;
            }
        }
        return true;
    }

    private List<String[]> getContents(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new ArgumentException("文件不存在");
        }
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        InputStreamReader isr = null;
        CSVReader csvReader = null;
        try {
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            bis.mark(0);
            String charset = "GBK";
            byte[] head = new byte[3];
            bis.read(head);
            if (head[0] == (byte) 0xEF && head[1] == (byte) 0xBB && head[2] == (byte) 0xBF) {
                charset = "UTF-8";
                // 如果是UTF-8编码，则写文件时忽略开头的BOM，因为FileUtil.writeCsvFileToPath方法使用的是GBK编码
            } else {
                bis.reset();
            }
            isr = new InputStreamReader(bis, charset);
            csvReader = new CSVReader(isr);
            String[] headers = csvReader.readNext();
            if (headers.length > 30) {
                throw new ArgumentException("导入文件最多支持30个数据类型");
            }
            List<String[]> contents = csvReader.readAll();//分批读取导入?
            contents.add(0, headers);
            if (contents.size() <= 1) {
                throw new ArgumentException("导入文件不能为空");
            }
            return contents;
        } catch (ArgumentException ae) {
            throw ae;
        } catch (Exception e) {
            throw new ServiceException("error reading csv file content", e);
        } finally {
            if (csvReader != null) {
                try {
                    csvReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
