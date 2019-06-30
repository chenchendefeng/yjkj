package com.jiayi.platform.basic.serviceImpl;

import com.jiayi.platform.basic.dao.AuditTypeCodeDao;
import com.jiayi.platform.basic.dto.AuditTypeCodeDto;
import com.jiayi.platform.basic.entity.AuditTypeCode;
import com.jiayi.platform.basic.service.AuditTypeCodeService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AuditTypeCodeServiceImpl implements AuditTypeCodeService {

    @Autowired
    private AuditTypeCodeDao auditTypeCodeDao;

    @Override
    public List<AuditTypeCode> findByCollectType(String collectType) {
        return auditTypeCodeDao.findByType(collectType);
    }

    public List<AuditTypeCodeDto> tree(String type) {
        List<AuditTypeCode> types = auditTypeCodeDao.findByType(type);
        if(CollectionUtils.isEmpty(types)){
            return Collections.EMPTY_LIST;
        }
        Map<Long, List<AuditTypeCode>> groupByParent = types.stream().collect(Collectors.groupingBy(AuditTypeCode::getParentCode));
        List<AuditTypeCodeDto> resultList = new ArrayList<>();
        groupByParent.get(0L).forEach(a->{
            List<AuditTypeCode> list = groupByParent.get(a.getCode());
            if(list != null){
                List<AuditTypeCodeDto> nextLevel = list.stream().map(b -> new AuditTypeCodeDto(b.getCode(), b.getChineseSimplified(), null)).collect(Collectors.toList());
                resultList.add(new AuditTypeCodeDto(a.getCode(), a.getChineseSimplified(), nextLevel));
            }
        });
        return resultList;
    }
}
