package com.jiayi.platform.basic.dao;

import com.jiayi.platform.basic.entity.DeviceExtension;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceExtensionDao extends PagingAndSortingRepository<DeviceExtension, Integer>,
        JpaSpecificationExecutor<DeviceExtension> {

    @Query(value = "select * from t_device_extension d where d.src = :src and d.code = :code", nativeQuery = true)
    DeviceExtension findBySrcAndCode(@Param("src") String src, @Param("code") String code);

    @Query(value = "select count(1) from t_device_extension d where d.model = :model", nativeQuery = true)
    int findByModel(@Param("model") Integer model);

    void deleteBySrcAndCode(@Param("src") String src, @Param("code") String code);
}
