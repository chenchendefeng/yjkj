package com.jiayi.platform.basic.dao;

import com.jiayi.platform.basic.dto.DepartPlaceDto;
import com.jiayi.platform.basic.entity.Place;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface PlaceDao extends CrudRepository<Place, Long> , JpaSpecificationExecutor<Place>, JpaRepository<Place, Long> {

	@Query("select new com.jiayi.platform.basic.dto.DepartPlaceDto(p.id,p.name,p.address,p.code,p.department.id,p.longitude,p.latitude) from Place p where p.department.id=:deptId or p.department.id is null")
	Page<DepartPlaceDto> findByDepartment(@Param("deptId") Integer deptId, Pageable pageable);

	@Query("select p from Place p where p.id=:id")
	Optional<Place> findById(@Param("id") Long id);

	@Modifying
	@Query("update Place p set p.code=:placeCode where p.id=:id")
	int update(@Param("placeCode") String placeCode, @Param("id") Long id);

	@Query("select count(1) from Device d where d.placeId=:id")
	int isUsedInDevice(@Param("id") Long id);

	List<Place> findByIdIn(List<Long> ids);

//	@Query(value = "select * from t_place p where p.code = :code limit 1", nativeQuery = true)
//	Place findBySrcAndCode(@Param("src") String src, @Param("code") String code);

	@Query(value = "select count(1) from t_place p where p.code = :code", nativeQuery = true)
	int findCode(@Param("code") String code);

	@Query(value = "select p from Place p where p.code = :code")
	Place findByCode(@Param("code") String code);

	@Query("select new com.jiayi.platform.basic.entity.Place(p.id, p.code) from Place p")
	List<Place> findAllPlaceIdCode();

	@Query("select new com.jiayi.platform.basic.entity.Place(p.id, p.code, p.name, p.address) from Place p")
	List<Place> findIdCodeNameAddressAll();

//	@Query("select p.id from Place p where p.address like concat('%',:address,'%')")
//	List<Long> findPlaceIdByAddress(@Param("address") String address);

	@Query("select p from Place p where p.code in(:placeCode)")
    List<Place> findByPlaceCodes(@Param("placeCode") Set<String> placeCode);

	@Query("select count(p) from Place p where p.department.id=:deptId")
    int isHavePlace(@Param("deptId") Integer deptId);

	@Query("select p.id as id,p.address as value,p.code as code from Place p where p.address like concat('%',:address,'%')")
	List<Map<String, String>> findByFuzzyAddress(@Param("address") String address);

	@Query("select p.id as id,p.name as value,p.code as code from Place p where p.name like concat('%',:name,'%')")
	List<Map<String, String>> findByFuzzyName(@Param("name") String name);

    int countByCity(@Param("cityId") String cityId);
}
