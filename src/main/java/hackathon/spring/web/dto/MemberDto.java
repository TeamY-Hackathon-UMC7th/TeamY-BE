package hackathon.spring.web.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


public class MemberDto {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JoinRequestDto {

        @NotNull
        @NotBlank
        @Size(message = "이메일은 50자 이하로 적어주세요", max=50)
        private String email;
        private String password;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JoinResultDto {

        private Long Id;
        private String email;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequestDto {

        private String email;
        private String password;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginResultDto {

        private Long Id;
        private String email;
        private String accessToken;
        private String refreshToken;
        private long accessTokenExpiresIn;
        private long refreshTokenExpiresIn;
    }
}
