package com.jiayi.platform.repo.monrepo.dao;


import com.jiayi.platform.repo.monrepo.dto.MonRepoDto;
import com.jiayi.platform.repo.monrepo.entity.MonitorRepo;
import com.jiayi.platform.repo.monrepo.vo.MonRepoSearchVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface MonRepoDao {
    List<MonRepoDto> selectMonRepoList (@Param ("searchVo") MonRepoSearchVo searchVo, @Param ("limit") Integer limit,
                                        @Param ("offset") Integer offset, @Param ("userId") Long userId, @Param ("possibleUserStr") List<String> possibleUserStr);
    Long countMonRepoList (@Param ("searchVo") MonRepoSearchVo searchVo, @Param ("limit") Integer limit,
						   @Param ("offset") Integer offset, @Param ("userId") Long userId, @Param ("possibleUserStr") List<String> possibleUserStr);
    MonitorRepo selectMonRepoById (Long uid);
    Long countMonRepoName (String repoName);
    void insertMonRepo (MonitorRepo monitorRepo);
    void updateMonRepo (MonitorRepo monitorRepo);
    void deleteMonRepoById (Long uid);
    Long selectMaxUid ();
    List<Map<String, Long>> countMonRepoPersons (@Param ("repoIds") List<Long> repoIds);
    List<Map<String, Long>> countMonRepoObjects (@Param ("repoIds") List<Long> repoIds);
}
