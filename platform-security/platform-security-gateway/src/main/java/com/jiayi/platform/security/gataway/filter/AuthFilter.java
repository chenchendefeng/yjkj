package com.jiayi.platform.security.gataway.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.jiayi.platform.security.core.dto.JsonObject;
import com.jiayi.platform.security.gataway.core.config.AuthCodes;
import com.jiayi.platform.security.core.util.JWTUtil;
import com.jiayi.platform.security.gataway.service.AuthenticationService;
import io.netty.buffer.ByteBufAllocator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.apache.commons.lang.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
public class AuthFilter implements GlobalFilter, Ordered {

	private final AuthenticationService authenticationService;

	private final byte[] UN_AUTH = "{\"payLoad\": null,\"message\": \"非法操作\",\"code\": 401}"
			.getBytes(StandardCharsets.UTF_8);

	private final byte[] TOKEN_EXPIRED = "{\"payLoad\": null,\"message\": \"token 过期\",\"code\": 100}"
			.getBytes(StandardCharsets.UTF_8);

	private final byte[] UNKNOWN_USER = "{\"payLoad\": null,\"message\": \"未知用户，请重新登录\",\"code\": 101}"
			.getBytes(StandardCharsets.UTF_8);

	private final Map<String, String> AUTH_CODES;
	private final String SPILT_STR;
	private final String PARAM_STR;

	@Autowired
	public AuthFilter (AuthCodes authCodes, AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
		AUTH_CODES = authCodes.getCodes();
		SPILT_STR = authCodes.getSplit();
		PARAM_STR = authCodes.getParam();
	}

	@Override
	public Mono<Void> filter (ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();
		HttpHeaders headers = request.getHeaders();
		List<String> authorization = headers.get("Authorization");
		String token = null;
		if (authorization != null && authorization.size() > 0) token = authorization.get(0);
		String userId = null;
		//获取user ID
		if (token != null) userId = JWTUtil.getUserId(token);
		//判断token的合法性
		boolean pass;
		String path = request.getPath().value();
		String ip = getClientIP(request);
		try {
			pass = (userId != null && JWTUtil.verify(token, userId));
		} catch (TokenExpiredException ex) {
			log.warn(String.format("用户ID[%s] ip[%s] 访问 %s token 失效！[%s]", userId, ip, path, ex.getMessage()));
			return unPass(exchange, HttpStatus.INTERNAL_SERVER_ERROR, TOKEN_EXPIRED);
		}
		if (userId == null) {
			log.warn("未知用户，请重新登录");
			return unPass(exchange, HttpStatus.SWITCHING_PROTOCOLS, UNKNOWN_USER);
		}
		//进行权限验证
		if (pass) {
			int pl = path.length();
			if (path.endsWith("/") && pl > 1) path = path.substring(0, pl - 1);
			String method = "METHOD_NULL";
			try {
				method = Objects.requireNonNull(request.getMethod()).name();
			} catch (NullPointerException ex) {
				log.warn(String.format("用户ID[%s] ip[%s] 访问 %s 无 method！", userId, ip, path));
				pass = false;
			}
			Object body=null;
			if ("POST".equals(method)) {
				body = exchange.getAttribute("cachedRequestBodyObject");

			} else  {
				Map requestQueryParams = request.getQueryParams();
				body =JSONObject.toJSON(requestQueryParams);
			}
			//获取权限code
			String code = null;
			String path_=null;
			if (pass) {
				StringBuilder buffer = new StringBuilder();
				for (String p : path.split("/")) {
					if (p.length() > 0) {
						buffer.append("_");
						if (StringUtils.isNumeric(p)) buffer.append(PARAM_STR);
						else buffer.append(p);
					}
				}
				buffer.append(SPILT_STR);
				buffer.append(method);
				path_ = buffer.toString();
				code = AUTH_CODES.getOrDefault(path_, null);
			}
			if (code == null) {
				path_="ONLY_LOGIN";
				code ="ONLY_LOGIN";
				pass = true;
			} else {
				//判断用户是否拥有该code的权限
				long uid = -1L;
				try {
					uid = Long.parseLong(userId);
				} catch (NumberFormatException ex) {
					log.warn(String.format("用户ID[%s] ip[%s] 访问 %s 用户ID非法！", userId, ip, path));
					pass = false;
				}
				if (pass) {
					Set<String> acs = authenticationService.getAuthCodes(uid);
					if (code.contains(",")) {
						for (String c : code.split(",")) {
							pass = acs.contains(c);
							if (pass) break;
						}
					} else pass = acs.contains(code);
				}
			}
			if (!pass)
				log.warn(String.format("用户ID[%s] ip[%s] 访问 %s[%s] 无系统权限！", userId, ip, code, path));
			else {
				if(path.indexOf("#")>=0||path_.indexOf("#")>=0||code.indexOf("#")>=0){
					log.error("path:{},path_:{},code:{}",pass,path_,code);
				}
				if(body==null){
					body="";
				}
				log.info(String.format("userId==%s#ip==%s#path==%s#key==%s#code==%s#param==%s", userId, ip, path, path_, code,body.toString()));
			}
		} else {
			log.warn(String.format("用户ID[%s] ip[%s] 访问 %s token验证未通过！", userId, ip, path));
		}
		if (!pass) {
			log.warn(String.format("用户ID[%s] ip[%s] 访问 %s 权限验证未通过！", userId, ip, path));
			return unPass(exchange, HttpStatus.UNAUTHORIZED, UN_AUTH);
		}
		return chain.filter(exchange);
	}

	private Mono<Void> unPass (ServerWebExchange exchange, HttpStatus status, byte[] info) {
		ServerHttpResponse response = exchange.getResponse();
		DataBuffer buffer = response.bufferFactory().wrap(info);
		response.setStatusCode(status);
		response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
		return response.writeWith(Mono.just(buffer));
	}

	@Override
	public int getOrder () {
		return 0;
	}

	public static String getClientIP (ServerHttpRequest request) {
		String fromSource = "X-Real-IP";
		String ip = request.getHeaders().getFirst("X-Real-IP");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeaders().getFirst("X-Forwarded-For");
			fromSource = "X-Forwarded-For";
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeaders().getFirst("Proxy-Client-IP");
			fromSource = "Proxy-Client-IP";
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeaders().getFirst("WL-Proxy-Client-IP");
			fromSource = "WL-Proxy-Client-IP";
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeaders().getHost().getHostString();
			fromSource = "request.getRemoteAddr";
		}
		log.debug("App Client IP: " + ip + ", fromSource: " + fromSource);
		return ip;
	}
}
