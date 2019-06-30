package com.jiayi.platform.basic.dao;

import com.jiayi.platform.basic.entity.MacCompany;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MacCompanyDao extends PagingAndSortingRepository<MacCompany, Long> {

}
