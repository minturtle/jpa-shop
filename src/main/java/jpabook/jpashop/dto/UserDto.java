package jpabook.jpashop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public abstract class UserDto {


    @AllArgsConstructor
    @Data
    public abstract static class RegisterInfo{
        private String name;
        private String email;
        private String address;
        private String detailedAddress;
        private String profileImageUrl;
    }

    public static class UsernamePasswordUserRegisterInfo extends RegisterInfo{

        @Builder
        public UsernamePasswordUserRegisterInfo(
                String name,
                String email,
                String address,
                String detailedAddress,
                String profileImageUrl,
                String username,
                String password
        ) {
            super(name, email, address, detailedAddress, profileImageUrl);
            this.username = username;
            this.password = password;
        }

        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }




    @AllArgsConstructor
    @Builder
    @Data
    public static class Detail{

    }


}

