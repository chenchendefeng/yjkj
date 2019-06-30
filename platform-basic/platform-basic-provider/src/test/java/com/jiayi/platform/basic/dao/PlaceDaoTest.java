package com.jiayi.platform.basic.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PlaceDaoTest {

    @Autowired
    private PlaceDao placeDao;

    @Test
    public void findByName(){
        List<Map<String, String>> list = placeDao.findByFuzzyAddress("四会");
        List<Map<String, String>> list1 = placeDao.findByFuzzyName("四会");
        System.out.println(list.size());
        System.out.println(list1.size());
    }

    @Test
    public void countByCity(){
        int coutn = placeDao.countByCity("441200");
        System.out.println(coutn);
    }
}
