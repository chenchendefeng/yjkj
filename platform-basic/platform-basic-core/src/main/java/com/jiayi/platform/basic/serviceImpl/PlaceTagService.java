package com.jiayi.platform.basic.serviceImpl;

import com.jiayi.platform.basic.dao.PlaceTagDao;
import com.jiayi.platform.basic.entity.PlaceTag;
import com.jiayi.platform.basic.request.PlaceTagRequest;
import com.jiayi.platform.common.enums.ErrorEnum;
import com.jiayi.platform.common.exception.ArgumentException;
import com.jiayi.platform.common.exception.DBException;
import com.jiayi.platform.common.web.dto.PageResult;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class PlaceTagService {

    @Autowired
    private PlaceTagDao placeTagDao;

    public PageResult<PlaceTag> findAlltag(Integer page, Integer size) {
        if (page == null)
            page = 0;
        if (size == null)
            size = 10;
        try {
            Sort sort = new Sort(Sort.Direction.ASC, "code");
            Pageable pageable = new PageRequest(page, size, sort);
            Page<PlaceTag> pageResult = placeTagDao.findAllResult(pageable);
            return new PageResult<PlaceTag>(pageResult.getContent(), pageResult.getTotalElements(), page,
                    pageResult.getContent().size());
        } catch (Exception e) {
            throw new DBException(ErrorEnum.DB_ERROR.message(), e);
        }
    }

    public PlaceTag addTag(PlaceTagRequest placeTagRequest) {
        try {
            PlaceTag placeTag = new PlaceTag();
            placeTag.setName(placeTagRequest.getName());
            placeTag.setDescription(placeTagRequest.getDescription());
            placeTag.setCreateDate(new Date());
            placeTag.setUpdateDate(new Date());
            return placeTagDao.save(placeTag);
        } catch (Exception e) {
            throw new ArgumentException(ErrorEnum.ARGUMENT_ERROR.message(), e);
        }
    }

    public void deleteTag(Long id) {
        try {
            placeTagDao.deleteById(id);
        } catch (Exception e) {
            throw new ArgumentException(ErrorEnum.ARGUMENT_ERROR.message(), e);
        }
    }

    public PlaceTag updatePlaceTag(Long id, PlaceTagRequest placeTagRequest) {
        try {
            PlaceTag placetag = placeTagDao.findById(id).get();
            if (placeTagRequest != null) {
                if (StringUtils.isNotBlank(placeTagRequest.getName())) {
                    placetag.setName(placeTagRequest.getName());
                }
                if (StringUtils.isNotBlank(placeTagRequest.getDescription())) {
                    placetag.setDescription(placeTagRequest.getDescription());
                }
            }
            placetag.setUpdateDate(new Date());
            return placeTagDao.save(placetag);
        } catch (Exception e) {
            throw new ArgumentException(ErrorEnum.ARGUMENT_ERROR.message(), e);
        }
    }

    public PlaceTag findOneTag(Long id) {
        try {
        	return placeTagDao.findById(id).get();
        }catch (Exception e) {
            throw new DBException(ErrorEnum.DB_ERROR.message(), e);
        }
    }
}
