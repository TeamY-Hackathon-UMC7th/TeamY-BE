package hackathon.spring;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.util.Date;

public class JwtTokenProvider {

    private static final String SECRET_KEY = "${SECRET_KEY}";
    private static final long EXPIRATION_TIME = 604800000; //일주일

    public static String generateToken(String nickname) {
        return JWT.create()
                .withSubject(nickname)
//                .withClaim("memberId", memberId)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(SECRET_KEY));
    }
}
