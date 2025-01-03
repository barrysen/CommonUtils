package org.barrysen.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.barrysen.constant.CommonConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/**
 * 功能：外部应用认证  JWT 工具类，用于生成和解析 Token。
 *
 * @author: Barrysen
 * @date: 2025/1/3 13:40
 */
@Component
@Slf4j
public class JwtOutsideUtil {

    @Autowired
    private RedisUtil redisUtilInstance;

    private static RedisUtil redisUtil; // 静态字段供静态方法使用

    /**
     * 初始化静态依赖
     */
    @PostConstruct
    public void init() {
        redisUtil = redisUtilInstance; // 将非静态实例赋值给静态字段
    }

    // 密钥（可以换成更复杂的字符串）
//    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    private static final String SECRET = "prefix-gyl-cyl-outside-app-secret";
    private static final Key key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    // Token 有效期（毫秒）
    private static final long EXPIRATION_TIME = 30 * 60 * 1000; // 30分钟

    // 生成 Token
    public static String generateToken(String appId, String appSecret) {
        String token = Jwts.builder()
                .setSubject(appId) // 保存主题，可以是用户名、用户ID等
                .claim("appSecret", appSecret) // 将 appSecret 存入 Claims
                .setIssuedAt(new Date()) // 签发时间
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 过期时间
                .signWith(key) // 签名算法
                .compact();
        if (!ObjectConvertUtils.isEmpty(token)) {
            String redisTokenKey = CommonConstant.PREFIX_OUTSIDE_APP_TOKEN + token;
            // 存入 Redis，并设置过期时间
            redisUtil.set(redisTokenKey, token);
            redisUtil.expire(redisTokenKey, EXPIRATION_TIME / 1000);
        }
        return token;
    }


    // 解析 Token
    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key) // 验证签名
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 判断 Token 是否有效
    public static boolean isTokenExpired(String token) {
        // 从 Redis 检查 Token 是否存在
        String redisToken = (String) redisUtil.get(CommonConstant.PREFIX_OUTSIDE_APP_TOKEN + token);
        if (redisToken == null) {
            log.error("Token not found in Redis");
            return true; // Redis 中不存在，判定为无效
        }
        return false;
        // 如果 Redis 中存在，解析 Token 并验证其过期时间
//        try {
//            Claims claims = parseToken(token);
//            // 打印 Token 解析结果（调试用）
//            log.info("Token subject: {}", claims.getSubject());
//            log.info("Token expiration: {}", claims.getExpiration());
//            // 检查 Token 是否过期
//            return claims.getExpiration().before(new Date());
//        } catch (ExpiredJwtException e) {
//            log.error("Token has expired: {}", e.getMessage());
//            return true; // 如果过期，判定为无效
//        } catch (JwtException e) {
//            log.error("Token parsing failed: {}", e.getMessage());
//            return true; // 解析失败，判定为无效
//        }
    }

    /**
     * 功能：检查是否需要续期
     *
     * @param token Token
     * @return: boolean
     * @author: Barry
     * @date: 2024/11/27 09:20
     */
    public static boolean shouldRenewToken(String token) {
//        Date expirationDate = getExpirationDateFromToken(token); // 获取过期时间
//        long remainingTime = expirationDate.getTime() - System.currentTimeMillis(); // 剩余时间
//        long threshold = 29 * 60 * 1000; // 设置阈值29分钟
//        return remainingTime <= threshold; // 如果剩余时间小于阈值，返回 true
        // 从 Redis 检查 Token 是否存在
        String redisKey = CommonConstant.PREFIX_OUTSIDE_APP_TOKEN + token;
        Long remainingTTL = redisUtil.getExpire(redisKey); // 获取剩余过期时间（单位：秒）
        if (remainingTTL <= 0) {
            log.error("Token not found or expired in Redis");
            return false; // Redis 中不存在或已过期
        }
        // 设置续期阈值（例如 5 分钟，即 300 秒）
        long threshold = 29 * 60; // 5 分钟
        return remainingTTL <= threshold; // 如果剩余时间小于阈值，返回 true
    }

    /**
     * 功能：续期Token
     *
     * @param token Token
     * @return: java.lang.String
     * @author: Barry
     * @date: 2024/11/27 09:20
     */
    public static void renewToken(String token) {
        // 检查 Redis 中是否存在 Token
        String redisToken = (String) redisUtil.get(CommonConstant.PREFIX_OUTSIDE_APP_TOKEN + token);
        if (redisToken == null) {
            log.error("Token not found in Redis");
            throw new RuntimeException("Token not found, cannot renew");
        }
        // 重置 Token 的 TTL 为 30 分钟
        redisUtil.expire(CommonConstant.PREFIX_OUTSIDE_APP_TOKEN + token, EXPIRATION_TIME / 1000);
        log.info("Token TTL has been reset to {} seconds", EXPIRATION_TIME / 1000);
    }

    // 获取 Token 的过期时间
    public static Date getExpirationDateFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token); // 获取 Token 中的所有 Claims
        return claims.getExpiration(); // 返回过期时间
    }

    // 获取 appid
    public static String getAppidFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.get("appid", String.class); // 假设 Token 中的 appid 存在于 Payload 的 "appid" 字段
    }

    // 获取 appSecret
    public static String getAppSecretFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.get("appSecret", String.class); // 假设 Token 中的 appSecret 存在于 Payload 的 "appSecret" 字段
    }

    private static Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();
    }
}
