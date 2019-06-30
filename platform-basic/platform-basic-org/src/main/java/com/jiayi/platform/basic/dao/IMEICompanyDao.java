package com.jiayi.platform.basic.dao;

import com.jiayi.platform.basic.entity.IMEICompany;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IMEICompanyDao extends PagingAndSortingRepository<IMEICompany, String> {
}
