package com.jiayi.platform.judge.dao.mysql;

import com.jiayi.platform.judge.entity.mysql.RequestHistoryInfo;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface RequestHistoryInfoDao extends CrudRepository<RequestHistoryInfo, Long>, JpaSpecificationExecutor<RequestHistoryInfo> {

}
