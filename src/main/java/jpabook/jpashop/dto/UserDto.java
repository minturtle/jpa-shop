package jpabook.jpashop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public abstract class UserDto {


    @AllArgsConstructor
    @Builder
    @Data
    public abstract static class RegisterInfo{
        private String name;
        private String email;
        private String address;
        private String detailedAddress;
        private String profileImageUrl;
    }

    @Builder
    @Data
    public static class UsernamePasswordUserRegisterInfo extends RegisterInfo{
        private String username;
        private String password;
    }




    @AllArgsConstructor
    @Builder
    @Data
    public static class Detail{

    }


}

