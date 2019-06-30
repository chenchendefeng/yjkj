package com.jiayi.platform.basic.serviceImpl;

import com.jiayi.platform.basic.dao.CertificationTypeDao;
import com.jiayi.platform.basic.entity.CertificationType;
import com.jiayi.platform.basic.service.CertificationTypeService;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Service
public class CertificationTypeServiceImpl implements CertificationTypeService {

    @Autowired
    private CertificationTypeDao certificationTypeDao;

    @Override
    public List<CertificationType> findAll() {
        return IterableUtils.toList(certificationTypeDao.findAll());
    }
}
