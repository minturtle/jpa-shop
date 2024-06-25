package jpabook.jpashop.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;
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

        @Getter
        private final String uid;
        private final String username;
        private final String password;


        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return List.of(new GrantedAuthority() {

                @Override
                public String getAuthority() {
                    return "ROLE_USER";
                }
            });
        }

        @Override
        public String getPassword() {
            return password;
        }

        @Override
        public String getUsername() {
            return username;
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }



    @Getter
    public static class CustomOAuth2User extends DefaultOAuth2User {

        /**
         * Constructs a {@code DefaultOAuth2User} using the provided parameters.
         *
         * @param authorities      the authorities granted to the user
         * @param attributes       the attributes about the user
         * @param nameAttributeKey the key used to access the user's &quot;name&quot; from
         *                         {@link #getAttributes()}
         */
        public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes, String nameAttributeKey, String uid) {
            super(authorities, attributes, nameAttributeKey);
            this.uid = uid;
        }

        private final String uid;

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

