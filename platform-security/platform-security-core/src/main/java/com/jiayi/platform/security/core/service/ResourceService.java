package com.jiayi.platform.security.core.service;

import com.jiayi.platform.security.core.entity.Resource;
import com.jiayi.platform.security.core.dao.ResourceDao;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional
@Service
public class ResourceService {
    @Autowired
    private ResourceDao resourceDao;

    public List<Resource> tree(List<Short> versions) {
        List<Resource> resources = resourceDao.findByVersionsIn(versions);

        if (CollectionUtils.isEmpty(resources)) {
            return Collections.EMPTY_LIST;
        }

        Map<Integer, Resource> resourceMap = resources.stream().collect(
                Collectors.toMap(Resource::getId,Function.identity()));
        List<Resource> topResources = new ArrayList<>();
        resourceMap.forEach((id, resource) -> {
            if (resource.getParentId() == 0) {
                topResources.add(resource);
                return;
            }

            Resource parent = resourceMap.get(resource.getParentId());
            if (parent != null) {
                parent.getChildren().add(resource);
            }
        });
        return topResources;
    }
}
