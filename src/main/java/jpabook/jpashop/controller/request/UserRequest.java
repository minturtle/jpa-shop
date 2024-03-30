package jpabook.jpashop.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



public abstract class UserRequest {



    @NoArgsConstructor
    @Getter
    @Setter
    @Schema(name = "UsernamePasswordRegisterForm")
    @AllArgsConstructor
    public static class Create{
        private String name;
        private String email;
        private String address;
        private String detailedAddress;
        private String profileImageUrl;
        private String username;
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
    public class Login {
        private String userId;
        private String password;

    }
}



