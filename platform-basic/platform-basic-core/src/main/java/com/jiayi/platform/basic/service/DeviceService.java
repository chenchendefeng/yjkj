package com.jiayi.platform.basic.service;

import com.jiayi.platform.basic.entity.Device;
import com.jiayi.platform.common.enums.CollectType;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author : weichengke
 * @date : 2019-03-07 17:19
 */
public interface DeviceService {

    List<Device> findAll();

    List<Device> findByCollect(CollectType collect);

    List<Device> findByCollectId(Integer collectCode);

    Device findByPkId(Long pkId);

    Device findById(Long id);

    Map<Long, Device> findByPkIds(Set<Long> pkIds);

    Map<Long, Device> findByIds(Set<Long> ids);

    List<Device> findByMainType(@PathVariable("type") Integer type);
}
