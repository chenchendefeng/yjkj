package com.jiayi.platform.basic.service;

import com.jiayi.platform.basic.entity.Place;

import java.util.List;

public interface PlaceService {

    List<Place> findAll();

    List<Place> findIdCodeNameAddressAll();

//    List<Long> findPlaceIdByAddress(String address);

    String getCityShortName();

    boolean isHavePlace(Integer deptId);
}
