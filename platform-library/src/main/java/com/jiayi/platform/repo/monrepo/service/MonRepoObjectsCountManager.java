package com.jiayi.platform.repo.monrepo.service;

import com.jiayi.platform.repo.monrepo.dao.MonRepoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// todo: optimize cache methods use condition.
@Component
public class MonRepoObjectsCountManager {
    @Autowired
    private MonRepoDao monRepoDao;

    @Cacheable(key = "'all'", value = "monRepoPersonCount")
    public Map<Long, Long> countAllMonRepoPersons() {
        return countMonRepoPersons(null);
    }

    @Cacheable(key = "'all'", value = "monRepoObjectCount")
    public Map<Long, Long> countAllMonRepoObjects() {
        return countMonRepoPersons(null);
    }

    @CachePut(key = "'all'", value = "monRepoPersonCount")
    public Map<Long, Long> updatePersonCount() {
        return countMonRepoPersons(null);
    }

    @CachePut(key = "'all'", value = "monRepoObjectCount")
    public Map<Long, Long> updateObjectCount() {
        return countMonRepoObjects(null);
    }

    public Map<Long, Long> countMonRepoPersons(List<Long> repoIds) {
        List<Map<String, Long>> countResult = monRepoDao.countMonRepoPersons(repoIds);
        Map<Long, Long> resultMap = new HashMap<>();
        if (countResult != null) {
            for (Map<String, Long> r : countResult) {
                resultMap.put(r.get("repoid"), r.get("cnt"));
            }
        }
        return resultMap;
    }

    public Map<Long, Long> countMonRepoObjects(List<Long> repoIds) {
        List<Map<String, Long>> countResult = monRepoDao.countMonRepoObjects(repoIds);
        Map<Long, Long> resultMap = new HashMap<>();
        if (countResult != null) {
            for (Map<String, Long> r : countResult) {
                resultMap.put(r.get("repoid"), r.get("cnt"));
            }
        }
        return resultMap;
    }
}
