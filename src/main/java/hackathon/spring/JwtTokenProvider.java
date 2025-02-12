package hackathon.spring;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Getter
public class JwtTokenProvider {

    private final String SECRET_KEY = "${SECRET_KEY}";

    @Value("${access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${refresh-token-expiration}")
    private long refreshTokenExpiration;

    public String generateAccessToken(String nickname) {
        return JWT.create()
                .withSubject(nickname)
                .withExpiresAt(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .sign(Algorithm.HMAC512(SECRET_KEY));
    }

    public String generateRefreshToken(String nickname) {
        return JWT.create()
                .withSubject(nickname)
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

    public String getNicknameFromToken(String token) {
        return JWT.require(Algorithm.HMAC512(SECRET_KEY)).build().verify(token).getSubject();
    }
}