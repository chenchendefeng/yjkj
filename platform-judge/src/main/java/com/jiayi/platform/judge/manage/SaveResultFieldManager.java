package com.jiayi.platform.judge.manage;

import com.jiayi.platform.common.exception.ArgumentException;
import com.jiayi.platform.common.exception.DBException;
import com.jiayi.platform.common.util.JsonUtils;
import com.jiayi.platform.judge.dao.mysql.CollisionResultFieldDao;
import com.jiayi.platform.judge.entity.mysql.CollisionResultField;
import com.jiayi.platform.judge.entity.mysql.QueryHistory;
import com.jiayi.platform.judge.enums.FileImportTypeEnum;
import com.jiayi.platform.judge.enums.RequestType;
import com.jiayi.platform.judge.enums.ResultFieldEnum;
import com.jiayi.platform.judge.request.FileObjectTypeInfo;
import com.jiayi.platform.judge.request.JudgeRequest;
import com.jiayi.platform.judge.request.MiningQueryHistoryParam;
import com.jiayi.platform.judge.util.JudgeUtil;
import com.jiayi.platform.library.minerepo.entity.MiningRepo;
import com.jiayi.platform.library.minerepo.manager.MiningRepoCacheManager;
import com.jiayi.platform.library.minerepo.vo.MiningRepoTableDesc;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class SaveResultFieldManager {

    @Autowired
    private CollisionResultFieldDao collisionResultFieldDao;
    @Autowired
    private RequestHistoryManager requestHistoryManager;
    @Autowired
    private MiningRepoCacheManager miningRepoCacheManager;

    public void saveFileImportResultFields(Map<String, FileObjectTypeInfo> map, Long uid){
        List<CollisionResultField> resultFieldInfoList = new ArrayList<>();
        map.forEach((k, v) -> {
            CollisionResultField resultFieldInfo = new CollisionResultField();
            resultFieldInfo.setFieldName(k);
            resultFieldInfo.setFieldDesc(v.getFieldDesc());
            resultFieldInfo.setFieldType(FileImportTypeEnum.getTypeById(v.getFieldType()).getName());
            resultFieldInfo.setRequestTmplId(uid);
            resultFieldInfo.setRequestType(RequestType.FILE_IMPORT.typeName());
            resultFieldInfoList.add(resultFieldInfo);
        });
        try {
            collisionResultFieldDao.saveAll(resultFieldInfoList);
        } catch (Exception e) {
            throw new DBException("file import result field info mysql insert error", e);
        }
    }

    public List<CollisionResultField> getResultFieldList(Long requestId) {
        QueryHistory queryHistory = requestHistoryManager.getQueryHistoryByRequestId(requestId);
        if (queryHistory == null) {
            log.error("invalid request id");
            throw new ArgumentException("invalid request id");
        }
        List<CollisionResultField> resultFields = new ArrayList<>();
        if (queryHistory.getRequestType().equals(RequestType.AGGREGATE_COLLISION.typeName())
                || queryHistory.getRequestType().equals(RequestType.FILE_IMPORT.typeName())) {
            // 二次碰撞和文件导入的字段信息存储在collision_result_field表
            resultFields.addAll(collisionResultFieldDao.findByRequestTmplId(queryHistory.getId()));
        } else if (queryHistory.getRequestType().equals(RequestType.MINING_REPO.typeName())) {
            // 挖掘库的字段信息通过获取MiningRepoTableDesc查询
            MiningQueryHistoryParam param;
            try {
                param = JsonUtils.parse(queryHistory.getRequestParameter(), MiningQueryHistoryParam.class);
            } catch (IOException e) {
                throw new ArgumentException("invalid request parameter");
            }
            MiningRepo repo = miningRepoCacheManager.getMineReopById(param.getRepoId());
            MiningRepoTableDesc tableDesc = repo.getDetailTableDescObj();
            for (MiningRepoTableDesc.FieldDesc f : tableDesc.getFields()) {
                if (StringUtils.isBlank(f.getDesc())) {
                    continue;
                }
                CollisionResultField collisionResultField = new CollisionResultField();
                collisionResultField.setFieldName(f.getName());
                collisionResultField.setFieldType(f.getType());
                collisionResultField.setFieldDesc(f.getDesc());
                collisionResultField.setRequestType(RequestType.MINING_REPO.typeName());
                collisionResultField.setRequestTmplId(queryHistory.getId());
                resultFields.add(collisionResultField);
            }
        } else { // 常用库和其余碰撞分析字段信息在ResultFieldEnum中查
            JudgeRequest request;
            request = JudgeUtil.getJudgeRequest(queryHistory.getRequestParameter(),
                    RequestType.getRequestType(queryHistory.getRequestType()));

            List<ResultFieldEnum> resultInfoList = ResultFieldEnum.getResultInfoList(queryHistory.getRequestType(), request.getObjectTypeName());
            for (ResultFieldEnum resultField : resultInfoList) {
                CollisionResultField collisionResultField = new CollisionResultField();
                collisionResultField.setId(resultField.code().longValue()); // TODO 现在id应该没有用了？
                collisionResultField.setFieldName(resultField.resultName());
                collisionResultField.setFieldType(resultField.resultType());
                collisionResultField.setFieldDesc(resultField.resultDesc());
                collisionResultField.setRequestType(queryHistory.getRequestType());
                collisionResultField.setRequestTmplId(queryHistory.getId());
                resultFields.add(collisionResultField);
            }
        }
        return resultFields;
    }

    public String getResultFieldById(long id) {
        try {
            return collisionResultFieldDao.getResultFieldNameById(id);
        } catch (Exception e) {
            log.error("result field mysql search error", e);
            throw new DBException("result field mysql search error", e);
        }
    }

    public String getResultFieldByCode(int code) {
        ResultFieldEnum resultField = ResultFieldEnum.getResultFieldByCode(code);
        return resultField == null ? null : resultField.resultName();
    }
}
