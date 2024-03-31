package jpabook.jpashop.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;


public abstract class UserRequest {



    @NoArgsConstructor
    @Getter
    @Setter
    @Schema(name = "UsernamePasswordRegisterForm")
    @AllArgsConstructor
    public static class Create{
        private String name;

        @Email
        private String email;

        private String address;
        private String detailedAddress;

        @URL
        private String profileImageUrl;

        @Length(min = 7, max = 20)
        @NotBlank
        private String username;

        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$")
        @NotBlank
        private String password;
    }


    @NoArgsConstructor
    @Getter
    @Setter
    public static class Update {
        private String name;
        private String password;
    }



    @NoArgsConstructor
    @Getter
    @Setter
    @AllArgsConstructor
    @Schema(name = "UsernamePasswordLoginForm")
    public static class Login {
        private String userId;
        private String password;

    }
}



