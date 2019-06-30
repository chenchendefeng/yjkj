package com.jiayi.platform.repo.minerepo.dao;

import com.jiayi.platform.repo.minerepo.dto.MineRepoDto;
import com.jiayi.platform.repo.minerepo.entity.MineRepo;
import com.jiayi.platform.repo.minerepo.vo.MineRepoSearchVo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface MineRepoDao {
    List<MineRepoDto> selectMineRepoList (MineRepoSearchVo mineRepoSearchVo);
    MineRepo selectMineRepoById (Integer id);
    Long countMineRepoName (String repoName);
    void updateMineRepo (MineRepo mineRepo);
    List<Map<String, Object>> selectMineRepoDetail (String sql);
    Long countMineRepoDetail (String sql);
    void insertMineRepoDetail (String sql);
    void deleteMineRepoDetail (String sql);
    void batchInsertRepoDetails (String sql);
}
