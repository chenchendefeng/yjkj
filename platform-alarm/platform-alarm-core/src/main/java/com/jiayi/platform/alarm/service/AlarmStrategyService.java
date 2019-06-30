package com.jiayi.platform.alarm.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.jiayi.platform.alarm.dao.AlarmStrategyDao;
import com.jiayi.platform.alarm.dao.SuspectsDao;
import com.jiayi.platform.alarm.dao.SuspectsStrategyDao;
import com.jiayi.platform.alarm.dto.AlarmStrategyRequest;
import com.jiayi.platform.alarm.dto.ConditionDto;
import com.jiayi.platform.alarm.entity.AlarmStrategy;
import com.jiayi.platform.alarm.entity.Suspects;
import com.jiayi.platform.alarm.entity.SuspectsStrategy;
import com.jiayi.platform.alarm.enums.AlarmStatus;
import com.jiayi.platform.alarm.enums.AlarmType;
import com.jiayi.platform.alarm.enums.BeActive;
import com.jiayi.platform.alarm.util.DataUtil;
import com.jiayi.platform.common.exception.ServiceException;
import com.jiayi.platform.common.exception.ValidException;
import com.jiayi.platform.common.web.dto.PageResult;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
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

@Service
@Transactional
public class AlarmStrategyService {

    @Autowired
    private AlarmStrategyDao alarmStrategyDao;
    @Autowired
    private SuspectsDao suspectsDao;
    @Autowired
    private SuspectsStrategyDao suspectsStrategyDao;
//    @Autowired
//    private DeviceInfoDao deviceInfoDao;
//    @Autowired
//    private CaseCommonDao caseCommonDao;//TODO 案件相关


    public PageResult findAlarmstrategy(ConditionDto conditionDto) {
        Specification<AlarmStrategy> specification = (root, query, cb) -> {
            List<Predicate> list = new ArrayList<>();
            list.add(cb.equal(root.get("beActive"), BeActive.NORMAL.code()));
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
        Page<AlarmStrategy> resultPage = alarmStrategyDao.findAll(specification, pageable);
        List<AlarmStrategy> slist = resultPage.getContent();
        return new PageResult<>(slist, resultPage.getTotalElements(), conditionDto.getPageNo(), slist.size());
    }

    public AlarmStrategy findById(Long id) {
        return alarmStrategyDao.findStatusOpenById(id);
    }

    public AlarmStrategy add(AlarmStrategyRequest request) {
        //查询案件中是否已存在触碰策略，存在则不添加
        if(request.getType().equals(AlarmType.TOUCH.getType())){
            List<AlarmStrategy> alarmStrategies = alarmStrategyDao.findByCaseIdAndType(request.getCaseId(), AlarmType.TOUCH.getType());
            if(CollectionUtils.isNotEmpty(alarmStrategies)){
                throw new ValidException("触碰策略已存在");
            }
        }
        DataUtil.checkData(request.getType(), request.getMapRegion(), request.getExInfo());//数据校验
        if (request.getStatus() == null) {
            request.setStatus(AlarmStatus.CLOSE.code());//默认禁用
        }else if (request.getStatus().equals(AlarmStatus.OPEN.code())) {//0正常、1停止
            int count = alarmStrategyDao.countStatusOpen(request.getCaseId(), request.getType());
            if (count > 0) {
                throw new ValidException("添加失败：已有相同类型的策略启用");
            }
        }
        try {
            AlarmStrategy alarmStrategy = new AlarmStrategy();
            this.setAlarmStrategy(alarmStrategy, request);
            alarmStrategy.setBeActive(BeActive.NORMAL.code());
            alarmStrategy.setCreateAt(new Date());
//            updateTime(request.getCaseId(), date);
            alarmStrategyDao.save(alarmStrategy);
            if (request.getStatus().equals(AlarmStatus.OPEN.code())) {
                this.startStarrategy(alarmStrategy);
            }
            return alarmStrategy;
        } catch (Exception e) {
            throw new ServiceException("添加失败", e);
        }
    }

    public boolean delete(long id) {
        int count = suspectsStrategyDao.count(id);
        if (count > 0) {
            throw new ValidException("删除失败：该策略已被绑定");
        }
        AlarmStrategy alarmStrategy = findById(id);
        if(alarmStrategy.getType().equals(AlarmType.TOUCH.getType())){
            throw new ValidException("触碰策略不能删除");
        }
        try {
            alarmStrategy.setBeActive(BeActive.DELETE.code());
            alarmStrategy.setUpdateAt(new Date());
            alarmStrategyDao.save(alarmStrategy);
//            updateTime(alarmStrategy.getCaseId(), new Date());
            return true;
        } catch (Exception e) {
            throw new ServiceException("删除失败", e);
        }
    }

    public AlarmStrategy update(long id, AlarmStrategyRequest request) {
        DataUtil.checkData(request.getType(), request.getMapRegion(), request.getExInfo());
        AlarmStrategy alarmStrategy = findById(id);
        if (!alarmStrategy.getType().equals(request.getType())) {
            int count = suspectsStrategyDao.count(id);
            if (count > 0) {// 策略已被绑定
                throw new ValidException("修改失败:策略已被绑定,无法修改策略类型");
            }
        }
        if (request.getStatus().equals(AlarmStatus.OPEN.code())) {//启用前检查是否有其他相同类型策略启用
            int activeCount = alarmStrategyDao.countOtherActive(request.getCaseId(), request.getType(), id);
            if (activeCount > 0) {
                throw new ValidException("修改失败:已有相同类型的策略启用");
            }
        }
        try {
            if (!request.getStatus().equals(alarmStrategy.getStatus())) {
                if (request.getStatus().equals(AlarmStatus.OPEN.code())) {
                    this.startStarrategy(alarmStrategy);
                } else {
                    this.stopStrategy(alarmStrategy);
                }
            }
            setAlarmStrategy(alarmStrategy, request);
            alarmStrategyDao.save(alarmStrategy);
        } catch (Exception e) {
            throw new ServiceException("修改失败");
        }
        return alarmStrategy;
    }

    public void changeStatus(long strategyId, int status) {
        AlarmStrategy before = findById(strategyId);
        if (before.getStatus() == status) {
            return ;
        }
        if(status != AlarmStatus.OPEN.code() && status != AlarmStatus.CLOSE.code()){
            throw new ValidException("状态值错误");
        }
        if (status == AlarmStatus.CLOSE.code()) {// 禁用
            this.stopStrategy(before);
        } else {
            int num = alarmStrategyDao.countStatusOpen(before.getCaseId(), before.getType());
            if (num > 0) {
                throw new ValidException("已有相同类型的策略启用");// 只能启用一个同种类型的策略
            }
            this.startStarrategy(before);
        }
        try {
            before.setStatus(status);
//            return alarmStrategyDao.save(before);
        } catch (Exception e) {
            String message;
            if (status == 1) {//这里跟物品中的status相反
                message = "禁用失败";
            } else {
                message = "启用失败";
            }
            throw new ServiceException(message, e);
        }
    }

    private void setAlarmStrategy(AlarmStrategy alarmStrategy, AlarmStrategyRequest request){
        BeanCopier copier = BeanCopier.create(AlarmStrategyRequest.class, AlarmStrategy.class, false);
        copier.copy(request, alarmStrategy, null);
        alarmStrategy.setUpdateAt(new Date());
        if(request.getMapRegion() != null){
            alarmStrategy.setMapRegion(JSON.toJSONString(request.getMapRegion(), SerializerFeature.DisableCircularReferenceDetect));
        }
        if(request.getExInfo() != null){
            alarmStrategy.setExInfo(JSON.toJSONString(request.getExInfo(), SerializerFeature.DisableCircularReferenceDetect));
        }
//        updateTime(alarmStrategy.getCaseId(), date);
    }

//    private void setAlarmStrategy(AlarmStrategy before, AlarmStrategy alarmStrategy) {
//        before.setCaseId(alarmStrategy.getCaseId());
//        before.setName(alarmStrategy.getName());
//        before.setType(alarmStrategy.getType());
//        before.setMapRegion(alarmStrategy.getMapRegion());
//        before.setExInfo(alarmStrategy.getExInfo());
//        before.setRemark(alarmStrategy.getRemark());
//        before.setBeActive(alarmStrategy.getBeActive());
//        if (alarmStrategy.getUserId() != null) {
//            before.setUserId(alarmStrategy.getUserId());
//        }
//        Date date = new Date();
//        before.setUpdateAt(date);
//        updateTime(alarmStrategy.getCaseId(), date);
//    }



    //修改案件最后操作时间
    private void updateTime(Integer id, Date date) {
        //Integer iid = Integer.valueOf(id);
//        caseCommonDao.update(id,date);
    }

    private void stopStrategy(AlarmStrategy alarmStrategy) {
        List<SuspectsStrategy> list = suspectsStrategyDao.findBindingSuspects(alarmStrategy.getId());
        for (SuspectsStrategy suspectsStrategy : list) {
            // 查询该人员除本策略外，在此案件中已绑定的所有启用中的策略
            int otherCount = suspectsStrategyDao.countOthers(suspectsStrategy.getSuspectId(), alarmStrategy.getId(), alarmStrategy.getCaseId());
            if (otherCount == 0) {//说明人员未布控，删除人员物品布控
                suspectsDao.update(AlarmStatus.CLOSE.code(), suspectsStrategy.getSuspectId());// 设置状态为未布控
            }
        }
        suspectsStrategyDao.deleteAll(list);
    }

    private void startStarrategy(AlarmStrategy alarmStrategy) {
        List<Suspects> suspectIds = suspectsDao.findAllResult(alarmStrategy.getCaseId());
        List<SuspectsStrategy> suspectsStrategyList = new ArrayList<>();
        for (Suspects susp : suspectIds) {
            if (susp.getStatus() == AlarmStatus.OPEN.code()) { // 第一个启用的策略
                suspectsDao.update(AlarmStatus.OPEN.code(), susp.getId());
            }
            SuspectsStrategy ss = new SuspectsStrategy();
            ss.setSuspectId(susp.getId());
            ss.setAlarmStrategyId(alarmStrategy.getId());
            Date date = new Date();
            ss.setCreateAt(date);
            ss.setUpdateAt(date);
            suspectsStrategyList.add(ss);
        }
        suspectsStrategyDao.saveAll(suspectsStrategyList);
    }
}
