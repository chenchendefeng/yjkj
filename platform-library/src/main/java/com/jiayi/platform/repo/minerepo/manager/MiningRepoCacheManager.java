package com.jiayi.platform.repo.minerepo.manager;

import com.alibaba.fastjson.JSON;

import com.jiayi.platform.repo.minerepo.dao.MiningRepoDao;
import com.jiayi.platform.repo.minerepo.entity.MiningRepo;
import com.jiayi.platform.repo.minerepo.vo.MiningRepoTableDesc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MiningRepoCacheManager {
    private static final Logger log = LoggerFactory.getLogger(MiningRepoCacheManager.class);

    @Autowired
    private MiningRepoDao miningRepoDao;

//    @Cacheable("miningRepo")

    @Cacheable(key = "#id", value = "miningRepo")
    public MiningRepo getMineReopById(Integer id) {
        log.info("perf: no cache, in getMineRepoById="+id);
        MiningRepo repo = miningRepoDao.selectMineRepoById(id);
        MiningRepoTableDesc tableDesc = JSON.parseObject(repo.getDetailTableDesc(), MiningRepoTableDesc.class);
        repo.setDetailTableDescObj(tableDesc);
        return repo;
    }

    @CachePut(key = "#repo.id", value = "miningRepo")
    public MiningRepo updateCache(MiningRepo repo) {
        return repo;
    }

    public List<MiningRepo> getAllMineRepo() {
        List<MiningRepo> miningRepos = miningRepoDao.selectMineRepo();
        for(MiningRepo miningRepo : miningRepos){
            MiningRepoTableDesc tableDesc = JSON.parseObject(miningRepo.getDetailTableDesc(), MiningRepoTableDesc.class);
            miningRepo.setDetailTableDescObj(tableDesc);
        }
        return miningRepos;
    }
}
