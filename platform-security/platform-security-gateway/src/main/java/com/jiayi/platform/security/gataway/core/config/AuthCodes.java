package com.jiayi.platform.security.gataway.core.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "platform-security-auth")
public class AuthCodes {
    private Map<String, String> codes;
    private String split;
    private String param;
}
