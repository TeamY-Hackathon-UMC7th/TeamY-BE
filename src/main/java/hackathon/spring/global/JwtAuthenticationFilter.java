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
        if (requestURI.startsWith("/swagger-ui") || requestURI.startsWith("/v3/api-docs")
                || requestURI.equals("/auth/join") || requestURI.equals("/auth/login")) {
            chain.doFilter(request, response);
            return;
        }

        String token = jwtTokenProvider.resolveToken(request);

        if (token != null && jwtTokenProvider.validateToken(token)) {

            if (jwtTokenProvider.isBlacklisted(token)) {
                throw new GeneralException(ErrorStatus._LOGOUT_TOKEN);
            }

            Authentication auth = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolderStrategy strategy = SecurityContextHolder.getContextHolderStrategy();
            SecurityContextHolder.getContext().setAuthentication(auth);
            strategy.getContext().setAuthentication(auth);
        }
        else{
            throw new GeneralException(ErrorStatus._INVALID_TOKEN);
        }

        chain.doFilter(request, response);
    }
}
