package com.jiayi.platform.basic.dao;


import com.jiayi.platform.basic.entity.PlateNumber;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlateNumberDao extends PagingAndSortingRepository<PlateNumber, String> {

}
