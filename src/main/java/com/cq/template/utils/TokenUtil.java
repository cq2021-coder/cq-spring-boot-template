package com.cq.template.utils;

import cn.hutool.json.JSONUtil;
import com.cq.template.model.dto.user.UserLoginTokenDTO;
import com.cq.template.model.vo.LoginUserVO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 令牌生成与解析工具包
 *
 * @author 程崎
 * @since 2023/10/25
 */
@Slf4j
@Component
public class TokenUtil {


    private static final String TOKEN_PASSWORD = "nckjlsblkjcbsajkdhiaous.template-uhiasbjklcaskljdndjklas-cq2024-iklwuyheoqybjlkalsd";

    private static final String ACCESS_TOKEN_HEADER = "Access-Token";

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    /**
     * 生成token
     *
     * @param loginUserVO 用户
     * @return {@link String}
     */
    public String signToken(LoginUserVO loginUserVO) {
        Map<String, Object> claims = new HashMap<>(4);
        claims.put("user", JSONUtil.toJsonStr(loginUserVO));

        Calendar calendar = Calendar.getInstance();

        Date now = calendar.getTime();

        // 一个月后过期
        calendar.add(GregorianCalendar.DAY_OF_YEAR, 30);
        UserLoginTokenDTO loginTokenDTO = new UserLoginTokenDTO();
        loginTokenDTO.setLoginUserVO(loginUserVO);
        loginTokenDTO.setExpireTime(calendar.getTime());
        String token = Jwts.builder()
                .claims(claims)
                .expiration(calendar.getTime())
                .issuedAt(now)
                .signWith(Keys.hmacShaKeyFor(TOKEN_PASSWORD.getBytes()))
                .compact();
        stringRedisTemplate.opsForValue().set(token, JSONUtil.toJsonStr(loginTokenDTO), 30, TimeUnit.DAYS);
        return token;
    }

    /**
     * 验证token
     *
     * @return boolean
     */
    public boolean verifyToken() {
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        return verifyToken(httpServletRequest.getHeader(ACCESS_TOKEN_HEADER));
    }

    /**
     * 验证token
     *
     * @param token 令牌
     * @return {@link boolean}
     */
    public boolean verifyToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(TOKEN_PASSWORD.getBytes()))
                    .build()
                    .parseSignedClaims(token);
            return isRedisTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 通过请求获取用户信息
     *
     * @return {@link LoginUserVO}
     */
    public LoginUserVO getUserByRequest() {
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        return getUserByToken(httpServletRequest.getHeader(ACCESS_TOKEN_HEADER));
    }

    /**
     * 通过令牌获取用户信息
     *
     * @param token 令牌
     * @return {@link LoginUserVO}
     */
    public LoginUserVO getUserByToken(String token) {
        if (StringUtils.isBlank(token)) {
            return null;
        }
        if (isRedisTokenExpired(token)) {
            return null;
        }
        Claims claims;
        try {
            claims = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(TOKEN_PASSWORD.getBytes()))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
        String user = claims.get("user", String.class);
        return JSONUtil.toBean(user, LoginUserVO.class);
    }

    public boolean removeToken() {
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        String token = httpServletRequest.getHeader(ACCESS_TOKEN_HEADER);
        if (StringUtils.isBlank(token)) {
            return true;
        }
        stringRedisTemplate.delete(token);
        return true;
    }


    private boolean isRedisTokenExpired(String token) {
        String userTokenInfo = stringRedisTemplate.opsForValue().get(token);
        if (StringUtils.isBlank(userTokenInfo)) {
            return true;
        }
        UserLoginTokenDTO userLoginTokenDTO = JSONUtil.toBean(userTokenInfo, UserLoginTokenDTO.class);
        return userLoginTokenDTO.getExpireTime().before(new Date());
    }

}
