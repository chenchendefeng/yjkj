package com.jiayi.platform.basic.dao;

import com.jiayi.platform.basic.entity.PlaceLabelRelation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceLabelRelationDao extends JpaRepository<PlaceLabelRelation, Long> {
    void deleteByPlaceId(Long id);
}
