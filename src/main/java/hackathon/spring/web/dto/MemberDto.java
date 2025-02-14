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

        @NotNull
        @NotBlank
        @Size(message = "비밀번호는 8~20자로 설정해주세요", max=20)
        private String password;

        @NotNull
        @NotBlank
        @Size(message = "비밀번호는 8~20자로 설정해주세요", max=20)
        private String checkPassword;

    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JoinResultDto {

        private Long id;
        private String email;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequestDto {

        @NotNull
        @NotBlank
        private String email;

        @NotNull
        @NotBlank
        private String password;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginResultDto {

        private Long id;
        private String email;
        private String accessToken;
        private String refreshToken;
        private long accessTokenExpiresIn;
        private long refreshTokenExpiresIn;
    }
}
