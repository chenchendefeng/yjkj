package com.jiayi.platform.basic.dao;

import com.jiayi.platform.basic.entity.PhoneHome;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhoneHomeDao extends PagingAndSortingRepository<PhoneHome, Long> {

}
