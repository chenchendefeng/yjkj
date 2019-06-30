package com.jiayi.platform.basic.serviceImpl;

import com.jiayi.platform.basic.dao.DataAccessDao;
import com.jiayi.platform.basic.entity.DataAccess;
import com.jiayi.platform.basic.entity.Src;
import com.jiayi.platform.basic.request.DataAccessRequest;
import com.jiayi.platform.common.enums.ErrorEnum;
import com.jiayi.platform.common.exception.ArgumentException;
import com.jiayi.platform.common.web.dto.PageResult;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DataAccessService {

	@Autowired
	private DataAccessDao dataAccessDao;

	public PageResult<DataAccess> search(Integer page, Integer size) {
		if(page == null)
			page = 0;
		if(size == null)
			size = 10;
		Sort sort = Sort.by(Sort.Direction.DESC, "id");
		Pageable pageable = PageRequest.of(page, size, sort);
		Page<DataAccess> pageResult = dataAccessDao.findAll(pageable);
		List<DataAccess> dataAccesses = pageResult.getContent();
		return new PageResult<>(dataAccesses, pageResult.getTotalElements(), page, dataAccesses.size());
	}

	public DataAccess add(DataAccessRequest request) {
		try {
			DataAccess dataAccess = new DataAccess();
			this.toEntity(dataAccess, request);
			dataAccess.setCreateDate(new Date());
			dataAccessDao.save(dataAccess);
			return dataAccess;
		} catch (Exception e) {
			throw new ArgumentException(ErrorEnum.ARGUMENT_ERROR.message(), e);
		}
	}

	private void toEntity(DataAccess dataAccess, DataAccessRequest request) {
		if(StringUtils.isNotBlank(request.getName()))
			dataAccess.setName(request.getName());
		if(request.getSrcId() != null) {
			Src src = new Src();
			src.setId(request.getSrcId());
			dataAccess.setSrc(src);
		}
		if(request.getPriority() != null)
			dataAccess.setPriority(request.getPriority());
		if(request.getGeoType() != null)
			dataAccess.setGeoType(request.getGeoType());
		if(request.getExpireDays() != null)
			dataAccess.setExpireDays(request.getExpireDays());
		if(request.getIsAutoMode() != null)
			dataAccess.setIsAutoMode(request.getIsAutoMode());
		if(request.getIsActive() != null)
			dataAccess.setIsActive(request.getIsActive());
		if(StringUtils.isNotBlank(request.getProcessor()))
			dataAccess.setProcessor(request.getProcessor());
		if(request.getFileList() != null)
			dataAccess.setFileList(request.getFileList());
		if(StringUtils.isNotBlank(request.getDownloaderType()))
			dataAccess.setDownloaderType(request.getDownloaderType());
		if(request.getDownloaderParam() != null)
			dataAccess.setDownloaderParam(request.getDownloaderParam());
		if(StringUtils.isNotBlank(request.getKafkaParam()))
			dataAccess.setKafkaParam(request.getKafkaParam());
		dataAccess.setUpdateDate(new Date());
	}

	public void delete(Integer id) {
		dataAccessDao.deleteById(id);
	}

	public DataAccess modify(Integer id, DataAccessRequest request) {
		try {
			DataAccess dataAccess = dataAccessDao.findById(id).get();
			this.toEntity(dataAccess, request);
			dataAccessDao.save(dataAccess);
			return dataAccess;
		} catch (Exception e) {
			throw new ArgumentException(ErrorEnum.ARGUMENT_ERROR.message(), e);
		}
	}
}
