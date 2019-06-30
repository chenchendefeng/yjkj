package com.jiayi.platform.basic.service;

import com.jiayi.platform.basic.serviceImpl.DeviceSubTypeService;
import com.jiayi.platform.common.vo.ModifyRemark;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author : weichengke
 * @date : 2019-04-13 14:56
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class DeviceSubTypeServiceTest {

    @Autowired
    private DeviceSubTypeService deviceTypeService;


    @Test
    public void modifyRemark(){
        ModifyRemark remark = new ModifyRemark();
        remark.setId(1);
        remark.setRemark("hello");
        deviceTypeService.modifyRemark(remark);
    }
}
