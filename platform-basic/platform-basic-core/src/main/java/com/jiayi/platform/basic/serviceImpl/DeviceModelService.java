package com.jiayi.platform.basic.serviceImpl;

import com.jiayi.platform.basic.dao.DeviceExtensionDao;
import com.jiayi.platform.basic.dao.DeviceModelDao;
import com.jiayi.platform.basic.entity.DeviceModel;
import com.jiayi.platform.basic.enums.DataTypeEnum;
import com.jiayi.platform.basic.request.DeviceModelRequest;
import com.jiayi.platform.common.enums.ErrorEnum;
import com.jiayi.platform.common.exception.ArgumentException;
import com.jiayi.platform.common.exception.DBException;
import com.jiayi.platform.common.exception.ValidException;
import com.jiayi.platform.common.web.dto.PageResult;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceModelService {
    @Autowired
    private DeviceModelDao deviceModelDao;
    @Autowired
    private DeviceExtensionDao deviceExtensionDao;

    public PageResult<?> findAll(Integer page, Integer size) {
        if (page == null)
            page = 0;
        if (size == null)
            size = 10;
        try {
            Sort sort = Sort.by(Sort.Direction.DESC, "id");
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<DeviceModel> pageResult = deviceModelDao.findAll(pageable);
            List<DeviceModel> data = pageResult.getContent();
            return new PageResult<>(data, pageResult.getTotalElements(), page, data.size());
        } catch (Exception e) {
            throw new DBException(ErrorEnum.DB_ERROR.message(), e);
        }
    }

    public DeviceModel add(DeviceModelRequest deviceModelRequest) {
        List<DeviceModel> models = deviceModelDao.findByName(deviceModelRequest.getName());
        models.forEach(item -> {
            if (item.getDeviceSubType() == deviceModelRequest.getDeviceSubType() && item.getVendorId() == deviceModelRequest.getVendorId())
                throw new ArgumentException("该型号已存在");
        });
        try {
            DeviceModel deviceModel = new DeviceModel();
            deviceModel.setName(deviceModelRequest.getName());
            deviceModel.setDeviceSubType(deviceModelRequest.getDeviceSubType());
            deviceModel.setVendorId(deviceModelRequest.getVendorId());
            deviceModel.setDescription(deviceModelRequest.getDescription());
            return deviceModelDao.save(deviceModel);
        } catch (Exception e) {
            throw new ArgumentException(ErrorEnum.ARGUMENT_ERROR.message(), e);
        }
    }

    public DeviceModel findOne(int id) {
        try {
            return deviceModelDao.findById(id).get();
        } catch (Exception e) {
            throw new DBException(ErrorEnum.DB_ERROR.message(), e);
        }
    }

    public void modify(int id, DeviceModelRequest deviceModelRequest) {
        DeviceModel deviceModel = findOne(id);
        try {
            if (StringUtils.isNotBlank(deviceModelRequest.getName()))
                deviceModel.setName(deviceModelRequest.getName());
            if (null != deviceModelRequest.getDeviceSubType())
                deviceModel.setDeviceSubType(deviceModelRequest.getDeviceSubType());
            if (null != deviceModelRequest.getVendorId())
                deviceModel.setVendorId(deviceModelRequest.getVendorId());
            if (StringUtils.isNotBlank(deviceModelRequest.getDescription()))
                deviceModel.setDescription(deviceModelRequest.getDescription());
            deviceModelDao.save(deviceModel);
        } catch (Exception e) {
            throw new ArgumentException(ErrorEnum.ARGUMENT_ERROR.message(), e);
        }
    }

    public void delete(int id) {
        DataTypeEnum message = DataTypeEnum.DEVICE;
        int count = deviceExtensionDao.findByModel(id);
        if (count > 0)
            throw new ValidException("删除失败，型号类型已关联" + message.getDescription());
        try {
            deviceModelDao.deleteById(id);
        } catch (Exception e) {
            throw new ArgumentException(ErrorEnum.ARGUMENT_ERROR.message(), e);
        }
    }

    public void delete(List<Integer> ids) {
        DataTypeEnum message = DataTypeEnum.DEVICE;
        for (int id : ids) {
            int count = deviceExtensionDao.findByModel(id);
            if (count > 0)
                throw new ValidException("删除失败，型号类型已关联" + message.getDescription());
            try {
                deviceModelDao.deleteById(id);
            } catch (Exception e) {
                throw new ArgumentException(ErrorEnum.ARGUMENT_ERROR.message(), e);
            }
        }
    }
}
