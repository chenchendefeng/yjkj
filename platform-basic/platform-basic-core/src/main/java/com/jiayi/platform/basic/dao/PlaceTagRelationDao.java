package com.jiayi.platform.basic.dao;

import com.jiayi.platform.basic.entity.PlaceTagRelation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceTagRelationDao extends JpaRepository<PlaceTagRelation, Long> {

	void deleteByPlaceId(Long placeId);

}
