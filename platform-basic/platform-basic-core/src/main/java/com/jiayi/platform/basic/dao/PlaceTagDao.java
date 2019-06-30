package com.jiayi.platform.basic.dao;

import com.jiayi.platform.basic.entity.PlaceTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface PlaceTagDao extends CrudRepository<PlaceTag, Long> {

   @Query("select p from PlaceTag p")
   Page<PlaceTag> findAllResult(Pageable pageable);

   PlaceTag findByCode(String code);

}
