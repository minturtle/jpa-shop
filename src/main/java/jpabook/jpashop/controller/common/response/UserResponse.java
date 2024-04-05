package jpabook.jpashop.controller.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jpabook.jpashop.domain.user.AddressInfo;
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
        private String uid;
        private String name;
        private AddressInfo addressInfo;
        private String email;
        private String profileImageUrl;
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
