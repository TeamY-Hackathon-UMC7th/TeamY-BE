package hackathon.spring.global;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;

@Slf4j
@Component
@Getter
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final StringRedisTemplate redisTemplate;

    @Value("${custom.jwt.secretKey}")  // application.yml에서 값 주입
    private String SECRET_KEY;

    @Value("${custom.jwt.access-token-validity-in-seconds}")
    private long accessTokenExpiration;

    @Value("${custom.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    public String generateAccessToken(String email) {
        return JWT.create()
                .withSubject(email)
                .withExpiresAt(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .sign(Algorithm.HMAC512(SECRET_KEY));
    }

    public String generateRefreshToken(String email) {
        return JWT.create()
                .withSubject(email)
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .sign(Algorithm.HMAC512(SECRET_KEY));
    }

    public boolean validateToken(String token) {
        try {
            JWT.require(Algorithm.HMAC512(SECRET_KEY)).build().verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getEmailFromToken(String token) {
        return JWT.require(Algorithm.HMAC512(SECRET_KEY)).build().verify(token).getSubject();
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public void addToBlacklist(String token, long expirationInSeconds) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set(token, "blacklisted", Duration.ofSeconds(expirationInSeconds));
    }

    public boolean isBlacklisted(String token) {
        return redisTemplate.hasKey(token);
    }

    public long getExpiration(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        Date expiration = decodedJWT.getExpiresAt();

        return (expiration.getTime() - System.currentTimeMillis()) / 1000; // 초 단위 반환
    }

    public Authentication getAuthentication(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        String email = decodedJWT.getSubject();

        User principal = new User(email, "", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        return new UsernamePasswordAuthenticationToken(principal, token, principal.getAuthorities());
    }
}