package com.rgzn.ttd.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class JwtUtil {

    //签名密钥
    private static final String SECRET = "rgznsecretkeysadawedw23243214123124124142143124124124312398122141";
    /**
     * 生成JWT
     * @param userId
     * @return
     */
    public static String generateJwt(String userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        return Jwts.builder()
                .setClaims(claims)
                //System.currentTimeMillis() + 3600 * 1000 = 1小时
                .setExpiration(new Date(System.currentTimeMillis() + 3600 * 1000 * 24 * 365))
                .signWith(SignatureAlgorithm.HS256,SECRET)//使用HS256算法和密钥进行签名
                .compact(); //生成紧凑的JWT字符串
    }

    public static Claims verifyJwt(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET)//设置签名密钥
                .parseClaimsJws(token)//解析JWT字符串
                .getBody();// 获取载荷部分
    }

    public static void main(String[] args) {
        String jwt = generateJwt("lvguangyan");
        System.out.println("jwt:"+jwt);
        jwt = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiJsdmd1YW5neWFuIiwiZXhwIjoxNzMzMzY5MzMxfQ.T3X8ncv6XNgQobsPu6XumBEFIsKeWaiWuMyjlwrTwfA";
        Claims claims = verifyJwt(jwt);
        System.out.println("claims:"+claims);
        System.out.println("userId:"+claims.get("userId",String.class));
    }
}
