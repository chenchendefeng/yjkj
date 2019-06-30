package com.jiayi.platform.basic.dao;

import com.jiayi.platform.basic.entity.PlaceLabel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlaceLabelDao extends CrudRepository<PlaceLabel, Long> {

    Page<PlaceLabel> findByType(Integer type, Pageable pageable);

    @Query("select max(p.code) from PlaceLabel p where p.type=:type and p.pcode=:pcode")
    String findMaxPlaceLabelCodeByTypeAndPcode(@Param("type") Integer type, @Param("pcode") String pcode);

    @Query("select count(1) from PlaceLabel p where p.name=:name")
    int isNameUsed(@Param("name") String name);

    PlaceLabel findByCodeAndType(String code, Integer type);

    List<PlaceLabel> findByPcode(String pcode);

    @Query("select p from PlaceLabel p where p.code in(:codes)")
    List<PlaceLabel> findByCodeIn(@Param("codes") List<String> codes);

    List<PlaceLabel> findByPcodeIn(List<String> pcodes);

    @Query(value = "select case p.pcode when '0' then p.code else p.pcode end as code1 " +
            "from t_place_label p left join t_place_label p0 on p0.pcode=p.code " +
            "where p0.code in(:placeLabels) group by code1 having count(code1)>1", nativeQuery = true)
    List<String> getTopLevelCount(@Param("placeLabels") List<String> placeLabels);

    @Query("select p from PlaceLabel p")
    Page<PlaceLabel> findAllResult(Pageable pageable);

    @Modifying
    @Query("update PlaceLabel p set p.remark=:remark where p.code=:code")
    void updateRemarkByCode(@Param("code") String code, @Param("remark") String remark);

}
