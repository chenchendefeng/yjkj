package com.jiayi.platform.security.gataway.service;

import com.jiayi.platform.security.core.vo.UserDetailVO;
import com.jiayi.platform.security.gataway.core.dto.UserParam;
import com.jiayi.platform.security.gataway.core.dto.UserTokenParam;
import com.jiayi.platform.security.core.entity.User;
import com.jiayi.platform.security.core.entity.part.UserPwd;
import com.jiayi.platform.security.core.enums.ErrorEnum;
import com.jiayi.platform.security.core.util.JWTUtil;
import com.jiayi.platform.security.gataway.core.dto.AuthCode;
import com.jiayi.platform.security.core.dao.UserDao;
import com.jiayi.platform.security.gataway.exception.ServiceException;
import com.jiayi.platform.security.gataway.exception.ValidException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Transactional
@Slf4j
@Service
public class AuthenticationService {

    private final UserDao userDao;

    private final JdbcTemplate jdbcTemplate;

    //URL地址前缀
    @Value("${platform-source.file-portrait-prefix}")
    private String remotePrefix;
    //刷新token过期时间
    @Value("${platform-security.portrait-expire-time:604800000}")
    private int expireTime;
    //token过期时间
    @Value("${platform-security.token-expire-time:300000}")
    private int tokenExpireTime;

    @Value("${platform-security-gateway-sql.get-user-auth-codes}")
    private String SQL_GET_AUTH_CODES;

    @Autowired
    public AuthenticationService(UserDao userDao, JdbcTemplate jdbcTemplate) {
        this.userDao = userDao;
        this.jdbcTemplate = jdbcTemplate;
    }

    public UserDetailVO login(UserParam param) {
        User user = userDao.findUserByName(param.getUsername());
        this.valid(user, param);
        try {
            final String uid = String.valueOf(user.getId());
            String token = JWTUtil.sign(uid, tokenExpireTime);
            String refreshToken = JWTUtil.sign(uid, expireTime);
            user.setRefreshToken(refreshToken);
            userDao.updateToken(token, user.getId());
            if (StringUtils.isNotBlank(user.getPortraitUrl()))
                user.setRealPath(remotePrefix + user.getPortraitUrl());
            UserDetailVO vo = new UserDetailVO(user);
            vo.setToken(token);
            log.info(String.format("User %s token is: %s", uid, token));
            return vo;
        } catch (Exception e) {
            log.warn("登录失败", e);
            return null;
        }

    }

    public String refresh(UserTokenParam userToken) {
        String token;
        String uid = userToken.getId();
        try {
            if (JWTUtil.verifyFreshToken(userToken.getToken(), userToken.getId())) {
                token = JWTUtil.sign(uid, tokenExpireTime);
                userDao.updateToken(token, Long.parseLong(uid));
                return token;
            }

        } catch (Exception e) {
            throw new ServiceException(ErrorEnum.TOKEN_ERROR.getCode(),
                    ErrorEnum.TOKEN_ERROR.getMessage());
        }
        return null;
    }

    public void unlock(UserParam param) {
        UserPwd user = userDao.findPwdByName(param.getUsername());
        if (user == null) throw new ValidException("用户名不存在");
        this.valid(user.copy2User(), param);
    }

    /**
     * 验证用户
     */
    private void valid(User user, UserParam param) {
        if (user == null) throw new ValidException("用户名不存在");
        if (!user.getPassword().equals(param.getPassword())) throw new ValidException("密码错误");
        if (user.getBeActive() == 0) throw new ValidException("用户状态为禁用");
    }

    public Set<String> getAuthCodes(long userId) {
        return jdbcTemplate
                .query(SQL_GET_AUTH_CODES, new BeanPropertyRowMapper<>(AuthCode.class), userId)
                .stream()
                .map(AuthCode::getCode)
                .collect(Collectors.toSet());
    }
}

