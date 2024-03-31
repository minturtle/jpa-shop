package jpabook.jpashop.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public abstract class UserResponse {



    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Detail{
        private String name;
        private String address;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @Schema(name = "UsernamePasswordLoginResult")
    public static class Login {
        private String uid;
        private String accessToken;
    }
}
