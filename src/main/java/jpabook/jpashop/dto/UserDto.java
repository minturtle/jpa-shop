package jpabook.jpashop.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public abstract class UserDto {


    @AllArgsConstructor
    @Data
    @NoArgsConstructor
    public abstract static class RegisterInfo{
        private String name;
        private String email;
        private String address;
        private String detailedAddress;
        private String profileImageUrl;
    }

    @Getter
    @Setter
    @NoArgsConstructor
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



    @Getter
    public static class GoogleUserRegisterInfo extends RegisterInfo{

        @Builder
        public GoogleUserRegisterInfo(
                String name,
                String email,
                String address,
                String detailedAddress,
                String profileImageUrl,
                String googleUid
        ) {
            super(name, email, address, detailedAddress, profileImageUrl);
            this.googleUid = googleUid;
        }

        private String googleUid;
    }


    @AllArgsConstructor
    @Builder
    @Data
    public static class Detail{
        private String userUid;
        private String name;
        private String email;
        private String address;
        private String detailedAddress;
        private String profileImage;
    }


    @AllArgsConstructor
    public static class CustomUserDetails implements UserDetails{

        private final String username;
        private final String password;
        private final String salt;

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return List.of(new GrantedAuthority() {

                @Override
                public String getAuthority() {
                    return "ROLE_USER";   // ROLE_USER
                }
            });
        }

        @Override
        public String getPassword() {
            return String.format("%s:%s", password, salt);
        }

        @Override
        public String getUsername() {
            return username;
        }

        @Override
        public boolean isAccountNonExpired() {
            return false;
        }

        @Override
        public boolean isAccountNonLocked() {
            return false;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return false;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }


    @AllArgsConstructor
    @Builder
    @Data
    public static class UpdateDefaultUserInfo {


        private Optional<String> updatedName;

        private Optional<String> updatedAddress;

        private Optional<String> updatedDetailAddress;

        private Optional<String> updatedProfileImageUrl;

    }

    @AllArgsConstructor
    @Builder
    @Data
    public static class UpdatePassword {
        @NotNull
        private String beforePassword;

        @NotNull
        private String afterPassword;
    }

    @AllArgsConstructor
    @Getter
    public static class OAuthLoginResult {
        private String uid;
        private boolean isAdditionalInfoNeed;

    }

}

