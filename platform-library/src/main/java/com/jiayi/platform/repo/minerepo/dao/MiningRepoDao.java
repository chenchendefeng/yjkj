package com.jiayi.platform.repo.minerepo.dao;

import com.jiayi.platform.repo.minerepo.dto.MiningRepoDto;
import com.jiayi.platform.repo.minerepo.entity.MiningRepo;
import com.jiayi.platform.repo.minerepo.vo.MiningRepoSearchVo;
import com.jiayi.platform.repo.minerepo.vo.RepoResponse;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface MiningRepoDao {
    List<MiningRepoDto> selectMineRepoList (MiningRepoSearchVo mineRepoSearchVo);
    MiningRepo selectMineRepoById (Integer id);
    Long countMineRepoName (String repoName);
    void updateMineRepo (MiningRepo mineRepo);
    List<Map<String, Object>> selectMineRepoDetail (String sql);
    Long countMineRepoDetail (String sql);
    void insertMineRepoDetail (String sql);
    void deleteMineRepoDetail (String sql);
    void batchInsertRepoDetails (String sql);
    void updateMineRepoDetailCount (@Param ("id") Long id, @Param ("count") Long count);

    List<RepoResponse> getRepoInfo ();

    List<MiningRepo> selectMineRepo ();

    List<Map<String, Object>> search (@Param ("miningRepos") List<MiningRepo> miningRepos, @Param ("values") List<String> values,
									  @Param ("limit") Integer limit, @Param ("offset") Integer offset);

    List<Map<String, Object>> groupByObjType (@Param ("miningRepos") List<MiningRepo> miningRepos, @Param ("values") List<String> values);

    Long countSearch (@Param ("miningRepos") List<MiningRepo> miningRepos, @Param ("value") String value);
}
