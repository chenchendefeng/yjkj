package com.jiayi.platform.judge.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jiayi.platform.common.exception.DBException;
import com.jiayi.platform.common.util.ThreadPoolUtil;
import com.jiayi.platform.common.web.dto.JsonObject;
import com.jiayi.platform.common.web.enums.MessageCodeEnum;
import com.jiayi.platform.judge.common.bean.PageInfo;
import com.jiayi.platform.judge.common.bean.PageRequest;
import com.jiayi.platform.judge.common.bean.PageResult;
import com.jiayi.platform.judge.dao.mysql.RequestHistoryInfoDao;
import com.jiayi.platform.judge.response.HistoryRequestResponse;
import com.jiayi.platform.judge.entity.mysql.QueryHistory;
import com.jiayi.platform.judge.entity.mysql.RequestHistoryInfo;
import com.jiayi.platform.judge.enums.RequestType;
import com.jiayi.platform.judge.manage.RequestHistoryManager;
import com.jiayi.platform.judge.request.*;
import com.jiayi.platform.security.core.entity.UserBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import java.util.*;
import java.util.concurrent.Future;

@Service
@Slf4j
public class HistoryQueryService {
    @Autowired
    private RequestHistoryManager requestHistoryManager;
    @Autowired
    private RequestHistoryInfoDao requestHistoryInfoDao;

    private static final String AGGREGATE_COLLISION_STRING = "twocollisions";
    private static final String JUDGE_RECORD_STRING = "analysisrecord";
    @Value("${fileimport.errordata.validdate:30}")
    private String validDate;

    public PageResult<List<HistoryRequestResponse>> searchQueryRequest(RequestSearchRequest searchRequest, PageRequest pageRequest) {
        PageResult<List<HistoryRequestResponse>> result = new PageResult<>();

        Specification<RequestHistoryInfo> specification = new Specification<RequestHistoryInfo>() {
            @Override
            public Predicate toPredicate (Root<RequestHistoryInfo> root, @NonNull CriteriaQuery<?> query, CriteriaBuilder builder) {
                Predicate predicate = builder.conjunction();
                predicate.getExpressions().add(builder.equal(root.get("valid"), 1));
                if (StringUtils.isNotBlank(searchRequest.getCaseId())) {
                    predicate.getExpressions().add(builder.equal(root.get("caseId"), searchRequest.getCaseId()));
                }
                if (StringUtils.isNotBlank(searchRequest.getRequestType())) {
                    Join<RequestHistoryInfo, QueryHistory> join = root.join("queryHistory", JoinType.LEFT);
                    if (searchRequest.getRequestType().equals(RequestType.RULE_ANALYSIS.typeName())) {
                        List<String> ruleTypeList = Arrays.asList(RequestType.LOCATION_ANALYSIS.typeName(),
                                RequestType.MOVEMENT_ANALYSIS.typeName());
                        predicate.getExpressions().add(join.get("requestType").in(ruleTypeList));
                    } else
                        predicate.getExpressions()
                                .add(builder.equal(join.get("requestType"), searchRequest.getRequestType()));
                }
                return predicate;
            }
        };
        Sort sort = new Sort(Sort.Direction.DESC, "requestDate");
        Pageable pageable = org.springframework.data.domain.PageRequest.of(pageRequest.getPageIndex().intValue(),
                pageRequest.getPageSize(), sort);
        Future<Page<RequestHistoryInfo>> dataFuture = ThreadPoolUtil.getInstance().submit(() -> requestHistoryInfoDao.findAll(specification, pageable));
        Future<Long> countFuture = ThreadPoolUtil.getInstance().submit(() -> requestHistoryInfoDao.count(specification));
        try {
            Page<RequestHistoryInfo> pageData = dataFuture.get();
            List<HistoryRequestResponse> data = new ArrayList<>();
            for (RequestHistoryInfo requestHistoryInfo : pageData) {
                data.add(new HistoryRequestResponse(requestHistoryInfo));
            }
            Long count = countFuture.get();
            PageInfo pageInfo = new PageInfo(data.size(), count, pageRequest);
            result.setPayload(data);
            result.setPageInfo(pageInfo);
        } catch (Exception e) {
            log.error("history request mysql search error", e);
            throw new DBException("history request mysql search error", e);
        }
        return result;
    }

    public JsonObject<?> searchRequestHistoryInfo(RequestRecordRequest requestRecordRequest) {
        if (requestRecordRequest.getPageNo() == null)
            requestRecordRequest.setPageNo(0);
        if (requestRecordRequest.getPageSize() == null)
            requestRecordRequest.setPageSize(10);
        if (requestRecordRequest.getCaseId() == null)
            return new JsonObject<>(null, MessageCodeEnum.FAILED.getCode(), "请选择案件");
        Specification<RequestHistoryInfo> specification = new Specification<RequestHistoryInfo>() {
            @Override
            public Predicate toPredicate (Root<RequestHistoryInfo> root, @NonNull CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<>();
                list.add(cb.equal(root.get("valid"), 1));
                if (requestRecordRequest.getCaseId() != null) {
                    Path<Integer> caseId = root.get("caseId");
                    list.add(cb.equal(caseId, requestRecordRequest.getCaseId()));
                }
                if (StringUtils.isNotBlank(requestRecordRequest.getRemark())) {
                    Path<String> remark = root.get("requestRemark");
                    Path<String> name = root.get("resultName");
                    Predicate remarkCondition1 = cb.like(remark, "%"+requestRecordRequest.getRemark()+"%");
                    Predicate remarkCondition2 = cb.like(name, "%"+requestRecordRequest.getRemark()+"%");
                    list.add(cb.or(remarkCondition1,remarkCondition2));
                }
                if (requestRecordRequest.getBeginDate() > 0) {
                    Date startDate = new Date(requestRecordRequest.getBeginDate());
                    Date endDate = new Date(requestRecordRequest.getEndDate());
                    list.add(cb.between(root.get("requestDate"), startDate, endDate));
                }
                Join<RequestHistoryInfo, UserBean> joinUser = root.join("user", JoinType.LEFT);
                if (CollectionUtils.isNotEmpty(requestRecordRequest.getUserIds())) {
                    CriteriaBuilder.In<Long> in = cb.in(joinUser.get("id"));
                    requestRecordRequest.getUserIds().forEach(a -> in.value(a.longValue()));
                    list.add(in);
                }
                Join<RequestHistoryInfo, QueryHistory> join = root.join("queryHistory", JoinType.LEFT);
                if (AGGREGATE_COLLISION_STRING.equals(requestRecordRequest.getOp())) {// 二次碰撞
                    getRequestTypeCondition(requestRecordRequest, root, cb, join, list);
                } else {
                    if (CollectionUtils.isNotEmpty(requestRecordRequest.getSearchTypes())) {
                        CriteriaBuilder.In<String> in = cb.in(join.get("requestType"));
                        requestRecordRequest.getSearchTypes().forEach(in::value);
                        list.add(in);
                    } else {
                        list.add(cb.notEqual(join.get("requestType"), RequestType.AGGREGATE_COLLISION.typeName()));
                        list.add(cb.notEqual(join.get("requestType"), RequestType.MINING_REPO.typeName()));
                        list.add(cb.notEqual(join.get("requestType"), RequestType.FILE_IMPORT.typeName()));
                        list.add(cb.notEqual(join.get("requestType"), RequestType.REPO_IMPORT.typeName()));
                    }
                }
                return cb.and(list.toArray(new Predicate[0]));
            }
        };
        Sort sort;
        if (AGGREGATE_COLLISION_STRING.equals(requestRecordRequest.getOp())) {
            sort = new Sort(Sort.Direction.DESC, "updateDate");
        } else {
            sort = new Sort(Sort.Direction.DESC, "requestDate");
        }
        Pageable pageable = org.springframework.data.domain.PageRequest.of(requestRecordRequest.getPageNo(),
                requestRecordRequest.getPageSize(), sort);
        Page<RequestHistoryInfo> pageResult = requestHistoryInfoDao.findAll(specification, pageable);
        List<RequestHistoryInfo> list = pageResult.getContent();
        list.forEach(a -> a.setValidDate(validDate));
        return new JsonObject<>(new com.jiayi.platform.common.web.dto.PageResult<>(list, pageResult.getTotalElements(),
                requestRecordRequest.getPageNo(), list.size()));
    }

    private void getRequestTypeCondition (RequestRecordRequest requestRecordRequest, Root<RequestHistoryInfo> root, CriteriaBuilder cb,
                                          Join<RequestHistoryInfo, QueryHistory> join, List<Predicate> list) {
        List<String> requestType = requestRecordRequest.getSecondSearchTypes();
        List<String> requestTypes = Lists.newArrayList();
        if (requestType != null && requestType.size() > 0) {
            if (requestType.contains(JUDGE_RECORD_STRING)) {
                requestTypes.add(RequestType.LINE_COLLISION.typeName());
                requestTypes.add(RequestType.AREA_COLLISION.typeName());
                requestTypes.add(RequestType.FOLLOW_COLLISION.typeName());
                requestTypes.add(RequestType.MULTI_TRACK_COLLISION.typeName());
                requestTypes.add(RequestType.APPEAR_COLLISION.typeName());
                requestTypes.add(RequestType.DISAPPEAR_COLLISION.typeName());
                requestTypes.add(RequestType.MULTI_FEATURE_ANALYSIS.typeName());
                requestTypes.add(RequestType.INTIMATE_RELATION_ANALYSIS.typeName());
                requestTypes.add(RequestType.DEVICE_ANALYSIS.typeName());
                //多余的类型，解决存量脏数据问题
                requestTypes.add(RequestType.LOCATION_ANALYSIS.typeName());
                requestTypes.add(RequestType.MOVEMENT_ANALYSIS.typeName());
                requestTypes.add(RequestType.TRACK_COMPARE.typeName());
                requestTypes.add(RequestType.TRACK_MERGE.typeName());
                requestTypes.add(RequestType.TRACK_QUERY.typeName());
            }
            if(requestType.contains("repo_import")){//库导入包含挖掘库
                requestTypes.add(RequestType.MINING_REPO.typeName());
            }
            requestType.forEach(a -> {
                if (!Arrays.asList(AGGREGATE_COLLISION_STRING, JUDGE_RECORD_STRING).contains(a)) {
                    requestTypes.add(a);
                }
            });
        }
        Predicate isTwoCollisionType = cb.equal(join.get("requestType"), RequestType.AGGREGATE_COLLISION.typeName());
        //仅查询真正的二次碰撞数据
        if (requestType != null && requestType.size() == 1 && requestType.contains(AGGREGATE_COLLISION_STRING)) {
            list.add(isTwoCollisionType);
        } else if (requestType != null && requestType.size() >= 1) {
            CriteriaBuilder.In<String> in = cb.in(join.get("requestType"));
            requestTypes.forEach(in::value);
            Predicate condition1 = cb.and(in, cb.equal(root.get("twoCollision"), 1));
            if(requestType.contains(AGGREGATE_COLLISION_STRING)){
                list.add(cb.or(isTwoCollisionType,condition1));
            }else{
                list.add(condition1);
            }
        } else {
            list.add(cb.or(isTwoCollisionType, cb.equal(root.get("twoCollision"), 1)));
        }
        list.add(cb.and(cb.isNotNull(join.get("id"))));
    }

    public JsonObject<?> searchType () {
        Map<String, String> datas = Maps.newHashMap();
        RequestType[] requestTypeEnums = RequestType.values();
        Arrays.stream(requestTypeEnums).forEach(a -> {
            if(a.isShow()) {
                datas.put(a.typeName(), a.description());
            }
        });
        return new JsonObject<>(datas);
    }

    public JsonObject<?> updateRemark(Long id, String remark) {
        try {
            requestHistoryManager.updateRequestRemark(id,remark);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new JsonObject<>(null, MessageCodeEnum.FAILED.getCode(), "操作失败");
        }
        return new JsonObject<>(true);
    }

    public JsonObject<?> deleteRequestRecord(List<Long> idList, String op) {
        if (idList == null || idList.size() == 0)
            return new JsonObject<>(false, MessageCodeEnum.FAILED.getCode(), "参数ids不能为空");
        try {
            if("delete".equals(op))
                idList.forEach(id -> requestHistoryManager.setValid(id, false));
            else if("remove".equals(op)) {
                idList.forEach(id -> requestHistoryManager.updateRequestTwoCollision(id, false, null));
            }else {
                return new JsonObject<>(null, MessageCodeEnum.FAILED.getCode(), "操作失败");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return new JsonObject<>(true);
    }

    public JsonObject<?> addAggregate(Long id, String resultName) {
        try {
            if(StringUtils.isBlank(resultName))
                return new JsonObject<>(null, MessageCodeEnum.FAILED.getCode(), "请输入结果集名称");
            requestHistoryManager.updateRequestTwoCollision(id, true, resultName);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new JsonObject<>(null, MessageCodeEnum.FAILED.getCode(), "操作失败");
        }
        return new JsonObject<>(true);
    }

    public JsonObject<?> removeAggregate(Long id) {
        try {
            requestHistoryManager.updateRequestTwoCollision(id, false, null);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new JsonObject<>(null, MessageCodeEnum.FAILED.getCode(), "操作失败");
        }
        return new JsonObject<>(true);
    }
}
