package hackathon.spring.global;

import hackathon.spring.apiPayload.code.status.ErrorStatus;
import hackathon.spring.apiPayload.exception.GeneralException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        // 토큰 없이 접근을 허용할 경로
        if (requestURI.contains("/swagger-ui") || requestURI.contains("/v3/api-docs")
                || requestURI.equals("/auth/join") || requestURI.equals("/auth/login")) {
            chain.doFilter(request, response);
            return;
        }

        String token = jwtTokenProvider.resolveToken(request);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            if (jwtTokenProvider.isBlacklisted(token)) {
                System.out.println("🚨 블랙리스트 토큰 감지: " + token);
                throw new GeneralException(ErrorStatus._LOGOUT_TOKEN);
            }

            Authentication auth = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
            System.out.println("✅ 인증 완료: " + auth.getName());
        }
        else {
            System.out.println("🚨 유효하지 않은 토큰으로 접근 시도: " + token);
            chain.doFilter(request, response); // 🚀 예외 발생 없이 요청 진행
            return;
        }

        chain.doFilter(request, response);
    }
}
