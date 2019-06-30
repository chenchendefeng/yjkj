package com.jiayi.platform.alarm.service;

import com.jiayi.platform.alarm.dao.AlarmStrategyDao;
import com.jiayi.platform.alarm.dao.DeviceInfoDao;
import com.jiayi.platform.alarm.dao.SuspectsDao;
import com.jiayi.platform.alarm.dao.SuspectsStrategyDao;
import com.jiayi.platform.alarm.dto.ConditionDto;
import com.jiayi.platform.alarm.dto.DeviceInfoRequest;
import com.jiayi.platform.alarm.dto.SuspectsRequest;
import com.jiayi.platform.alarm.entity.AlarmStrategy;
import com.jiayi.platform.alarm.entity.DeviceInfo;
import com.jiayi.platform.alarm.entity.Suspects;
import com.jiayi.platform.alarm.entity.SuspectsStrategy;
import com.jiayi.platform.alarm.enums.AlarmStatus;
import com.jiayi.platform.alarm.enums.AlarmType;
import com.jiayi.platform.common.enums.CollectType;
import com.jiayi.platform.common.exception.ServiceException;
import com.jiayi.platform.common.exception.ValidException;
import com.jiayi.platform.common.util.MacUtil;
import com.jiayi.platform.common.web.dto.PageResult;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class SuspectsService {

    @Autowired
    private SuspectsDao suspectsDao;
    @Autowired
    private SuspectsStrategyDao suspectsStrategyDao;
    @Autowired
    private DeviceInfoDao deviceInfoDao;
    @Autowired
    private AlarmStrategyDao alarmStrategyDao;
//    @Autowired
//    private CaseCommonDao caseCommonDao; TODO 案件相关

    public PageResult findSuspects(ConditionDto conditionDto) {
        Specification<Suspects> specification = (Specification<Suspects>) (root, query, cb) -> {
            List<Predicate> list = new ArrayList<>();
            if (StringUtils.isNotBlank(conditionDto.getName())) {
                updateTime(conditionDto.getCaseId(), new Date());
                Path<String> name = root.get("name");
                list.add(cb.like(name, "%" + conditionDto.getName().trim() + "%"));
            }
            Path<String> caseId = root.get("caseId");
            list.add(cb.equal(caseId, conditionDto.getCaseId()));

            return cb.and(list.toArray(new Predicate[0]));
        };
        Sort sort = Sort.by(new Sort.Order(Direction.DESC, "id"));
        Pageable pageable = PageRequest.of(conditionDto.getPageNo(), conditionDto.getPageSize(), sort);
        Page<Suspects> resultPage = suspectsDao.findAll(specification, pageable);
        List<Suspects> slist = resultPage.getContent();
        return new PageResult<>(slist, resultPage.getTotalElements(), conditionDto.getPageNo(), slist.size());
    }

    public Suspects addSuspect(SuspectsRequest request) {
//        updateTime(suspects.getCaseId(), new Date());
        int count = suspectsDao.isNameExistByCase(request.getName(), request.getCaseId());
        if (count > 0) {
            throw new ValidException("同一案件中姓名不能重复");
        }
        List<DeviceInfoRequest> deviceList = request.getDeviceInfoData();
        duplicateCheck(deviceList);//物品重复检验
        List<AlarmStrategy> allActiveStrategy = alarmStrategyDao.findByCaseIdAndActive(request.getCaseId(), AlarmStatus.OPEN.code());// 查询所有已启用策略并绑定
        Map<Integer, List<AlarmStrategy>> strategyMap = allActiveStrategy.stream().collect(Collectors.groupingBy(AlarmStrategy::getType));
        strategyMap.forEach((k, v) -> {
            if (v.size() > 1) {//重复类型校验
                throw new ValidException(AlarmType.getTaskStatusByValue(v.get(0).getType()).getDescription() + "策略类型重复,请禁用相同类型的策略");
            }
        });
        try {
            Suspects suspects = new Suspects();
            this.setSuspects(suspects, request);
            suspects.setBeActive(0);
            suspects.setCreateAt(new Date());
            suspects.setStatus(CollectionUtils.isEmpty(allActiveStrategy) ? AlarmStatus.CLOSE.code() : AlarmStatus.OPEN.code());
            suspectsDao.save(suspects);//1.级联添加人员物品
            List<AlarmStrategy> touchStrategyList = allActiveStrategy.stream().filter(a -> a.getType().equals(AlarmType.TOUCH.getType())).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(touchStrategyList)) {
                // 触碰策略禁用或不存在,禁用则不做处理,不存在则直接添加,相应的人员同步绑定该策略
                AlarmStrategy touchStrategy = addTouchStrategyAndBindingSuspect(suspects.getCaseId(), suspects.getUserId());
//                if (touchStrategy != null) {//这里会把自己绑定，下面重复绑定
//                    allActiveStrategy.add(touchStrategy);
//                }
            }
            List<SuspectsStrategy> suspectsStrategyList = new ArrayList<>();
            for (AlarmStrategy alarmStrategy : allActiveStrategy) {
                suspectsStrategyList.add(setSuspectStrategy(alarmStrategy.getId(), suspects.getId()));
            }
            suspectsStrategyDao.saveAll(suspectsStrategyList);// 2.人员绑定所有启用的策略UNIQUE KEY `weiyi` (`suspect_id`,`alarm_strategy_id`),
            return suspects;
        } catch (Exception e) {
            throw new ServiceException("添加人员失败", e);
        }
    }

    private void setSuspects(Suspects suspects, SuspectsRequest request){
        BeanCopier copier = BeanCopier.create(SuspectsRequest.class, Suspects.class, false);
        copier.copy(request, suspects, null);
        suspects.setUpdateAt(new Date());
        List<DeviceInfoRequest> deviceList = request.getDeviceInfoData();
        if (CollectionUtils.isNotEmpty(deviceList)) {
            List<DeviceInfo> deviceInfoList = new ArrayList<>();
            for (DeviceInfoRequest deviceInfoRequest : deviceList) {
                DeviceInfo deviceInfo = new DeviceInfo();
                deviceInfo.setSuspects(suspects);
                deviceInfo.setType(deviceInfoRequest.getType());
                deviceInfo.setCode(deviceInfoRequest.getCode());
                Date date = new Date();
                deviceInfo.setCreateAt(date);
                deviceInfo.setUpdateAt(date);
                deviceInfoList.add(deviceInfo);
            }
            suspects.setDeviceInfoData(deviceInfoList);
        }
    }

    private AlarmStrategy addTouchStrategyAndBindingSuspect(Integer caseId, Long userId) {
        List<AlarmStrategy> strategyList = alarmStrategyDao.findByCaseIdAndType(caseId, AlarmType.TOUCH.getType());
        if (CollectionUtils.isEmpty(strategyList)) {//可能触碰策略禁用，所有不需要添加策略，也不需要跟人员绑定
            AlarmStrategy alarmStrategy = new AlarmStrategy();
            alarmStrategy.setType(AlarmType.TOUCH.getType());
            alarmStrategy.setCaseId(caseId);
            Date date = new Date();
            alarmStrategy.setCreateAt(date);
            alarmStrategy.setUpdateAt(date);
            alarmStrategy.setUserId(userId);
            alarmStrategy.setName("触碰策略");
            alarmStrategy.setBeActive(0);
            alarmStrategy.setStatus(AlarmStatus.OPEN.code());
            alarmStrategyDao.save(alarmStrategy);
            List<Suspects> suspectsList = suspectsDao.findResultByCaseId(caseId);//查询案件中所有人员,绑定该新增策略
            if (CollectionUtils.isNotEmpty(suspectsList)) {
                List<SuspectsStrategy> suspectsStrategyList = new ArrayList<>();
                suspectsList.forEach(a -> {
                    a.setStatus(AlarmStatus.OPEN.code());
                    suspectsStrategyList.add(setSuspectStrategy(alarmStrategy.getId(), a.getId()));
                });//人员未绑定策略即为未布控，因为人员自动绑定新增触碰策略，所以改为布控状态
                suspectsDao.saveAll(suspectsList);
                suspectsStrategyDao.saveAll(suspectsStrategyList);// 绑定
            }
            return alarmStrategy;
        }
        return null;
    }

    private void duplicateCheck(List<DeviceInfoRequest> deviceList) {// 查重
        for (DeviceInfoRequest a : deviceList) {
            if (a.getType() == 1)
                a.setCode(MacUtil.toTrimMac(a.getCode()));
        }
        Map<String, List<DeviceInfoRequest>> deviceMap = deviceList.stream().collect(Collectors.groupingBy(a -> a.getType() + "|" + a.getCode()));
        deviceMap.forEach((k, v) -> {
            if (v.size() > 1) {
                throw new ValidException(CollectType.getByCode(v.get(0).getType()) + ":" + v.get(0).getCode() + ",已存在");
            }
        });
    }

    public boolean deleteSuspect(long id) {
        Suspects suspect = findSuspectById(id);
        try {
            suspectsStrategyDao.deleteBySuspectId(id);
            suspectsDao.delete(suspect);
            updateTime(suspect.getCaseId(), new Date());
        } catch (Exception e) {
            throw new ServiceException("删除人员失败", e);
        }
        return true;
    }

    public Suspects findSuspectById(long id) {
        return suspectsDao.findById(id).orElseThrow(() -> new ValidException("人员不存在"));
    }

    public Suspects updateSuspect(long id, SuspectsRequest request) {
        Suspects suspects = findSuspectById(id);
        if (!suspects.getName().equals(request.getName())) {
            int count = suspectsDao.isNameExistByCase(request.getName(), request.getCaseId());
            if (count > 0) {
                throw new ValidException("同一案件中姓名不能重复");
            }
        }
        List<DeviceInfoRequest> deviceInfos = request.getDeviceInfoData(); // 页面传参
        duplicateCheck(deviceInfos);// 查重

        List<DeviceInfo> beforeDeviceInfos = suspects.getDeviceInfoData(); // load
        deviceInfoDao.deleteAll(beforeDeviceInfos);
//        deviceInfoDao.saveAll(deviceInfos);

        setSuspects(suspects, request);
//        suspects.setDeviceInfoData(deviceInfos);
        try {
            suspectsDao.save(suspects);
        } catch (Exception e) {
            throw new ServiceException("修改人员失败", e);
        }
        return suspects;
    }

    private SuspectsStrategy setSuspectStrategy(long strategyId, long suspectsId) {
        SuspectsStrategy suspectsStrategy = new SuspectsStrategy();
        suspectsStrategy.setAlarmStrategyId(strategyId);
        suspectsStrategy.setSuspectId(suspectsId);
        Date date = new Date();
        suspectsStrategy.setCreateAt(date);
        suspectsStrategy.setUpdateAt(date);
        return suspectsStrategy;
    }

//    private void setSuspect(Suspects before, Suspects suspect) {
//        if (suspect.getAddress() != null) {
//            before.setAddress(suspect.getAddress());
//        }
//        if (suspect.getBeActive() != null) {
//            before.setBeActive(suspect.getBeActive());
//        }
//        if (suspect.getCaseId() != null) {
//            before.setCaseId(suspect.getCaseId());
//        }
//        if (suspect.getName() != null) {
//            before.setName(suspect.getName());
//        }
//        if (suspect.getMobile() != null) {
//            before.setMobile(suspect.getMobile());
//        }
//        if (suspect.getIdentitycard() != null) {
//            before.setIdentitycard(suspect.getIdentitycard());
//        }
//        if (suspect.getRemark() != null) {
//            before.setRemark(suspect.getRemark());
//        }
//        if (suspect.getUserId() != 0) {
//            before.setUserId(suspect.getUserId());
//        }
//        Date date = new Date();
//        before.setUpdateAt(date);
//        updateTime(suspect.getCaseId(), date);
//    }

    //修改案件最后操作时间
    public void updateTime(Integer id, Date date) {
        //Integer iid = Integer.valueOf(id);
//        caseCommonDao.update(id,date);
    }
}
