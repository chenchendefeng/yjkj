package com.jiayi.platform.common.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.io.UnsupportedEncodingException;
import java.util.Date;

public class JWTUtil {

    // 过期时间5分钟
    private static final long EXPIRE_TIME = 24 * 60 * 60 * 1000;
    private static String secret = "2d2eb**************20dba3633";
    /**
     * 校验token是否正确
     *
     * @param token  密钥
     * @return 是否正确
     */
    public static boolean verify(String token, String userId) {
        try {
            DecodedJWT jwt = getDecodedJWT(token, userId, secret);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    private static DecodedJWT getDecodedJWT(String token, String userId, String secret) throws UnsupportedEncodingException {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier verifier = JWT.require(algorithm)
                .withClaim("userId", userId)
                .build();
        DecodedJWT jwt = verifier.verify(token);
        return jwt;

    }

    public static boolean verifyFreshToken(String token, String userId) throws Exception {
        DecodedJWT jwt = getDecodedJWT(token, userId, secret);
        return jwt.getExpiresAt().compareTo(new Date()) <= 0 ? true : false;
    }

    /**
     * 获得token中的信息无需secret解密也能获得
     *
     * @return token中包含的用户名
     */
    public static String getUserId(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("userId").asString();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    /**
     * 生成签名,5min后过期
     *
     * @param expireTime 过期时间配置
     * @return 加密的token
     */
    public static String sign(String userId, int expireTime) {
        try {
            Date date = new Date(System.currentTimeMillis() + expireTime);
            Algorithm algorithm = Algorithm.HMAC256(secret);
            // 附带username信息
            return JWT.create()
                    .withClaim("userId", userId)
                    .withExpiresAt(date)
                    .sign(algorithm);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }
}
