package com.jiayi.platform.basic.service;

import com.jiayi.platform.security.core.util.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@Component
@WebFilter(filterName = "accessLog", urlPatterns = "/*")
public class AccessLogFilter implements Filter {

    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
//        long startTime = System.currentTimeMillis();
//        ResponseWrapper wrapper = new ResponseWrapper((HttpServletResponse) response);
//        HttpServletRequest req = (HttpServletRequest) request;
//
//        chain.doFilter(request, wrapper);
//        long endTime = System.currentTimeMillis();
//        Gson gson = new Gson();
//        // 获取response返回的内容并重新写入response
//
//        String result = wrapper.getResponseData(response.getCharacterEncoding());
//        response.getOutputStream().write(result.getBytes());
//
//
//        String uri = req.getRequestURI();
//        AccessLog log =  new AccessLog();
//
//        log.setMethod(req.getMethod());
//        log.setUrl(uri);
//        log.setParameters(gson.toJson(req.getParameterMap()));
//        log.setResponseCode(String.valueOf(wrapper.getStatus()));
//        log.setResult(result);
//        log.setCreateDatetime(new Date());
//        log.setTimeConsuming((int)(endTime - startTime));
//        accessLogMapper.insertSelective(log);

        System.out.println("AccessLogFilter执行前！！！");

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String authorizationToken = httpRequest.getHeader("Authorization");
        String userId = null;
        //获取user ID
        if (authorizationToken != null) userId = JWTUtil.getUserId(authorizationToken);

        log.info("userId:"+userId);

//        log.info(String.format("userId==%s#ip==%s#path==%s#key==%s#code==%s#param==%s", userId, ip, path, path_, code,body.toString()));

        filterChain.doFilter(request, response); // 让目标资源执行，放行
        System.out.println("AccessLogFilter执行后！！！");

    }

    public void init(FilterConfig fConfig) throws ServletException {

    }





}
