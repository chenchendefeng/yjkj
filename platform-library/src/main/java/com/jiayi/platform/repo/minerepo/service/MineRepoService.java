package com.jiayi.platform.repo.minerepo.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.jiayi.platform.common.enums.CollectType;
import com.jiayi.platform.common.enums.ErrorEnum;
import com.jiayi.platform.common.exception.ArgumentException;
import com.jiayi.platform.common.exception.DBException;
import com.jiayi.platform.common.exception.ServiceException;
import com.jiayi.platform.common.util.MyDateUtil;
import com.jiayi.platform.common.util.ThreadPoolUtil;
import com.jiayi.platform.common.web.util.ExportUtil;
import com.jiayi.platform.repo.minerepo.dao.MineRepoDao;
import com.jiayi.platform.repo.minerepo.dto.MinePageDto;
import com.jiayi.platform.repo.minerepo.dto.MineRepoDetailDto;
import com.jiayi.platform.repo.minerepo.dto.MineRepoDto;
import com.jiayi.platform.repo.minerepo.entity.MineRepo;
import com.jiayi.platform.repo.minerepo.vo.MineRepoRequest;
import com.jiayi.platform.repo.minerepo.vo.MineRepoSearchVo;
import com.jiayi.platform.repo.minerepo.vo.MineRepoTableDesc;
import com.jiayi.platform.repo.minerepo.vo.MiningRepoDetailSearchVO;
import com.jiayi.platform.security.core.dao.DepartmentDao;
import com.jiayi.platform.security.core.entity.Department;
import com.opencsv.CSVReader;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
public class MineRepoService {
    private static Logger log = LoggerFactory.getLogger(MineRepoService.class);
// TODO: 2019/4/24 jar or 远程接口 
//    @Autowired
//    private QueryHistoryDao queryHistoryDao;
//
//    @Autowired
//    private RequestHistoryDao requestHistoryDao;

    @Autowired
    private MineRepoDao mineRepoDao;

    @Autowired
    private DepartmentDao departmentDao;

    public MinePageDto<MineRepoDto> findMineRepoList(MineRepoSearchVo mineRepoSearchVo) {
        List<MineRepoDto> mineRepoDtoAll = mineRepoDao.selectMineRepoList(mineRepoSearchVo);

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
            return new MinePageDto<MineRepoDto>(Lists.newArrayList(), (long) count, mineRepoSearchVo.getPage(), 0);
        }
        List<MineRepoDto> list = mineRepoDtoAll.subList(start, end);
        List<Department> departments = departmentDao.findAllById(list.stream().filter(a -> a.getDepartmentId() != null).map(b -> b.getDepartmentId()).collect(Collectors.toList()));
        for (MineRepoDto mineRepoDto : list.stream().filter(a -> a.getDepartmentId() != null).collect(Collectors.toList())) {
            for (Department department : departments) {
                if (mineRepoDto.getDepartmentId().equals(department.getId())) {
                    mineRepoDto.setDepartment(department);
                    break;
                }
            }
        }
        return new MinePageDto<MineRepoDto>(list, (long)count, mineRepoSearchVo.getPage(), list.size());
    }

    public MineRepo modifyMineRepo(Integer id, MineRepoRequest mineRepoRequest) {
        try {
            MineRepo mineRepo = mineRepoDao.selectMineRepoById(id);
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
            return mineRepo;
        }
        catch (ArgumentException ae) {
            throw ae;
        }
        catch (Exception e) {
            log.error("MineRepoService modifyMineRepo error!", e);
            throw new DBException(ErrorEnum.DB_ERROR.message(), e);
        }
    }

    public MineRepoDetailDto findMineRepoDetail(MiningRepoDetailSearchVO miningRepoDetailSearchVO) {
        MineRepo mineRepo = mineRepoDao.selectMineRepoById(Integer.valueOf(miningRepoDetailSearchVO.getRepoId()));
        MineRepoTableDesc tableDesc = JSON.parseObject(mineRepo.getDetailTableDesc(), MineRepoTableDesc.class);
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

        Future<List<Map<String, Object>>> dataFuture = ThreadPoolUtil.getInstance()
                .submit(() -> mineRepoDao.selectMineRepoDetail(sqlBuilder.toString()));
        Future<Long> countFuture = ThreadPoolUtil.getInstance()
                .submit(() -> mineRepoDao.countMineRepoDetail(countSqlBuilder.toString()));

        try {
            List<Map<String, Object>> list = dataFuture.get();
            Long count = countFuture.get();

            MineRepoDetailDto data = new MineRepoDetailDto(tableDesc.getFields(), list, count,
                    miningRepoDetailSearchVO.getPage(), miningRepoDetailSearchVO.getSize());
            return data;
        } catch (Exception e) {
            log.error("error findMineRepoDetail", e);
            throw new ArgumentException("findMineRepoDetail impala search error", e);
        }
    }

    private String buildFieldsSql(MineRepoTableDesc mineRepoTableDesc) {
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

    private MineRepoTableDesc.FieldDesc getFiedDesc(MineRepoTableDesc mineRepoTableDesc, String fieldName) {
        for (MineRepoTableDesc.FieldDesc f : mineRepoTableDesc.getFields()) {
            if (f.getName().equals(fieldName)) {
                return f;
            }
        }
        return null;
    }

    private String buildWhereSql(MineRepoTableDesc mineRepoTableDesc, Map<String, String> searchParam) {
        StringBuilder whereSqlBuilder = new StringBuilder();

        searchParam.put(getDeleteField(mineRepoTableDesc).getName(), "0");

        int i = 0;
        for (Map.Entry<String, String> entry : searchParam.entrySet()) {
            String fieldName = entry.getKey();
            String fieldValue = entry.getValue();
            MineRepoTableDesc.FieldDesc fieldDesc = getFiedDesc(mineRepoTableDesc, fieldName);
            boolean isStr = fieldDesc.getType().equalsIgnoreCase("string");

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

            if (isStr) {
                whereSqlBuilder.append("lower(");
            }
            whereSqlBuilder.append(fieldName);
            if (isStr) {
                whereSqlBuilder.append(")");
            }

            whereSqlBuilder.append(fieldDesc.getSearchSign() == null ? "=" : fieldDesc.getSearchSign());
            if (isStr) {
                whereSqlBuilder.append("'");
                fieldValue = fieldValue.toLowerCase();
            }
            if ("like".equalsIgnoreCase(fieldDesc.getSearchSign())) {
                whereSqlBuilder.append("%");
            }
            whereSqlBuilder.append(fieldValue);
            if ("like".equalsIgnoreCase(fieldDesc.getSearchSign())) {
                whereSqlBuilder.append("%");
            }
            if (isStr) {
                whereSqlBuilder.append("'");
            }

            i++;
        }


        return whereSqlBuilder.toString();
    }

    private MineRepoTableDesc.FieldDesc getDeleteField(MineRepoTableDesc mineRepoTableDesc) {
        for (MineRepoTableDesc.FieldDesc fieldDesc : mineRepoTableDesc.getFields()) {
            if (fieldDesc.getDeleteField() != null && fieldDesc.getDeleteField())
                return fieldDesc;
        }
        return null;
    }

    private String buildOrderBySql(MineRepoTableDesc mineRepoTableDesc) {
        StringBuilder sqlBuilder = new StringBuilder();
        List<MineRepoTableDesc.OrderDesc> orderDesc = mineRepoTableDesc.getOrderBy();
        int i = 0;

        if (CollectionUtils.isEmpty(orderDesc)) {
            return "";
        }

        for (MineRepoTableDesc.OrderDesc desc:  orderDesc) {
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
        MineRepo mineRepo = mineRepoDao.selectMineRepoById(repoId);
        MineRepoTableDesc tableDesc = JSON.parseObject(mineRepo.getDetailTableDesc(), MineRepoTableDesc.class);
        StringBuilder insertSql = new StringBuilder();
        Map<MineRepoTableDesc.FieldDesc, String> fieldMap = new HashMap<>();

        for (MineRepoTableDesc.FieldDesc f : tableDesc.getFields()) {
            if (f.getSourceField() != null && f.getSourceField()) {
                fieldMap.put(f, "1");
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
        for (MineRepoTableDesc.FieldDesc key : fieldMap.keySet()) {
            if (i > 0) {
                insertSql.append(",");
            }
            insertSql.append(key.getName());
            i++;
        }
        insertSql.append(") values (");

        int j = 0;
        for (MineRepoTableDesc.FieldDesc key : fieldMap.keySet()) {
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
    }

    public void deleteMineRepoDetail(Integer repoId, Map<String, String> params) throws IllegalArgumentException {
        MineRepo mineRepo = mineRepoDao.selectMineRepoById(repoId);
        MineRepoTableDesc tableDesc = JSON.parseObject(mineRepo.getDetailTableDesc(), MineRepoTableDesc.class);
        StringBuilder deleteSql = new StringBuilder();
        Map<MineRepoTableDesc.FieldDesc, String> fieldMap = new HashMap<>();
        MineRepoTableDesc.FieldDesc deleteFlagField = null;

        for (MineRepoTableDesc.FieldDesc f : tableDesc.getFields()) {
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
        for (MineRepoTableDesc.FieldDesc key : fieldMap.keySet()) {
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
    }
// FIXME: 2019/4/28 放到judge的二次碰撞中去
//    public void addToTwoCollision(Integer repoId, MiningRepoTwoCollisionRequest request) {
//        MineRepo mineRepo = mineRepoDao.selectMineRepoById(repoId);
//
//        QueryHistory queryHistory = saveQueryHistory(mineRepo, request);
//        saveRequestHistory(queryHistory, request);
//    }
//
//    private QueryHistory saveQueryHistory(MineRepo mineRepo, MiningRepoTwoCollisionRequest request) {
//        MineRepoTableDesc tableDesc = JSON.parseObject(mineRepo.getDetailTableDesc(), MineRepoTableDesc.class);
//        QueryHistory queryHistory = new QueryHistory();
//        Date curDate = new Date();
//
//        MiningQueryHistoryParam historyParam = new MiningQueryHistoryParam();
//        historyParam.setObjTypes(request.getObjTypes());
//        historyParam.setTableName(tableDesc.getName());
//        historyParam.setStartTime(request.getStartTime());
//        historyParam.setEndTime(request.getEndTime());
//        String param = JSON.toJSONString(historyParam, SerializerFeature.DisableCircularReferenceDetect);
//        queryHistory.setRequestParameter(param);
//        queryHistory.setMd5(DigestUtils.md5Hex(param));
//        // TODO: use RequestTypeEnum
//        queryHistory.setRequestType("mining_repo");
//        // TODO: use CalculateStatusEnum
//        queryHistory.setStatus(4);
//        queryHistory.setCreateDate(curDate);
//        queryHistory.setUpdateDate(curDate);
//        queryHistory.setResultCount(0L);
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

    public MineRepo getMineReopById(Integer id) {
        return mineRepoDao.selectMineRepoById(id);
    }

    public void importFromCsv(Integer repoId, MultipartFile file) {
        MineRepo mineRepo = mineRepoDao.selectMineRepoById(repoId);
        MineRepoTableDesc tableDesc = JSON.parseObject(mineRepo.getDetailTableDesc(), MineRepoTableDesc.class);
        try {
            CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()));
            List<String[]> content = csvReader.readAll();
            String[] headers = content.remove(0);
            List<MineRepoTableDesc.FieldDesc> fields = getFieldsFromDesc(tableDesc, headers);
            batchInsertMiningDetails(tableDesc, fields, content);
        } catch (IOException e) {
            log.error("error reading csv file content", e);
        }
    }

    private List<MineRepoTableDesc.FieldDesc> getFieldsFromDesc(MineRepoTableDesc tableDesc, String[] headers) {
        List<MineRepoTableDesc.FieldDesc> fieldDescs = tableDesc.getFields();
        Map<String, MineRepoTableDesc.FieldDesc> fieldDescMap = new HashMap<>();
        List<MineRepoTableDesc.FieldDesc> fields = new ArrayList<>();

        for (MineRepoTableDesc.FieldDesc f : fieldDescs) {
            fieldDescMap.put(f.getDesc(), f);
        }

        for (int i = 0; i < headers.length; i++) {
            fields.add(fieldDescMap.get(headers[i]));
        }

        return fields;
    }

    private void batchInsertMiningDetails(MineRepoTableDesc tableDesc, List<MineRepoTableDesc.FieldDesc> fields, List<String[]> content) {
        MineRepoTableDesc.FieldDesc sourceField = getSourceField(tableDesc);
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
                if (isString) {
                    sqlBuilder.append("'");
                }
                sqlBuilder.append(values[k]);
                if (isString) {
                    sqlBuilder.append("'");
                }
                sqlBuilder.append(",");
            }
            sqlBuilder.append(2);

            sqlBuilder.append(")");

            if (j < content.size() - 1) {
                sqlBuilder.append(",");
            }
        }

        mineRepoDao.batchInsertRepoDetails(sqlBuilder.toString());
    }

    private MineRepoTableDesc.FieldDesc getSourceField(MineRepoTableDesc tableDesc) {
        List<MineRepoTableDesc.FieldDesc> fields = tableDesc.getFields();
        for (MineRepoTableDesc.FieldDesc f : fields) {
            if (f.getSourceField() != null && f.getSourceField()) {
                return f;
            }
        }
        return null;
    }

    public void exportMineRepoDetail(Integer repoId, HttpServletResponse response){
        MiningRepoDetailSearchVO miningRepoDetailSearchVO = new MiningRepoDetailSearchVO();
        miningRepoDetailSearchVO.setPage(0);
        miningRepoDetailSearchVO.setSize(50000);
        miningRepoDetailSearchVO.setRepoId(repoId);
        MineRepoDetailDto mineRepoDetail = findMineRepoDetail(miningRepoDetailSearchVO);
        List<Map<String, Object>> data = mineRepoDetail.getData();
        List<MineRepoTableDesc.FieldDesc> titles = mineRepoDetail.getTitles();
        List<String> contents = new ArrayList<>();
        List<String[]> rowData = new ArrayList<>();
        String colNames = "";
        String fileName = "MineRepo_" + System.currentTimeMillis();
        try {
            List<String> descs = titles.stream().map(MineRepoTableDesc.FieldDesc::getDesc).filter(a -> StringUtils.isNotBlank(a)).collect(Collectors.toList());
            colNames = StringUtils.join(descs, ",");
            data.forEach(item -> {
                String[] rowValue = new String[descs.size()];
                int j = 0;
                for(MineRepoTableDesc.FieldDesc a : titles) {
                    if (StringUtils.isBlank(a.getDesc()))
                        continue;
                    if (a.getType().equalsIgnoreCase("Bigint")) {//目前Bigint只有日期类型，后期用带“时间”字眼判断？
                        rowValue[j++] = MyDateUtil.getDateStr(Long.parseLong(item.get(a.getName()).toString()));
                    } else {
                        rowValue[j++] = CollectType.getByLabel(item.get(a.getName()).toString()).desc();
                    }
                }
                rowData.add(rowValue);
            });
        }catch (Exception e){
            log.error("convert mine repo detail data error:",e);// 至少要有4个字段
        }
        contents.add(ExportUtil.genContentByStringList(rowData));
        if (!ExportUtil.doExport(contents, colNames, fileName, response)) {
            log.error("writing csv file error!");
            throw new ServiceException("writing csv file error!");
        }
    }
}
