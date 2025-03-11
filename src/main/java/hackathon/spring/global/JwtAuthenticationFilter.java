package hackathon.spring.global;

import com.fasterxml.jackson.databind.ObjectMapper;
import hackathon.spring.apiPayload.ApiResponse;
import hackathon.spring.apiPayload.code.status.ErrorStatus;
import hackathon.spring.apiPayload.exception.GeneralException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;


@Slf4j
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
                || requestURI.equals("/auth/join") || requestURI.equals("/auth/login/kakao")
                || requestURI.equals("/auth/email") ) {
            chain.doFilter(request, response);
            return;
        }

        try {
            String token = jwtTokenProvider.resolveToken(request);

            if (token != null && jwtTokenProvider.validateToken(token)) {
                System.out.println("토큰이 validate되었습니다!");
                if (jwtTokenProvider.isBlacklisted(token)) {
                    log.error("❌ [JwtAuthenticationFilter] 블랙리스트 요청 → 401 반환");
                    handleAuthenticationError(response, "접근할 수 없는 사용자입니다.");
                    return;
                }
                Authentication auth = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
                System.out.println("✅ 인증 완료: " + auth.getName());
            } else {
                System.out.println("🚨 유효하지 않은 토큰으로 접근 시도: " + token);
                handleAuthenticationError(response, "로그인이 필요한 서비스입니다.");
                return;
            }

            chain.doFilter(request, response);
        } catch (Exception e) {
            System.out.println("🚨 필터 예외 발생: " + e.getMessage());
            throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR);
        }
    }

    private void handleAuthenticationError(HttpServletResponse response, String message) throws IOException {
        log.error("🚨 [JwtAuthenticationFilter] 인증 실패: {}", message);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(
                new ObjectMapper().writeValueAsString(
                        ApiResponse.onFailure("_NOT_LOGIN_USER", message, null)
                )
        );
    }
}
