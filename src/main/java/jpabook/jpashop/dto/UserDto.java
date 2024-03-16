package jpabook.jpashop.dto;

import lombok.*;

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



    @Getter
    public static class KakaoUserRegisterInfo extends RegisterInfo {


        @Builder
        public KakaoUserRegisterInfo(
                String name,
                String email,
                String address,
                String detailedAddress,
                String profileImageUrl,
                String kakaoUid
        ) {
            super(name, email, address, detailedAddress, profileImageUrl);
            this.kakaoUid = kakaoUid;
        }

        private String kakaoUid;
    }


    @AllArgsConstructor
    @Builder
    @Data
    public static class Detail{

    }


    public static class Update {
    }

}

