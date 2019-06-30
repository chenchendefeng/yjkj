package com.jiayi.platform.security.gataway.controller;

import com.jiayi.platform.security.core.dto.JsonObject;
import com.jiayi.platform.security.gataway.core.dto.UserParam;
import com.jiayi.platform.security.gataway.core.dto.UserTokenParam;
import com.jiayi.platform.security.gataway.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * 用户登录
     */
    @PostMapping(value = "/login")
    @ResponseBody
    public JsonObject<?> login(@RequestBody @Valid UserParam userParam){
        return new JsonObject<>(authenticationService.login(userParam));
    }

    /**
     * 刷新token
     */
    @PostMapping(value = "/refreshToken")
    @ResponseBody
    public JsonObject<?> refresh(@RequestBody @Valid UserTokenParam userTokenParam){
        String accessToken = authenticationService.refresh(userTokenParam);
        return new JsonObject<>(accessToken);
    }

    /**
     * 用户解锁
     */
    @PostMapping(value = "/unlock")
    @ResponseBody
    public JsonObject<?> unlock(@RequestBody @Valid UserParam userParam){
        authenticationService.unlock(userParam);
        return new JsonObject<>("");
    }
}
