package com.dtest.drools.global.security.jwt;

import com.dtest.drools.global.apipayload.code.status.ErrorStatus;
import com.dtest.drools.global.apipayload.exception.GeneralException;
import com.dtest.drools.global.redis.RedisClient;
import com.dtest.drools.user.User;
import com.dtest.drools.user.dto.response.TokenResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.util.Date;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private Key key;
    private final RedisClient redisClient;

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.token.access-expiration-time}")
    private long accessTokenExpirationTime;

    @Value("${jwt.token.refresh-expiration-time}")
    private long refreshTokenExpirationTime;

    @PostConstruct //Bean이 생성된 후 자동으로 실행됨
    protected void init() {
        byte[] secretKeyBytes = Decoders.BASE64.decode(secretKey);
        key = Keys.hmacShaKeyFor(secretKeyBytes);
    }

    public String createAccessToken(User user) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getUserRole().name())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTokenExpirationTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(User user) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getUserRole().name())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenExpirationTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // jwt 토큰 발급
    public TokenResponse createToken(User user) {
        return TokenResponse.builder()
                .accessToken(createAccessToken(user))
                .refreshToken(createRefreshToken(user))
                .build();
    }

    // jwt 토큰 재발급
    public TokenResponse recreate(User user, String refreshToken) {
        String accessToken = createAccessToken(user);

        // 기존 리프레시 토큰의 만료 시간이 엑세스 토큰보다 적으면 새 리프레시 토큰 발급
        if (getExpirationTime(refreshToken) <= getExpirationTime(accessToken)) {
            refreshToken = createRefreshToken(user);
        }
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // jwt 검증
    public boolean validateToken(String token, String tokenType) {
        try {
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            if (Objects.equals(tokenType, "refresh") && redisClient.checkExistsValue(token)) {
                return false; // 리프레시 토큰이 redis에 존재하는 경우(블랙리스트에 있음) 무효 처리
            }
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false; // 예외 발생 시에는 무효 토큰 처리
        }
    }

    public String getEmail(String token) {
        return Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }

    public Long getExpirationTime(String token) {
        return Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody().getExpiration().getTime();
    }

    // 토큰 추출
    public String resolveAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public String resolveRefreshToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // jwt 폐기
    @Transactional
    public void invalidateTokens(String refreshToken, String accessToken) {
        if (!validateToken(refreshToken, "refresh")) {
            throw new GeneralException(ErrorStatus.TOKEN_INVALID);
        }
        redisClient.deleteValue(getEmail(refreshToken));
        redisClient.setValue(accessToken, "logout", getExpirationTime(accessToken));
    }
}
