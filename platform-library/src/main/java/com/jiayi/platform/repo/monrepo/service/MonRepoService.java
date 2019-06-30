package com.jiayi.platform.repo.monrepo.service;

import com.google.common.collect.Lists;
import com.jiayi.platform.common.exception.ArgumentException;
import com.jiayi.platform.common.exception.DBException;
import com.jiayi.platform.common.exception.ValidException;
import com.jiayi.platform.repo.monrepo.dao.MonRepoDao;
import com.jiayi.platform.repo.monrepo.dto.MonPageDto;
import com.jiayi.platform.repo.monrepo.dto.MonRepoDto;
import com.jiayi.platform.repo.monrepo.dto.MonUtilDto;
import com.jiayi.platform.repo.monrepo.entity.MonitorRepo;
import com.jiayi.platform.repo.monrepo.vo.MonRepoRequest;
import com.jiayi.platform.repo.monrepo.vo.MonRepoSearchVo;
import com.jiayi.platform.security.core.dao.DepartmentDao;
import com.jiayi.platform.security.core.dao.UserDao;
import com.jiayi.platform.security.core.entity.Department;
import com.jiayi.platform.security.core.entity.UserBean;
import com.jiayi.platform.security.core.util.JWTUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
public class MonRepoService {
    private static final Logger log = LoggerFactory.getLogger(MonRepoService.class);

    @Autowired
    private MonRepoDao monRepoDao;
    @Autowired
    private DepartmentDao departmentDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private MonRepoObjectsCountManager monRepoObjectsCountManager;
    @Autowired
    private HttpServletRequest request;

    public MonPageDto<MonRepoDto> findMonRepoList(MonRepoSearchVo monRepoSearchVo) {
        Integer size = monRepoSearchVo.getSize();
        Integer page = monRepoSearchVo.getPage();
        Integer offset = page * size;
        String token = request.getHeader("Authorization");
        String userId = JWTUtil.getUserId(token);
        List<String> possibleUserStr = buildSearchUserStr(userId);//查询私密库条件
        log.trace("perf-monrepo: begin query database");
        try {
            List<MonRepoDto> list = monRepoDao.selectMonRepoList(monRepoSearchVo, size, offset, Long.valueOf(userId), possibleUserStr);
            log.trace("perf-monrepo: end query data");
            Long count = monRepoDao.countMonRepoList(monRepoSearchVo, size, offset, Long.valueOf(userId), possibleUserStr);
            log.trace("perf-monrepo: end query count");
            if (CollectionUtils.isEmpty(list)) {
                return new MonPageDto<MonRepoDto>(Lists.newArrayList(), 0l, monRepoSearchVo.getPage(), 0);
            }
            setObjectsCount(list);//统计库列表（人员/物品）布控数量
            log.trace("perf-monrepo: end setObjectsCount");
            parseMonRepoDto(list);
            return new MonPageDto<MonRepoDto>(list, count, monRepoSearchVo.getPage(), list.size());
        } catch (Exception e) {
            throw new ArgumentException("monRepoList impala search error", e);
        }
    }

    private void parseMonRepoDto(List<MonRepoDto> list){
        List<Integer> departmentIds = list.stream().filter(a -> a.getDepartmentId() != null).map(MonRepoDto::getDepartmentId).collect(toList());
        List<Department> departments = departmentDao.findAllById(departmentIds);
        Map<Integer, Department> departMap = departments.stream().collect(Collectors.toMap(Department::getId, Function.identity(), (k1, k2) -> k2));

        List<Long> createUserIds = list.stream().filter(a -> a.getUserId() != null).map(MonRepoDto::getUserId).collect(toList());
        List<UserBean> createUser = userDao.findUserObjByIds(createUserIds);
        Map<Long, UserBean> userMap = createUser.stream().collect(Collectors.toMap(UserBean::getId, Function.identity(), (k1, k2) -> k2));
        log.trace("perf-monrepo: end find departments");
        try {
            list.forEach(monRepoDto -> {
                monRepoDto.setDepartment(departMap.get(monRepoDto.getDepartmentId()));
                UserBean userBean = userMap.get(monRepoDto.getUserId());
                if(userBean != null)
                    monRepoDto.setUserName(userBean.getNickname());
                List<Long> userIds = monRepoDto.permissionUserIds();
                if(CollectionUtils.isNotEmpty(userIds)) {
                    List<UserBean> user = userDao.findUserObjByIds(userIds);
                    monRepoDto.setPermissionUserIds(user);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.trace("perf-monrepo: end set departments");
    }

    private void setObjectsCount(List<MonRepoDto> repos) {
        Map<Long, Long> objectsCountMap = monRepoObjectsCountManager.countAllMonRepoObjects();
        Map<Long, Long> personCountMap = monRepoObjectsCountManager.countAllMonRepoPersons();
        for (MonRepoDto repo : repos) {
            Long objectCount = objectsCountMap.get(repo.getUid());
            if (objectCount == null) {
                objectCount = 0L;
            }
            Long personCount = personCountMap.get(repo.getUid());
            if (personCount == null) {
                personCount = 0L;
            }
            repo.setTotalObject(objectCount.intValue());
            repo.setTotalPerson(personCount.intValue());
            repo.setTotalAll((int) (objectCount + personCount));
        }
    }

    private List<String> buildSearchUserStr(String userId){
        List<String> list = Lists.newArrayList("[%s]","[%s,",",%s,",",%s]");
        return list.stream().map(a -> String.format(a, userId)).collect(Collectors.toList());
    }

    public MonitorRepo addMonRepo(MonRepoRequest monRepoRequest) {
        long count = monRepoDao.countMonRepoName(monRepoRequest.getRepoName());
        if (count > 0) {
            throw new ValidException("常用库名称重复");
        }
        try {
            MonitorRepo monitorRepo = new MonitorRepo();
            monitorRepo.setUid(MonUtilDto.generateUid());
            monitorRepo.setCreateAt(System.currentTimeMillis());
            monitorRepo.setUpdateAt(monitorRepo.getCreateAt());
            monitorRepo.setUserId(monRepoRequest.getUserId());
            setMonRepo(monitorRepo, monRepoRequest);
            monRepoDao.insertMonRepo(monitorRepo);
            return monitorRepo;
        } catch (Exception e) {
            throw new DBException("MonRepoService addMonRepo error!", e);
        }
    }

    public MonitorRepo modifyMonRepo(Long id, MonRepoRequest monRepoRequest) {
        MonitorRepo monitorRepo = monRepoDao.selectMonRepoById(id);
        if (monRepoRequest.getRepoName() != null && !monRepoRequest.getRepoName().equals(monitorRepo.getRepoName())) {
            long count = monRepoDao.countMonRepoName(monRepoRequest.getRepoName());
            if (count > 0) {
                throw new ValidException("常用库名称重复");
            }
        }
        try {
            setMonRepo(monitorRepo, monRepoRequest);
            monitorRepo.setUpdateAt(System.currentTimeMillis());
            monRepoDao.updateMonRepo(monitorRepo);
            return monitorRepo;
        } catch (Exception e) {
            throw new DBException("MonRepoService modifyMonRepo error!", e);
        }
    }

    private void setMonRepo(MonitorRepo monitorRepo, MonRepoRequest monRepoRequest) {
        monitorRepo.setRepoName(monRepoRequest.getRepoName());
        monitorRepo.setRepoType(monRepoRequest.getRepoType());
        monitorRepo.setDepartmentId(monRepoRequest.getDepartmentId());
        if (monRepoRequest.getRepoDesc() == null) monRepoRequest.setRepoDesc("");
        monitorRepo.setRepoDesc(monRepoRequest.getRepoDesc());
        monitorRepo.setPermissionType(monRepoRequest.getPermissionType());
        if(CollectionUtils.isNotEmpty(monRepoRequest.getPermissionUserIds())){
            monitorRepo.setPermissionUserIds(monRepoRequest.getPermissionUserIds());
            monitorRepo.setPermissionUserIdStr(String.format("[%s]", StringUtils.join(monRepoRequest.getPermissionUserIds(), ",")));
        }else{
            monitorRepo.setPermissionUserIds(null);
            monitorRepo.setPermissionUserIdStr(null);
        }
    }

    public void deleteMonRepo(Long id) {
        MonitorRepo monitorRepo = monRepoDao.selectMonRepoById(id);
        if (monitorRepo == null) {
            throw new ArgumentException("常用库不存在");
        }
        try {
            monRepoDao.deleteMonRepoById(id);
        } catch (Exception e) {
            throw new DBException("MonRepoService deleteMonRepo error!", e);
        }
    }

}
