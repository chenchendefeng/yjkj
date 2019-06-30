package com.jiayi.platform.basic.serviceImpl;

import com.jiayi.platform.basic.dao.DeviceModelDao;
import com.jiayi.platform.basic.dao.DeviceSubTypeDao;
import com.jiayi.platform.basic.dao.VendorDao;
import com.jiayi.platform.basic.dto.DeviceSubTypeDto;
import com.jiayi.platform.basic.entity.DeviceModel;
import com.jiayi.platform.basic.entity.DeviceSubType;
import com.jiayi.platform.basic.entity.Vendor;
import com.jiayi.platform.basic.enums.DataTypeEnum;
import com.jiayi.platform.basic.request.DeviceSubTypeRequest;
import com.jiayi.platform.common.enums.CollectType;
import com.jiayi.platform.common.enums.ErrorEnum;
import com.jiayi.platform.common.exception.ArgumentException;
import com.jiayi.platform.common.exception.DBException;
import com.jiayi.platform.common.exception.ValidException;
import com.jiayi.platform.common.jpa.DaoUtils;
import com.jiayi.platform.common.vo.ModifyRemark;
import com.jiayi.platform.common.web.dto.PageResult;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DeviceSubTypeService {

    @Autowired
    private DeviceSubTypeDao deviceSubTypeDao;
    @Autowired
    private DeviceModelDao deviceModelDao;
    @Autowired
    private VendorDao vendorDao;

    @Autowired
    private DaoUtils daoUtils;

    public PageResult<?> findAll(Integer page, Integer size) {
        if (page == null)
            page = 0;
        if (size == null)
            size = 10;
        try {
            Sort sort = Sort.by(Sort.Direction.DESC, "id");
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<DeviceSubType> pageResult = deviceSubTypeDao.findAll(pageable);
            List<DeviceSubType> data = pageResult.getContent();
            return new PageResult<>(data, pageResult.getTotalElements(), page, data.size());
        } catch (Exception e) {
            throw new DBException(ErrorEnum.DB_ERROR.message(), e);
        }
    }

    public DeviceSubType add(DeviceSubTypeRequest deviceTypeRequest) {
        int count = deviceSubTypeDao.isNameUsed(deviceTypeRequest.getName());
        if (count > 0)
            throw new ValidException("名称重复");
        try {
            DeviceSubType deviceType = new DeviceSubType();
            deviceType.setName(deviceTypeRequest.getName());
            deviceType.setDeviceType(deviceTypeRequest.getDeviceType());
            deviceType.setDataType(deviceTypeRequest.getDataType());
            deviceType.setDescription(deviceTypeRequest.getDescription());
            deviceType.setCreateDate(new Date());
            deviceType.setUpdateDate(new Date());
            return deviceSubTypeDao.save(deviceType);
        } catch (Exception e) {
            throw new ArgumentException(ErrorEnum.ARGUMENT_ERROR.message(), e);
        }
    }

    public Map<String, String> getAllCollect() {
        Map<String, String> data = new HashMap<>();
        for (CollectType types : CollectType.values()) {
            data.put(types.name(), types.desc());
        }
        return data;
    }

    public DeviceSubType findOne(int id) {
        try {
            return deviceSubTypeDao.findById(id).get();
        } catch (Exception e) {
            throw new DBException(ErrorEnum.DB_ERROR.message(), e);
        }
    }

    public void modify(int id, DeviceSubTypeRequest deviceTypeRequest) {
        DeviceSubType deviceType = findOne(id);
        if (StringUtils.isNotBlank(deviceTypeRequest.getName()) && !deviceTypeRequest.getName().equals(deviceType.getName())) {
            int count = deviceSubTypeDao.isNameUsed(deviceTypeRequest.getName());
            if (count > 0)
                throw new ValidException("名称重复");
        }
        try {
            if (StringUtils.isNotBlank(deviceTypeRequest.getName()))
                deviceType.setName(deviceTypeRequest.getName());
            if (StringUtils.isNotBlank(deviceTypeRequest.getDataType()))
                deviceType.setDataType(deviceTypeRequest.getDataType());
            if (null != deviceTypeRequest.getDescription())
                deviceType.setDescription(deviceTypeRequest.getDescription());
            if (null != deviceTypeRequest.getDeviceType())
                deviceType.setDeviceType(deviceTypeRequest.getDeviceType());
            deviceType.setUpdateDate(new Date());
            deviceSubTypeDao.save(deviceType);
        } catch (Exception e) {
            throw new ArgumentException(ErrorEnum.ARGUMENT_ERROR.message(), e);
        }
    }

    public void delete(int id) {
        DataTypeEnum message = DataTypeEnum.DEVICE;
        int count = deviceSubTypeDao.isUsedInDevice((int) id);
        if (count > 0)
            throw new ValidException("删除失败，设备类型已关联" + message.getDescription());
        try {
            deviceSubTypeDao.deleteById(id);
        } catch (Exception e) {
            throw new ArgumentException(ErrorEnum.ARGUMENT_ERROR.message(), e);
        }
    }

    public void modifyRemark(ModifyRemark modifyRemark) {
        daoUtils.updateNativeById("t_device_sub_type", modifyRemark.getId(), new HashMap<String, Object>() {{
            put("description", modifyRemark.getRemark());
            put("update_date", new Date());
        }});
    }

    public DeviceSubTypeDto findAllType() {
        try {
            List<DeviceSubType> subTypeList = deviceSubTypeDao.findAll();
            List<Vendor> vendorList = vendorDao.findAll();
            List<DeviceModel> models = deviceModelDao.findAll();
            return new DeviceSubTypeDto(subTypeList, vendorList, models);
        } catch (Exception e) {
            throw new DBException(ErrorEnum.DB_ERROR.message());
        }
    }

    public List<?> findAll() {
        try {
            return deviceSubTypeDao.findAll();
        } catch (Exception e) {
            throw new DBException(ErrorEnum.DB_ERROR.message());
        }
    }
}
