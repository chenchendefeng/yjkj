package com.jiayi.platform.repo.minerepo.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Lists;
import com.jiayi.platform.common.enums.ErrorEnum;
import com.jiayi.platform.common.exception.ArgumentException;
import com.jiayi.platform.common.exception.DBException;
import com.jiayi.platform.common.exception.ValidException;
import com.jiayi.platform.common.util.ThreadPoolUtil;
import com.jiayi.platform.repo.minerepo.dao.MiningRepoDao;
import com.jiayi.platform.repo.minerepo.dto.MiningPageDto;
import com.jiayi.platform.repo.minerepo.dto.MiningRepoDetailDto;
import com.jiayi.platform.repo.minerepo.dto.MiningRepoDto;
import com.jiayi.platform.repo.minerepo.dto.RepoSearchResultDto;
import com.jiayi.platform.repo.minerepo.entity.MiningRepo;
import com.jiayi.platform.repo.minerepo.enums.MiningRepoSourceEnum;
import com.jiayi.platform.repo.minerepo.manager.MiningRepoCacheManager;
import com.jiayi.platform.repo.minerepo.manager.RepoSearchManager;
import com.jiayi.platform.repo.minerepo.vo.*;
import com.jiayi.platform.security.core.dao.DepartmentDao;
import com.jiayi.platform.security.core.entity.Department;
import com.opencsv.CSVReader;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.mozilla.universalchardet.UniversalDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
public class MiningRepoService {
    private static Logger log = LoggerFactory.getLogger(MiningRepoService.class);

    private static String[] IMPORT_DATE_PATTERNS = new String[] {"yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-mm-dd HH:mm",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm"};

//    @Autowired
//    private QueryHistoryDao queryHistoryDao;
//
//    @Autowired
//    private RequestHistoryDao requestHistoryDao;

    @Autowired
    private MiningRepoDao mineRepoDao;

    @Autowired
    private DepartmentDao departmentDao;

    @Autowired
    private MiningRepoCacheManager miningRepoCacheManager;

    @Autowired
    private RepoSearchManager repoSearchManager;

    public MiningPageDto<MiningRepoDto> findMineRepoList(MiningRepoSearchVo mineRepoSearchVo) {
        List<MiningRepoDto> mineRepoDtoAll = mineRepoDao.selectMineRepoList(mineRepoSearchVo);

        if (mineRepoSearchVo.getPage() == null) {
            mineRepoSearchVo.setPage(0);
        }
        if (mineRepoSearchVo.getSize() == null) {
            mineRepoSearchVo.setSize(10);
        }

        int start = mineRepoSearchVo.getPage() * mineRepoSearchVo.getSize();
        int count = mineRepoDtoAll.size();
        int end = count - start > mineRepoSearchVo.getSize() ? start + mineRepoSearchVo.getSize() : count;
        if(CollectionUtils.isEmpty(mineRepoDtoAll) || start > end) {
            return new MiningPageDto<MiningRepoDto>(Lists.newArrayList(), (long) count, mineRepoSearchVo.getPage(), 0);
        }
        List<MiningRepoDto> list = mineRepoDtoAll.subList(start, end);
        List<Department> departments = departmentDao.findAllById(list.stream().filter(a -> a.getDepartmentId() != null).map(b -> b.getDepartmentId()).collect(Collectors.toList()));
        for (MiningRepoDto mineRepoDto : list.stream().filter(a -> a.getDepartmentId() != null).collect(Collectors.toList())) {
            for (Department department : departments) {
                if (mineRepoDto.getDepartmentId().equals(department.getId())) {
                    mineRepoDto.setDepartment(department);
                    break;
                }
            }
        }
        return new MiningPageDto<MiningRepoDto>(list, (long)count, mineRepoSearchVo.getPage(), list.size());
    }

    public MiningRepo modifyMineRepo(Integer id, MiningRepoRequest mineRepoRequest) {
        try {
            MiningRepo mineRepo = miningRepoCacheManager.getMineReopById(id);
            if (mineRepoRequest.getRepoName() != null && !mineRepoRequest.getRepoName().equals(mineRepo.getRepoName())) {
                long count = mineRepoDao.countMineRepoName(mineRepoRequest.getRepoName());
                if (count > 0) { throw new ArgumentException("挖掘库名称重复");}
            }

            if (mineRepoRequest.getRepoName() != null) mineRepo.setRepoName(mineRepoRequest.getRepoName());
            if (mineRepoRequest.getRepoType() != null) mineRepo.setRepoType(mineRepoRequest.getRepoType());
            if (mineRepoRequest.getDepartmentId() != null) mineRepo.setDepartmentId(mineRepoRequest.getDepartmentId());
            if (mineRepoRequest.getRepoDesc() != null) mineRepo.setRepoDesc(mineRepoRequest.getRepoDesc());
            mineRepo.setUpdateAt(System.currentTimeMillis());
            mineRepoDao.updateMineRepo(mineRepo);
            miningRepoCacheManager.updateCache(mineRepo);
            return mineRepo;
        }
        catch (ArgumentException ae) {
            throw ae;
        }
        catch (Exception e) {
            log.error("MiningRepoService modifyMineRepo error!", e);
            throw new DBException(ErrorEnum.DB_ERROR.message(), e);
        }
    }

    public MiningRepoDetailDto findMineRepoDetail(MiningRepoDetailSearchVO miningRepoDetailSearchVO) {
        log.trace("perf: begin getMineReopById");
        MiningRepo mineRepo = miningRepoCacheManager.getMineReopById(Integer.valueOf(miningRepoDetailSearchVO.getRepoId()));
        MiningRepoTableDesc tableDesc = mineRepo.getDetailTableDescObj();
        log.trace("perf: end getMineReopById");
        StringBuilder sqlBuilder = new StringBuilder();
        StringBuilder countSqlBuilder = new StringBuilder();

        sqlBuilder.append("SELECT ").append(buildFieldsSql(tableDesc)).append(" FROM ")
                .append(tableDesc.getName()).append(" ")
                .append(buildWhereSql(tableDesc, miningRepoDetailSearchVO.getParams()))
                .append(" ").append(buildOrderBySql(tableDesc)).append(" limit ")
                .append(miningRepoDetailSearchVO.getSize()).append(" offset ")
                .append(miningRepoDetailSearchVO.getPage() * miningRepoDetailSearchVO.getSize());
        log.debug("findMineRepoDetail sql is: {}", sqlBuilder.toString());

        countSqlBuilder.append("SELECT count(*) FROM ").append(tableDesc.getName()).append(" ")
                .append(buildWhereSql(tableDesc, miningRepoDetailSearchVO.getParams()));
        log.debug("findMineRepoDetail count sql is: {}", countSqlBuilder.toString());

        log.trace("perf: end sqlBuilder");
//        Future<List<Map<String, Object>>> dataFuture = ThreadPoolUtil.getInstance()
//                .submit(() -> mineRepoDao.selectMineRepoDetail(sqlBuilder.toString()));
//        Future<Long> countFuture = ThreadPoolUtil.getInstance()
//                .submit(() -> mineRepoDao.countMineRepoDetail(countSqlBuilder.toString()));

        try {
            List<Map<String, Object>> list = mineRepoDao.selectMineRepoDetail(sqlBuilder.toString());
            log.trace("perf: end execute data sql");
            Long count = mineRepoDao.countMineRepoDetail(countSqlBuilder.toString());
            log.trace("perf: end execute count sql");
            MiningRepoDetailDto data = new MiningRepoDetailDto(tableDesc.getFields(), list, count,
                    miningRepoDetailSearchVO.getPage(), miningRepoDetailSearchVO.getSize());
            log.trace("perf: before return");
            return data;
        } catch (Exception e) {
            log.error("error findMineRepoDetail", e);
            throw new ArgumentException("findMineRepoDetail impala search error", e);
        }
    }

    private String buildFieldsSql(MiningRepoTableDesc mineRepoTableDesc) {
        StringBuilder sqlBuilder = new StringBuilder();

        int size = mineRepoTableDesc.getFields().size();
        for (int i = 0; i < size; i++) {
            sqlBuilder.append(mineRepoTableDesc.getFields().get(i).getName());
            if (i < size - 1) {
                sqlBuilder.append(",");
            }
        }
        return sqlBuilder.toString();
    }

    private MiningRepoTableDesc.FieldDesc getFiedDesc(MiningRepoTableDesc mineRepoTableDesc, String fieldName) {
        for (MiningRepoTableDesc.FieldDesc f : mineRepoTableDesc.getFields()) {
            if (f.getName().equals(fieldName)) {
                return f;
            }
        }
        return null;
    }

    private String buildWhereSql(MiningRepoTableDesc mineRepoTableDesc, Map<String, String> searchParam) {
        StringBuilder whereSqlBuilder = new StringBuilder();

        searchParam.put(getDeleteField(mineRepoTableDesc).getName(), "0");

        int i = 0;
        for (Map.Entry<String, String> entry : searchParam.entrySet()) {
            String fieldName = entry.getKey();
            String fieldValue = entry.getValue();
            MiningRepoTableDesc.FieldDesc fieldDesc = getFiedDesc(mineRepoTableDesc, fieldName);

            if (StringUtils.isBlank(fieldValue)) {
                continue;
            }

            if (fieldDesc == null || !fieldDesc.getSearchable()) {
                continue;
            }

            if (i == 0) {
                whereSqlBuilder.append("WHERE ");
            }

            if (i > 0) {
                whereSqlBuilder.append(" AND ");
            }

            buildFieldCondition(whereSqlBuilder, mineRepoTableDesc, fieldDesc, fieldName, fieldValue);

            i++;
        }


        return whereSqlBuilder.toString();
    }

    // a = 'b', a like '%b', a in ('b', 'c')
    private void buildFieldCondition(StringBuilder sqlBuilder, MiningRepoTableDesc mineRepoTableDesc,
                                     MiningRepoTableDesc.FieldDesc fieldDesc, String fieldName, String fieldValue) {
        boolean isStr = fieldDesc.getType().equalsIgnoreCase("string");

        if (isStr) {
            sqlBuilder.append("lower(");
        }
        sqlBuilder.append(fieldName);
        if (isStr) {
            sqlBuilder.append(")");
        }

        String searchSign = fieldDesc.getSearchSign() == null ? "=" : fieldDesc.getSearchSign();
        if ("objType".equals(fieldDesc.getUiType()) && fieldValue.contains(",")) {
            searchSign = "in";
        }
        sqlBuilder.append(" ");
        sqlBuilder.append(searchSign);
        sqlBuilder.append(" ");

        if (searchSign.equals("in")) {
            sqlBuilder.append("(");
        }

        if (!searchSign.equals("in") && isStr) {
            sqlBuilder.append("'");
            fieldValue = fieldValue.toLowerCase();
        }
        if ("like".equalsIgnoreCase(fieldDesc.getSearchSign())) {
            sqlBuilder.append("%");
        }
        sqlBuilder.append(fieldValue);
        if ("like".equalsIgnoreCase(fieldDesc.getSearchSign())) {
            sqlBuilder.append("%");
        }
        if (!searchSign.equals("in") && isStr) {
            sqlBuilder.append("'");
        }

        if (searchSign.equals("in")) {
            sqlBuilder.append(")");
        }
    }

    private MiningRepoTableDesc.FieldDesc getDeleteField(MiningRepoTableDesc mineRepoTableDesc) {
        for (MiningRepoTableDesc.FieldDesc fieldDesc : mineRepoTableDesc.getFields()) {
            if (fieldDesc.getDeleteField() != null && fieldDesc.getDeleteField())
                return fieldDesc;
        }
        return null;
    }

    private MiningRepoTableDesc.FieldDesc getStartTimeField(MiningRepoTableDesc mineRepoTableDesc) {
        for (MiningRepoTableDesc.FieldDesc fieldDesc : mineRepoTableDesc.getFields()) {
            if ("startTime".equalsIgnoreCase(fieldDesc.getUiType()))
                return fieldDesc;
        }
        return null;
    }

    private MiningRepoTableDesc.FieldDesc getEndTimeField(MiningRepoTableDesc mineRepoTableDesc) {
        for (MiningRepoTableDesc.FieldDesc fieldDesc : mineRepoTableDesc.getFields()) {
            if ("endTime".equalsIgnoreCase(fieldDesc.getUiType()))
                return fieldDesc;
        }
        return null;
    }

    private MiningRepoTableDesc.FieldDesc getObjTypeField(MiningRepoTableDesc mineRepoTableDesc) {
        for (MiningRepoTableDesc.FieldDesc fieldDesc : mineRepoTableDesc.getFields()) {
            if ("objType".equalsIgnoreCase(fieldDesc.getUiType()))
                return fieldDesc;
        }
        return null;
    }

    private String buildOrderBySql(MiningRepoTableDesc mineRepoTableDesc) {
        StringBuilder sqlBuilder = new StringBuilder();
        List<MiningRepoTableDesc.OrderDesc> orderDesc = mineRepoTableDesc.getOrderBy();
        int i = 0;

        if (CollectionUtils.isEmpty(orderDesc)) {
            return "";
        }

        for (MiningRepoTableDesc.OrderDesc desc:  orderDesc) {
            if (i == 0) {
                sqlBuilder.append("order by ");
            }
            if (i > 0) {
                sqlBuilder.append(",");
            }
            sqlBuilder.append(desc.getName()).append(" ").append(desc.getOrder());
            i++;
        }


        return sqlBuilder.toString();
    }

    public void addMineRepoDetail(Integer repoId, Map<String, String> params) {
        MiningRepo mineRepo = miningRepoCacheManager.getMineReopById(repoId);
        MiningRepoTableDesc tableDesc = mineRepo.getDetailTableDescObj();
        StringBuilder insertSql = new StringBuilder();
        Map<MiningRepoTableDesc.FieldDesc, String> fieldMap = new HashMap<>();

        checkAddDetail(tableDesc, params);

        for (MiningRepoTableDesc.FieldDesc f : tableDesc.getFields()) {
            if (f.getSourceField() != null && f.getSourceField()) {
                fieldMap.put(f, "" + MiningRepoSourceEnum.MANUAL.code());
            }
            if (f.getDeleteField() != null && f.getDeleteField()) {
                fieldMap.put(f, "0");
            }
        }

        for (Map.Entry<String, String> entry : params.entrySet()) {
            fieldMap.put(getFiedDesc(tableDesc, entry.getKey()), entry.getValue());
        }

        insertSql.append("INSERT INTO ").append(tableDesc.getName()).append(" (");
        int i = 0;
        for (MiningRepoTableDesc.FieldDesc key : fieldMap.keySet()) {
            if (i > 0) {
                insertSql.append(",");
            }
            insertSql.append(key.getName());
            i++;
        }
        insertSql.append(") values (");

        int j = 0;
        for (MiningRepoTableDesc.FieldDesc key : fieldMap.keySet()) {
            if (j > 0) {
                insertSql.append(",");
            }
            if (key.getType().equalsIgnoreCase("string")) {
                insertSql.append("'");
            }
            insertSql.append(fieldMap.get(key));
            if (key.getType().equalsIgnoreCase("string")) {
                insertSql.append("'");
            }
            j++;
        }
        insertSql.append(")");

        log.debug("add mining repo detail, insertSql is: {}", insertSql);

        mineRepoDao.insertMineRepoDetail(insertSql.toString());

        updateMiningRepoDetailCount(mineRepo);
    }

    private void checkAddDetail(MiningRepoTableDesc tableDesc, Map<String, String> params) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT count(1) as cnt FROM ");
        sqlBuilder.append(tableDesc.getName());
        sqlBuilder.append(" WHERE ");
        for (int i = 0; i < tableDesc.getPkFields().size(); i++) {
            MiningRepoTableDesc.FieldDesc f = tableDesc.getPkFields().get(i);
            if (i != 0) {
                sqlBuilder.append(" AND ");
            }
            sqlBuilder.append(f.getName());
            sqlBuilder.append("=");
            if ("string".equalsIgnoreCase(f.getType())) {
                sqlBuilder.append("'");
            }
            sqlBuilder.append(params.get(f.getName()));
            if ("string".equalsIgnoreCase(f.getType())) {
                sqlBuilder.append("'");
            }
        }

        long details = mineRepoDao.countMineRepoDetail(sqlBuilder.toString());
        if (details > 0) {
            throw new ArgumentException("已存在相同的数据!");
        }
    }

    public void deleteMineRepoDetail(Integer repoId, Map<String, String> params) throws IllegalArgumentException {
        MiningRepo mineRepo = miningRepoCacheManager.getMineReopById(repoId);
        MiningRepoTableDesc tableDesc = mineRepo.getDetailTableDescObj();
        StringBuilder deleteSql = new StringBuilder();
        Map<MiningRepoTableDesc.FieldDesc, String> fieldMap = new HashMap<>();
        MiningRepoTableDesc.FieldDesc deleteFlagField = null;

        for (MiningRepoTableDesc.FieldDesc f : tableDesc.getFields()) {
            if (f.getPK() != null && f.getPK()) {
                if (StringUtils.isEmpty(params.get(f.getName()))) {
                    throw new IllegalArgumentException("缺少必需的参数");
                }
                fieldMap.put(f, params.get(f.getName()));
            }

            if (f.getDeleteField() != null && f.getDeleteField()) {
                deleteFlagField = f;
            }
        }

        deleteSql.append("UPDATE ").append(tableDesc.getName()).append(" SET ");
        deleteSql.append(deleteFlagField.getName()).append("=");
        if (deleteFlagField.getType().equalsIgnoreCase("string")) {
            deleteSql.append("'");
        }
        deleteSql.append(1);
        if (deleteFlagField.getType().equalsIgnoreCase("string")) {
            deleteSql.append("'");
        }

        deleteSql.append(" WHERE ");

        int i = 0;
        for (MiningRepoTableDesc.FieldDesc key : fieldMap.keySet()) {
            if (i > 0) {
                deleteSql.append(" AND ");
            }

            deleteSql.append(key.getName());
            deleteSql.append("=");
            if (key.getType().equalsIgnoreCase("string")) {
                deleteSql.append("'");
            }
            deleteSql.append(fieldMap.get(key));
            if (key.getType().equalsIgnoreCase("string")) {
                deleteSql.append("'");
            }

            i++;
        }

        log.debug("delete mining repo detail, insertSql is: {}", deleteSql);

        mineRepoDao.deleteMineRepoDetail(deleteSql.toString());

        updateMiningRepoDetailCount(mineRepo);
    }
// FIXME: 2019/4/28 二次碰撞导入
//    public void importRepo(RepoImportRequest iRequest) {
//        int count = requestHistoryDao.findResultName(iRequest.getCaseId().toString(), iRequest.getResultName());
//        if(count > 0){
//            throw new ValidException("结果集名称已存在，请更换名称");
//        }
//        MiningRepoTwoCollisionRequest request = new MiningRepoTwoCollisionRequest();
//        request.setCaseId(Integer.valueOf(iRequest.getCaseId()));
//        request.setResultName(iRequest.getResultName());
//        request.setOperateUserId(iRequest.getUserId());
//        request.setObjTypes(Arrays.asList("mac", "carno", "imei", "imsi"));
//        addToTwoCollision(iRequest.getRepoId().intValue(), request, iRequest.getRepoType());
//    }
//
//    public void addToTwoCollision(Integer repoId, MiningRepoTwoCollisionRequest request, Integer repoType) {
//        MiningRepo mineRepo = miningRepoCacheManager.getMineReopById(repoId);
//
//        QueryHistory queryHistory = saveQueryHistory(mineRepo, request, repoType);
//        saveRequestHistory(queryHistory, request);
//    }
//
//    private QueryHistory saveQueryHistory(MiningRepo mineRepo, MiningRepoTwoCollisionRequest request, Integer repoType) {
//        MiningRepoTableDesc tableDesc = mineRepo.getDetailTableDescObj();
//        long resultCount = countMiningRepoDetail(tableDesc, request.getObjTypes(), request.getStartTime(), request.getEndTime());
//        if(resultCount <= 0){
//            throw new ValidException("数据挖掘库中内容为空，无法导入");
//        }
//        QueryHistory queryHistory = new QueryHistory();
//        Date curDate = new Date();
//
//        MiningQueryHistoryParam historyParam = new MiningQueryHistoryParam();
//        queryHistory.setRequestType(RequestTypeEnum.MINING_REPO.typeName());
//        if(repoType != null){
////            queryHistory.setRequestType(RequestTypeEnum.REPO_IMPORT.typeName());
//            historyParam.setRepoType(repoType);
//        } else {
//            historyParam.setStartTime(request.getStartTime());
//            historyParam.setEndTime(request.getEndTime());
//        }
//        historyParam.setRepoId(mineRepo.getId());
//        historyParam.setObjTypes(request.getObjTypes());
//
//        historyParam.setRepoName(mineRepo.getRepoName());
//        String param = JSON.toJSONString(historyParam, SerializerFeature.DisableCircularReferenceDetect);
//        queryHistory.setRequestParameter(param);
//        queryHistory.setMd5(DigestUtils.md5Hex(param));
//
//        queryHistory.setStatus(CalculateStatusEnum.SUCCEED.code());
//        queryHistory.setCreateDate(curDate);
//        queryHistory.setUpdateDate(curDate);
//
//        queryHistory.setResultCount(resultCount);
//
//        try {
//            queryHistoryDao.save(queryHistory);
//        } catch (Exception e) {
//            log.error("query history mysql insert error, request: {}", request);
//            throw new DBException("query history mysql insert error", e);
//        }
//
//        return queryHistory;
//    }
//
//    private RequestHistory saveRequestHistory(QueryHistory queryHistory, MiningRepoTwoCollisionRequest request) {
//
//        if (request.getCaseId() == null) {
//            log.warn("case id is null: {}", request);
//            throw new ArgumentException("caseId cannot be null");
//        }
//        RequestHistory requestHistory = new RequestHistory();
//        requestHistory.setCaseId(String.valueOf(request.getCaseId()));
//        requestHistory.setUserId(request.getOperateUserId());
//        requestHistory.setQueryId(queryHistory.getId());
//        Date curDate = new Date();
//        requestHistory.setRequestDate(curDate);
//        requestHistory.setCreateDate(curDate);
//        requestHistory.setUpdateDate(curDate);
//        requestHistory.setResultName(request.getResultName());
//        requestHistory.setValid(true);
//        requestHistory.setTwoCollision(true);
//        try {
//            requestHistoryDao.save(requestHistory);
//        } catch (Exception e) {
//            log.error("request history mysql insert error, request: {}", request);
//            throw new DBException("request history mysql insert error", e);
//        }
//        return requestHistory;
//    }

    public void importFromCsv(Integer repoId, MultipartFile file) {
        MiningRepo mineRepo = miningRepoCacheManager.getMineReopById(repoId);
        MiningRepoTableDesc tableDesc = mineRepo.getDetailTableDescObj();
        try {
            String encoding = detectEncoding(file);
            CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream(), encoding));
            List<String[]> content = csvReader.readAll();
            String[] headers = content.remove(0);
            List<MiningRepoTableDesc.FieldDesc> fields = getFieldsFromDesc(tableDesc,
                    getMineRepoFieldDescs(tableDesc).toArray(new String[]{}));

            // check header
            if (headers.length < fields.size()) {
                throw new IllegalArgumentException("文件内容缺少列, 请按照模板修改后导入");
            } else if (headers.length > fields.size()) {
                throw new IllegalArgumentException("文件列数太多, 请按照模板修改后导入");
            } else {
                for (int i = 0; i < headers.length; i++) {
                    if (!fields.get(i).getDesc().equalsIgnoreCase(headers[i].trim())) {
                        throw new IllegalArgumentException("第" + (i + 1) + "列与模板不符，请检查后导入");
                    }
                }
            }

            // check contents
            for (int i = 0; i < content.size(); i++) {
                int emptyValIndex = -1;
                if (content.get(i).length > fields.size()) {
                    throw new IllegalArgumentException("第" + (i + 1) + "行字段数量多于模板字段数!");
                } else if (content.get(i).length < fields.size()) {
                    emptyValIndex = content.get(i).length;
                } else {
                    for (int j = 0; j < content.get(i).length; j++) {
                        if (StringUtils.isBlank(content.get(i)[j])) {
                            emptyValIndex = j;
                        }
                    }
                }

                if (emptyValIndex >= 0) {
                    throw new IllegalArgumentException("第" + (i + 1) + "行, 第" + (emptyValIndex + 1) + "列, "
                            + fields.get(emptyValIndex).getDesc()
                            + "不能为空!");
                }
            }

            fixObjTypes(fields, content);

            batchInsertMiningDetails(tableDesc, fields, content);

            updateMiningRepoDetailCount(mineRepo);
        } catch (IOException e) {
            log.error("error reading csv file content", e);
        }
    }

    private void fixObjTypes(List<MiningRepoTableDesc.FieldDesc> fields, List<String[]> content) {
        int objTypeIdx = -1;
        for (int i = 0; i < fields.size(); i++) {
            MiningRepoTableDesc.FieldDesc f = fields.get(i);
            if ("objType".equals(f.getUiType())) {
                objTypeIdx = i;
            }
        }

        if (objTypeIdx < 0) {
            return;
        }

        for (int i = 0; i < content.size(); i++) {
            String objType = content.get(i)[objTypeIdx];
            objType = objType.trim().toLowerCase();
            if (objType.trim().equalsIgnoreCase("车牌")) {
                objType = "carno";
            }
            content.get(i)[objTypeIdx] = objType;
        }

        return;
    }

    private String detectEncoding(MultipartFile file) throws IOException {
        UniversalDetector detector = new UniversalDetector(null);
        int nread;
        byte[] buf = new byte[1024];
        InputStream fis = file.getInputStream();
        while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
            detector.handleData(buf, 0, nread);
        }
        detector.dataEnd();

        String encoding = detector.getDetectedCharset();
        detector.reset();

        if (encoding == null) {
            encoding = "UTF-8";
        }
        return encoding;
    }

    private List<MiningRepoTableDesc.FieldDesc> getFieldsFromDesc(MiningRepoTableDesc tableDesc, String[] headers) {
        List<MiningRepoTableDesc.FieldDesc> fieldDescs = tableDesc.getFields();
        Map<String, MiningRepoTableDesc.FieldDesc> fieldDescMap = new HashMap<>();
        List<MiningRepoTableDesc.FieldDesc> fields = new ArrayList<>();

        for (MiningRepoTableDesc.FieldDesc f : fieldDescs) {
            fieldDescMap.put(f.getDesc(), f);
        }

        for (int i = 0; i < headers.length; i++) {
            fields.add(fieldDescMap.get(headers[i]));
        }

        return fields;
    }

    private void batchInsertMiningDetails(MiningRepoTableDesc tableDesc, List<MiningRepoTableDesc.FieldDesc> fields, List<String[]> content) {
        MiningRepoTableDesc.FieldDesc sourceField = getSourceField(tableDesc);
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("INSERT INTO ").append(tableDesc.getName()).append("(");
        for (int i = 0; i < fields.size(); i++) {
            sqlBuilder.append(fields.get(i).getName());
            sqlBuilder.append(",");
        }
        sqlBuilder.append(sourceField.getName());

        sqlBuilder.append(") VALUES ");
        for (int j = 0; j < content.size(); j++) {
            String[] values = content.get(j);
            sqlBuilder.append("(");

            for (int k = 0; k < values.length; k++) {
                boolean isString = fields.get(k).getType().equalsIgnoreCase("String");
                String value = values[k];

                if (isString) {
                    sqlBuilder.append("'");
                }
                if (fields.get(k).getUiType().equalsIgnoreCase("startTime")
                        || fields.get(k).getUiType().equalsIgnoreCase("endTime")) {
                    try {
                        value = "" + DateUtils.parseDateStrictly(value, IMPORT_DATE_PATTERNS).getTime();
                    } catch (ParseException e) {
                        log.error("", e);
                        throw new IllegalArgumentException("文件内容错误，请核对后重新导入");
                    }
                }
                sqlBuilder.append(value);
                if (isString) {
                    sqlBuilder.append("'");
                }
                sqlBuilder.append(",");
            }
            sqlBuilder.append(MiningRepoSourceEnum.IMPORT.code());

            sqlBuilder.append(")");

            if (j < content.size() - 1) {
                sqlBuilder.append(",");
            }
        }

        try {
            mineRepoDao.batchInsertRepoDetails(sqlBuilder.toString());
        } catch (Exception e) {
            log.error("insert error", e);
        }
    }

    private MiningRepoTableDesc.FieldDesc getSourceField(MiningRepoTableDesc tableDesc) {
        List<MiningRepoTableDesc.FieldDesc> fields = tableDesc.getFields();
        for (MiningRepoTableDesc.FieldDesc f : fields) {
            if (f.getSourceField() != null && f.getSourceField()) {
                return f;
            }
        }
        return null;
    }

    public List<String> getMineRepoFieldDescs(MiningRepoTableDesc tableDesc) {
        List<MiningRepoTableDesc.FieldDesc> fields = tableDesc.getFields();
        List<String> descs = Lists.newArrayList();
        for (MiningRepoTableDesc.FieldDesc f : fields) {
            if (StringUtils.isNotBlank(f.getDesc()))
                descs.add(f.getDesc());
        }
        return descs;
    }

    public Long countMiningRepoDetail(MiningRepoTableDesc tableDesc, List<String> objTypes, Long startTime, Long endTime) {
        StringBuilder countSqlBuilder = new StringBuilder();

        MiningRepoTableDesc.FieldDesc deleteField = getDeleteField(tableDesc);
        MiningRepoTableDesc.FieldDesc startTimeField = getStartTimeField(tableDesc);
        MiningRepoTableDesc.FieldDesc endTimeField = getEndTimeField(tableDesc);
        MiningRepoTableDesc.FieldDesc objTypeField = getObjTypeField(tableDesc);
        countSqlBuilder.append("SELECT count(*) FROM ").append(tableDesc.getName())
                .append(" WHERE ").append(deleteField.getName()).append("=0");
        if (startTime != null) {
            countSqlBuilder.append(" AND ").append(startTimeField.getName()).append(startTimeField.getSearchSign())
                    .append(startTime);
        }
        if (endTime != null) {
            countSqlBuilder.append(" AND ").append(endTimeField.getName()).append(endTimeField.getSearchSign())
                    .append(endTime);
        }
        if (CollectionUtils.isNotEmpty(objTypes)) {
            countSqlBuilder.append(" AND lower(").append(objTypeField.getName()).append(")").append(" in (");
            for (int i = 0; i < objTypes.size(); i++) {
                String objType = objTypes.get(i);
                if (i > 0) {
                    countSqlBuilder.append(",");
                }
                countSqlBuilder.append("'").append(objType.toLowerCase()).append("'");
            }
            countSqlBuilder.append(")");
        }

        return mineRepoDao.countMineRepoDetail(countSqlBuilder.toString());
    }

    public void updateMiningRepoDetailCount(MiningRepo miningRepo) {
        MiningRepoTableDesc tableDesc = JSON.parseObject(miningRepo.getDetailTableDesc(), MiningRepoTableDesc.class);
        long count = countMiningRepoDetail(tableDesc, null, null, null);
        mineRepoDao.updateMineRepoDetailCount(miningRepo.getId(), count);
    }

    public List<RepoResponse> getRepoByType() {
        return mineRepoDao.getRepoInfo();
    }

    public RepoSearchResultDto search(RepoSearchVo searchVo) {
        try {
            long start = System.currentTimeMillis();
            int page = searchVo.getPage();
            int size = searchVo.getSize();
            int offset = page * size;
            RepoSearchResultDto resultDto = new RepoSearchResultDto();
            List<String> values = repoSearchManager.trimQueryString(searchVo.getValue());
            List<MiningRepo> miningRepos = miningRepoCacheManager.getAllMineRepo();//获取所有挖掘库
            Future<List<Map<String, Object>>> dataFuture = ThreadPoolUtil.getInstance().submit(() -> mineRepoDao.search(miningRepos, values, size, offset));
            Future<List<Map<String, Object>>> groupFuture = ThreadPoolUtil.getInstance().submit(() -> mineRepoDao.groupByObjType(miningRepos, values));
            List<Map<String, Object>> result = dataFuture.get();
//          Long count = mineRepoDao.countSearch(miningRepos, searchVo.getValue());
            List<Map<String, Object>> groupResult = groupFuture.get();
            log.info("mingingRepo search cost:{}ms", (System.currentTimeMillis()-start));
            int count = groupResult.stream().mapToInt(a -> Integer.valueOf(a.get("type_count").toString())).sum();
            resultDto.setRepoName("数据挖掘库");
            resultDto.setDatas(result);
            resultDto.setGroupMap(groupResult);
            resultDto.setTotal((long)count);
            return resultDto;
        } catch (Exception e) {
            throw new DBException("search miningRepo error", e);
        }
    }
}
