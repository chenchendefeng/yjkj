package com.jiayi.platform.alarm.service;

import com.jiayi.platform.alarm.dao.SignalGoodsDao;
import com.jiayi.platform.alarm.dto.ConditionDto;
import com.jiayi.platform.alarm.dto.SignalGoodsRequest;
import com.jiayi.platform.alarm.entity.SignalGoods;
import com.jiayi.platform.common.enums.CollectType;
import com.jiayi.platform.common.exception.ArgumentException;
import com.jiayi.platform.common.exception.ServiceException;
import com.jiayi.platform.common.exception.ValidException;
import com.jiayi.platform.common.util.MacUtil;
import com.jiayi.platform.common.web.dto.PageResult;
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

@Service
@Transactional
public class SignalGoodsService {

    @Autowired
    private SignalGoodsDao signalGoodsDao;
//    @Autowired
//    private CaseCommonDao caseCommonDao; TODO 案件相关，操作记录

    public PageResult findSignalGoods(ConditionDto conditionDto) {
        Specification<SignalGoods> specification = (root, query, cb) -> {
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
        Page<SignalGoods> resultPage = signalGoodsDao.findAll(specification, pageable);
        List<SignalGoods> slist = resultPage.getContent();
        return new PageResult<>(slist, resultPage.getTotalElements(), conditionDto.getPageNo(), slist.size());
    }

    public SignalGoods addGoods(SignalGoodsRequest request) {
        int count = signalGoodsDao.isNameExist(request.getName(), request.getCaseId());
        if (count > 0) {
            throw new ValidException("物品名称重复");
        }
        int count1 = signalGoodsDao.isGoodsExistInCase(request.getObjType(), request.getObjValue(), request.getCaseId());
        if (count1 > 0) {
            throw new ValidException("该案件中已存在此类型设备");
        }
        SignalGoods signalGoods = new SignalGoods();
        this.setSignalGoods(signalGoods, request);
        signalGoods.setBeActive(0);
        signalGoods.setCreateAt(new Date());
        try {
            signalGoodsDao.save(signalGoods);
//            updateTime(signalGoods.getCaseId(), date);
        } catch (Exception e) {
            throw new ArgumentException("添加物品失败", e);
        }
        return signalGoods;
    }

    public SignalGoods findById(long id) {
        return signalGoodsDao.findById(id).orElseThrow(() -> new ValidException("物品不存在"));
    }

    public boolean deleteGoods(long id) {
//        SignalGoods signalGoods = findById(id);
        try {
            signalGoodsDao.deleteById(id);
//            deleteRedis(signalGoods);
//            updateTime(signalGoods.getCaseId(), new Date());
        } catch (Exception e) {
            throw new ServiceException("删除物品失败", e);
        }
        return true;
    }

    public SignalGoods updateGoods(long id, SignalGoodsRequest request) {
        SignalGoods signalGoods = findById(id);
        Integer caseId = signalGoods.getCaseId();
        if (!signalGoods.getName().equals(request.getName())) {
            int count = signalGoodsDao.isNameExist(request.getName(), caseId);
            if (count > 0) {
                throw new ValidException("物品名称重复");
            }
        }
        if (!signalGoods.getObjType().equals(request.getObjType()) || !signalGoods.getObjValue().equals(request.getObjValue())) {
            int count1 = signalGoodsDao.isGoodsExistInCase(request.getObjType(), request.getObjValue(), caseId);
            if (count1 > 0) {
                throw new ValidException("该案件中已存在此类型设备");
            }
        }
        try {
            setSignalGoods(signalGoods, request);
            signalGoodsDao.save(signalGoods);
            updateTime(signalGoods.getCaseId(), new Date());
        } catch (Exception e) {
            throw new ServiceException("修改物品失败", e);
        }
        return signalGoods;
    }

    public SignalGoods updateStatus(long id, int status) {
        SignalGoods goods = findById(id);
        if(goods.getStatus() == status){
            return goods;
        }
        if(status != 0 && status != 1){
            throw new ValidException("布控状态错误");
        }
        goods.setStatus(status);
        try {
            goods = signalGoodsDao.save(goods);
        } catch (Exception e) {
            String message = null;
            if (status == 0) {
                message = "禁用物品失败";
            } else if (status == 1) {
                message = "启用物品失败";
            }
            throw new ServiceException(message, e);
        }
        return goods;
    }

    private void setSignalGoods(SignalGoods signalGoods, SignalGoodsRequest request) {
        BeanCopier copier = BeanCopier.create(SignalGoodsRequest.class, SignalGoods.class, false);
        copier.copy(request, signalGoods, null);
        signalGoods.setObjValue(this.transformMac(request.getObjType(), request.getObjValue()));
        signalGoods.setUpdateAt(new Date());
//        if (signalGoods.getName() != null) {
//            before.setName(signalGoods.getName());
//        }
//        if (signalGoods.getCaseId() != null) {
//            before.setCaseId(signalGoods.getCaseId());
//        }
//        if (signalGoods.getObjType() != null) {
//            before.setObjType(signalGoods.getObjType());
//        }
//        if (signalGoods.getObjValue() != null) {
//            before.setObjValue(this.transformMac(signalGoods.getObjType(), signalGoods.getObjValue()));
//        }
//        if (signalGoods.getUserName() != null) {
//            before.setUserName(signalGoods.getUserName());
//        }
//        if (signalGoods.getMobile() != null) {
//            before.setMobile(signalGoods.getMobile());
//        }
//        if (signalGoods.getIdentitycard() != null) {
//            before.setIdentitycard(signalGoods.getIdentitycard());
//        }
//        if (signalGoods.getAddress() != null) {
//            before.setAddress(signalGoods.getAddress());
//        }
//        if (signalGoods.getRemark() != null) {
//            before.setRemark(signalGoods.getRemark());
//        }
//        if (signalGoods.getBeActive() != null) {
//            before.setBeActive(signalGoods.getBeActive());
//        }
//        before.setUpdateAt(new Date());
    }

    private String transformMac(Integer ObjeType, String objValue) {
        if (ObjeType.equals(CollectType.MAC.code())) {
            return MacUtil.generateMac(objValue);
        }
        return objValue;
    }

    //修改案件最后操作时间
    private void updateTime(Integer id, Date date) {
        //Integer iid = Integer.valueOf(id);
//        caseCommonDao.update(id,date);
    }

}
