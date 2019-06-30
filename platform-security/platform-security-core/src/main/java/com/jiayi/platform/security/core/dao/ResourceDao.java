package com.jiayi.platform.security.core.dao;

import com.jiayi.platform.security.core.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface ResourceDao extends JpaRepository<Resource, Integer> {
    Set<Resource> findByIsDefault(short isDefault);
    List<Resource> findByVersionsIn(List<Short> isBigVersion);
}
