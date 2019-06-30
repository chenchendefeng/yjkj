package com.jiayi.platform.basic.manager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ObjectOrgManagerTest {

    @Autowired
    private ObjectOrganizationManager objectOrganizationManager;

    @Test
    public void getObjectOrganization(){
//        ObjectOrganizationInfo info = objectOrganizationManager.getObjectOrganization("mac", "E043DBE043DB");
        System.out.println("test");
    }
}
