package com.jiayi.platform.repo.minerepo.dao;

import com.jiayi.platform.repo.minerepo.dto.MonitorObjectDto;
import com.jiayi.platform.repo.minerepo.vo.RepoResponse;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface MonitorRepoDao {

    String findRepoNameById (Long id);

    Long countMonitorObjectByRepoId (Long repoId);

    List<MonitorObjectDto> selectMonitorObjectByRepoId (@Param ("repoId") Long repoId, @Param ("limit") Integer limit, @Param ("offset") Long offset);

    List<RepoResponse> getRepoByUserId (@Param ("userId") Long userId, @Param ("possibleUserStr") List<String> possibleUserStr);

    List<Map<String, Object>> search (@Param ("values") List<String> values, @Param ("userId") Long userId, @Param ("possibleUserStr") List<String> possibleUserStr, @Param ("limit") Integer limit, @Param ("offset") Long offset);

    List<Map<String, Object>> groupByObjType (@Param ("values") List<String> values, @Param ("userId") Long userId, @Param ("possibleUserStr") List<String> possibleUserStr);
}
