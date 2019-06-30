package com.jiayi.platform.basic.service;

import com.jiayi.platform.basic.entity.AuditTypeCode;

import java.util.List;

public interface AuditTypeCodeService {

    List<AuditTypeCode> findByCollectType(String collectType);

}
