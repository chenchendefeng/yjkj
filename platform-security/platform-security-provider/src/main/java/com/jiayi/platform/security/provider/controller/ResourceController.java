package com.jiayi.platform.security.provider.controller;

import com.google.common.collect.Lists;
import com.jiayi.platform.security.core.dto.JsonObject;
import com.jiayi.platform.security.core.service.ResourceService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/resources")
public class ResourceController {
    @Autowired
    private ResourceService resourceService;

    @Value("#{'${platform.versions}'.split(',')}")
    private List<Short> versions;

    @GetMapping()
    @ResponseBody
    public JsonObject<?> resourceTree() {
        if(CollectionUtils.isEmpty(versions)){
            versions = Lists.newArrayList();
            versions.add((short)0);
        }
        return new JsonObject<>(resourceService.tree(versions));
    }
}
