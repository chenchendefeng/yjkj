package com.jiayi.platform.repo.minerepo.service;

import com.google.common.collect.Lists;
import com.jiayi.platform.common.exception.AuthException;
import com.jiayi.platform.common.exception.DBException;
import com.jiayi.platform.common.util.JWTUtil;
import com.jiayi.platform.common.util.ThreadPoolUtil;
import com.jiayi.platform.repo.minerepo.dao.MonitorRepoDao;
import com.jiayi.platform.repo.minerepo.dto.MonitorObjectDto;
import com.jiayi.platform.repo.minerepo.dto.RepoSearchResultDto;
import com.jiayi.platform.repo.minerepo.manager.RepoSearchManager;
import com.jiayi.platform.repo.minerepo.vo.PageRequest;
import com.jiayi.platform.repo.minerepo.vo.RepoResponse;
import com.jiayi.platform.repo.minerepo.vo.RepoSearchVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
public class MonitorRepoService {
    private static Logger log = LoggerFactory.getLogger(MonitorRepoService.class);

    @Autowired
    private MonitorRepoDao monitorRepoDao;
//    @Autowired
//    private QueryHistoryDao queryHistoryDao;
//    @Autowired
//    private RequestHistoryDao requestHistoryDao;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private RepoSearchManager repoSearchManager;

    // FIXME: 2019/4/28 移动到judge做二次碰撞
//    public void importRepo(RepoImportRequest request) {
//        int count = requestHistoryDao.findResultName(request.getCaseId().toString(), request.getResultName());
//        if(count > 0){
//            throw new ValidException("结果集名称已存在，请更换名称");
//        }
//        Long queryId = saveQueryHistory(request);
//        saveRequestHistory(queryId, request);
//    }
//
//    private void saveRequestHistory(Long queryId, RepoImportRequest request) {
//        if (request.getCaseId() == null) {
//            log.warn("case id is null: {}", request);
//            throw new ArgumentException("caseId cannot be null");
//        }
//        RequestHistory requestHistory = new RequestHistory();
//        requestHistory.setCaseId(String.valueOf(request.getCaseId()));
//        requestHistory.setUserId(request.getUserId());
//        requestHistory.setQueryId(queryId);
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
//    }
//
//    private Long saveQueryHistory(RepoImportRequest request) {
//        Long count = monitorRepoDao.countMonitorObjectByRepoId(request.getRepoId());
//        if(count <= 0){
//            throw new ValidException("常用库中无物品信息，无法导入");
//        }
//        String repoName = monitorRepoDao.findRepoNameById(request.getRepoId());
//
//        QueryHistory queryHistory = new QueryHistory();
//        queryHistory.setRequestType(RequestTypeEnum.REPO_IMPORT.typeName());
//        MonitorRepoParam monitorRepoParam = new MonitorRepoParam();
//        monitorRepoParam.setRepoId(request.getRepoId());
//        monitorRepoParam.setRepoName(repoName);
//        monitorRepoParam.setRepoType(request.getRepoType());
//        monitorRepoParam.setObjTypes(MonitorRepoEnum.getFieldDescs());
//        String param = JSON.toJSONString(monitorRepoParam, SerializerFeature.DisableCircularReferenceDetect);
//        queryHistory.setRequestParameter(param);
//        queryHistory.setMd5(DigestUtils.md5Hex(param));
//        queryHistory.setStatus(CalculateStatusEnum.SUCCEED.code());
//        Date curDate = new Date();
//        queryHistory.setCreateDate(curDate);
//        queryHistory.setUpdateDate(curDate);
//
//        queryHistory.setResultCount(count);
//        try {
//            queryHistoryDao.save(queryHistory);
//        } catch (Exception e) {
//            log.error("query history mysql insert error, request: {}", request);
//            throw new DBException("query history mysql insert error", e);
//        }
//        return queryHistory.getId();
//    }

    public Pair<Long, List<MonitorObjectDto>> selectByRepoId(Long repoId, PageRequest pageRequest) {
        try {
            List<MonitorObjectDto> data = monitorRepoDao.selectMonitorObjectByRepoId(repoId, pageRequest.getPageSize(), pageRequest.calOffset());
            Long count = monitorRepoDao.countMonitorObjectByRepoId(repoId);
            return Pair.of(count, data);
        } catch (Exception e) {
            throw new DBException("t_monitor_object impala search error", e);
        }

    }

    public List<RepoResponse> getRepoByUserId() {
        String token = request.getHeader("Authorization");
        String userId = JWTUtil.getUserId(token);
        if(StringUtils.isEmpty(userId)){
            throw new AuthException("auth error");
        }
        List<String> possibleUserStr = buildSearchUserStr(userId);
        return monitorRepoDao.getRepoByUserId(Long.valueOf(userId), possibleUserStr);
    }

    private List<String> buildSearchUserStr(String userId){
        List<String> list = Lists.newArrayList("[%s]","[%s,",",%s,",",%s]");
        return list.stream().map(a -> String.format(a, userId)).collect(Collectors.toList());
    }

    public RepoSearchResultDto search(RepoSearchVo searchVo, Long userId) {
        List<String> possibleUserStr = buildSearchUserStr(userId.toString());
        try {
            long start = System.currentTimeMillis();
            int page = searchVo.getPage();
            int size = searchVo.getSize();
            long offset = page * size;
            List<String> values = repoSearchManager.trimQueryString(searchVo.getValue());
            RepoSearchResultDto resultDto = new RepoSearchResultDto();
            Future<List<Map<String, Object>>> groupFuture = ThreadPoolUtil.getInstance().submit(() -> monitorRepoDao.groupByObjType(values, userId, possibleUserStr));
            Future<List<Map<String, Object>>> dataFuture = ThreadPoolUtil.getInstance().submit(() -> monitorRepoDao.search(values, userId, possibleUserStr, size, offset));
            List<Map<String, Object>> result = dataFuture.get();
//          Long count = monitorRepoDao.countSearch(searchVo.getValue());
            List<Map<String, Object>> groupMap = groupFuture.get();
            log.info("monitor search cost:{}ms", (System.currentTimeMillis()-start));
            int count = groupMap.stream().mapToInt(a -> Integer.valueOf(a.get("type_count").toString())).sum();
            resultDto.setDatas(result);
            resultDto.setTotal((long)count);
            resultDto.setGroupMap(groupMap);
            resultDto.setRepoName("常用库");
            return resultDto;
        } catch (Exception e) {
            throw new DBException("search monitorRepo error", e);
        }
    }
}
