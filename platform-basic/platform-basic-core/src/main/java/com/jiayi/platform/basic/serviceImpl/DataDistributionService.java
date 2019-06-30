package com.jiayi.platform.basic.serviceImpl;

import com.jiayi.platform.basic.dao.DataDistributionDao;
import com.jiayi.platform.basic.entity.DataDistribution;
import com.jiayi.platform.basic.entity.Src;
import com.jiayi.platform.basic.request.DataDistributionRequest;
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
public class DataDistributionService {

	@Autowired
	private DataDistributionDao dataDistributionDao;

	public PageResult<?> search(Integer page, Integer size) {
		if(page == null)
			page = 0;
		if(size == null)
			size = 10;
		Sort sort = Sort.by(Sort.Direction.DESC, "id");
		Pageable pageable = PageRequest.of(page, size, sort);
		Page<DataDistribution> pageResult = dataDistributionDao.findAll(pageable);
		List<DataDistribution> list = pageResult.getContent();
		return new PageResult<>(list, pageResult.getTotalElements(), page, list.size());
	}

	public DataDistribution add(DataDistributionRequest request) {
		DataDistribution dataDistribution = new DataDistribution();
		this.toEntity(dataDistribution, request);
		dataDistribution.setCreateDate(new Date());
		dataDistributionDao.save(dataDistribution);
		return dataDistribution;
	}

	private void toEntity(DataDistribution dataDistribution, DataDistributionRequest request) {
		if(StringUtils.isNotBlank(request.getName()))
			dataDistribution.setName(request.getName());
		if(request.getSrcId() != null) {
			Src src = new Src();
			src.setId(request.getSrcId());
			dataDistribution.setSrc(src);
		}
		if(request.getIsAutoMode() != null)
			dataDistribution.setIsAutoMode(request.getIsAutoMode());
		if(request.getIsActive() != null)
			dataDistribution.setIsActive(request.getIsActive());
		if(StringUtils.isNotBlank(request.getProcessor()))
			dataDistribution.setProcessor(request.getProcessor());
		if(request.getFileList() != null)
			dataDistribution.setFileList(request.getFileList());
		if(StringUtils.isNotBlank(request.getDownloaderType()))
			dataDistribution.setDownloaderType(request.getDownloaderType());
		if(request.getDownloaderParam() != null)
			dataDistribution.setDownloaderParam(request.getDownloaderParam());
		if(StringUtils.isNotBlank(request.getKafkaParam()))
			dataDistribution.setKafkaParam(request.getKafkaParam());
		dataDistribution.setUpdateDate(new Date());
	}

	public void delete(Integer id) {
		dataDistributionDao.deleteById(id);
	}

	public DataDistribution modify(Integer id, DataDistributionRequest request) {
		DataDistribution dataDistribution = dataDistributionDao.findById(id).get();
		this.toEntity(dataDistribution, request);
		dataDistributionDao.save(dataDistribution);
		return dataDistribution;
	}
}
