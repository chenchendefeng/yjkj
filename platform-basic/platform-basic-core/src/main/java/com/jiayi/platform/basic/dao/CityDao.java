package com.jiayi.platform.basic.dao;


import com.jiayi.platform.basic.entity.City;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CityDao extends CrudRepository<City, Long>, JpaSpecificationExecutor<City> {

    List<City> findByLevelIn(List<String> list);

    List<City> findByIdIn(List<Long> ids);
}
