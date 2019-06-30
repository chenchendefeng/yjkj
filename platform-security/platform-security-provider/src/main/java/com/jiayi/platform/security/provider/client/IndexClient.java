package com.jiayi.platform.security.provider.client;

import com.jiayi.platform.security.core.dto.JsonObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;


@FeignClient(value = "PLATFORM-BASIC-PROVIDER", path = "basic")
public interface IndexClient {

    @GetMapping("/department/flush")
    JsonObject flushDepartment();
}
