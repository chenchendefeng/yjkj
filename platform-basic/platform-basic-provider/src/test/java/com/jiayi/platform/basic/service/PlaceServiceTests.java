package com.jiayi.platform.basic.service;

import com.jiayi.platform.basic.serviceImpl.PlaceServiceImpl;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : qinxiaoni
 * @date : 2019-05-16 18:15
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class PlaceServiceTests {
    @Autowired
    private PlaceServiceImpl placeService;

    @Test
    public void createPlaceCode(){
        System.out.println("生成的随机数："+RandomUtils.nextLong(000000, 999999));
        List<Long> placeTags = new ArrayList<>();
        placeTags.add(1L);
        System.out.println("生成的场所编码：" + placeService.generatePlaceCode("441284",placeTags,2));
    }
}
