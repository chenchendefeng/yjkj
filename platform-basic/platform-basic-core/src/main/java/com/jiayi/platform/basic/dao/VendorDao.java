package com.jiayi.platform.basic.dao;

import com.jiayi.platform.basic.entity.Vendor;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VendorDao extends PagingAndSortingRepository<Vendor, Integer>, JpaSpecificationExecutor<Vendor> {
    @Query("select count(1) from Vendor v where v.name =:name")
    int isNameUsed(@Param("name") String name);

    @Query("select count(1) from Vendor v where v.code =:code")
    int isCodeUsed(@Param("code") String code);

    @Query(value = "select * from \n" +
            "(select count(1) as sCount from t_src s where s.vendor_id=:vendorId)a\n" +
            "JOIN\n" +
            "(select count(1) mCount from t_device_model m where m.vendor_id=:vendorId)b\n" +
            "JOIN\n" +
            "(select count(1) dCount from t_device d where d.vendor_id=:vendorId)c", nativeQuery = true)
    List<Object[]> isVendorUsed(@Param("vendorId") Integer vendorId);

    @Query("select v from Vendor v order by v.createDate desc, v.updateDate desc")
    List<Vendor> findAll();
}
