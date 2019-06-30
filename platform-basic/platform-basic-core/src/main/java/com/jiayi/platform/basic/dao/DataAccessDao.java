package com.jiayi.platform.basic.dao;

import com.jiayi.platform.basic.entity.DataAccess;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataAccessDao extends JpaRepository<DataAccess, Integer> {

}
