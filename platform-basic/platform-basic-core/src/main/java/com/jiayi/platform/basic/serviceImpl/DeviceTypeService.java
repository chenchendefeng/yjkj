package com.jiayi.platform.basic.serviceImpl;

import com.jiayi.platform.basic.dao.DeviceSubTypeDao;
import com.jiayi.platform.basic.dao.DeviceTypeDao;
import com.jiayi.platform.basic.dto.DeviceTypeDto;
import com.jiayi.platform.basic.entity.DeviceSubType;
import com.jiayi.platform.basic.entity.DeviceType;
import com.jiayi.platform.common.enums.ErrorEnum;
import com.jiayi.platform.common.exception.DBException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DeviceTypeService {

    @Autowired
    private DeviceTypeDao deviceTypeDao;
    @Autowired
    private DeviceSubTypeDao deviceSubTypeDao;

    public List<?> findAll() {
        try {
            Sort sort = Sort.by(Sort.Direction.DESC, "id");
            List<DeviceType> data = deviceTypeDao.findAll();
            return data;
        } catch (Exception e) {
            throw new DBException(ErrorEnum.DB_ERROR.message(), e);
        }
    }

//    public PageResult<?> findAll(Integer page, Integer size) {
//        if (page == null)
//            page = 0;
//        if (size == null)
//            size = 10;
//        try {
//            Sort sort = Sort.by(Sort.Direction.DESC, "id");
//            Pageable pageable = PageRequest.of(page, size, sort);
//            Page<DeviceType> pageResult = deviceTypeDao.findAll(pageable);
//            List<DeviceType> data = pageResult.getContent();
//            return new PageResult<>(data, pageResult.getTotalElements(), page, data.size());
//        } catch (Exception e) {
//            throw new DBException(ErrorEnum.DB_ERROR.message(), e);
//        }
//    }
//
//    public DeviceType add(DeviceTypeRequest deviceTypeRequest) {
//        int count = deviceTypeDao.isNameUsed(deviceTypeRequest.getName());
//        if (count > 0)
//            throw new ValidException("名称重复");
//        try {
//            DeviceType deviceType = new DeviceType();
//            deviceType.setName(deviceTypeRequest.getName());
//            deviceType.setType(deviceTypeRequest.getType());
//            return deviceTypeDao.save(deviceType);
//        } catch (Exception e) {
//            throw new ArgumentException(ErrorEnum.ARGUMENT_ERROR.message(), e);
//        }
//    }
//
//    public DeviceType findOne(int id) {
//        try {
//            return deviceTypeDao.findById(id).get();
//        } catch (Exception e) {
//            throw new DBException(ErrorEnum.DB_ERROR.message(), e);
//        }
//    }
//
//    public void modify(int id, DeviceTypeRequest deviceTypeRequest) {
//        DeviceType deviceType = findOne(id);
//        if (StringUtils.isNotBlank(deviceTypeRequest.getName()) && !deviceTypeRequest.getName().equals(deviceType.getName())) {
//            int count = deviceTypeDao.isNameUsed(deviceTypeRequest.getName());
//            if (count > 0)
//                throw new ValidException("名称重复");
//        }
//        if (null != deviceTypeRequest.getType() && deviceTypeRequest.getType() != deviceType.getType()) {
//            int count = deviceTypeDao.isTypeUsed(deviceTypeRequest.getType());
//            if (count > 0)
//                throw new ValidException("type值重复");
//        }
//        try {
//            if (StringUtils.isNotBlank(deviceTypeRequest.getName()))
//                deviceType.setName(deviceTypeRequest.getName());
//            if (null != deviceTypeRequest.getType())
//                deviceType.setType(deviceTypeRequest.getType());
//            deviceTypeDao.save(deviceType);
//        } catch (Exception e) {
//            throw new ArgumentException(ErrorEnum.ARGUMENT_ERROR.message(), e);
//        }
//    }
//
//    public void delete(int id) {
//        DataTypeEnum message = DataTypeEnum.DEVICE;
//        int count = deviceTypeDao.isUsedInDevice(id);
//        if (count > 0)
//            throw new ValidException("删除失败，设备类型已关联" + message.getDescription());
//        try {
//            deviceTypeDao.deleteById(id);
//        } catch (Exception e) {
//            throw new ArgumentException(ErrorEnum.ARGUMENT_ERROR.message(), e);
//        }
//    }

    public List<DeviceTypeDto> tree() {
        List<DeviceType> pTypes = deviceTypeDao.findAll();
        List<DeviceSubType> subTypes = deviceSubTypeDao.findAll();
        Map<Integer, List<DeviceSubType>> subTypeMap = subTypes.stream().collect(Collectors.groupingBy(DeviceSubType::getDeviceType));
        List<DeviceTypeDto> resultList = new ArrayList<>();
        pTypes.forEach(a -> {
            List<DeviceSubType> subList = subTypeMap.get(a.getId());
            List<DeviceTypeDto> nextLevel = null;
            if (subList != null) {
                nextLevel = subList.stream().map(b -> new DeviceTypeDto(b.getId(), b.getName(), null)).collect(Collectors.toList());
            }
            DeviceTypeDto dto = new DeviceTypeDto(a.getId(), a.getName(), nextLevel);
            resultList.add(dto);
        });
        return resultList;
    }
}
