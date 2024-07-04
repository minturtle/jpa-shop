package jpabook.jpashop.security;

import jpabook.jpashop.domain.user.GoogleOAuth2AuthInfo;
import jpabook.jpashop.domain.user.KakaoOAuth2AuthInfo;
import jpabook.jpashop.domain.user.User;
import jpabook.jpashop.dto.UserDto;
import jpabook.jpashop.enums.user.OAuth2RegistrationType;
import jpabook.jpashop.repository.UserRepository;
import jpabook.jpashop.util.NanoIdProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;


@Component
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService{

    private final UserRepository userRepository;
    private final NanoIdProvider nanoIdProvider;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        OAuth2RegistrationType registrationType = OAuth2RegistrationType
                .valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase());

        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        String uid = saveOrUpdate(oAuth2User, registrationType);


        return new UserDto.CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                oAuth2User.getAttributes(),
                userNameAttributeName,
                uid
        );
    }


    /**
     * OAuth2 User의 정보를 최신화해 저장
     * @author minseok kim
     * @param
     * @return
     * @throws
    */
    private String saveOrUpdate(OAuth2User user, OAuth2RegistrationType registrationType){
        switch (registrationType){
            case KAKAO:
                return saveOrUpdateKakao(user);
            case GOOGLE:
                return saveOrUpdateGoogle(user);
        }

        throw new IllegalArgumentException();
    }


    private String saveOrUpdateKakao(OAuth2User user){
        String kakaoUid = user.getName();
        Map<String, Object> accountInfo = (Map)user.getAttribute("kakao_account");
        Map<String, Object> profile = (Map) accountInfo.get("profile");


        String email = (String) accountInfo.get("email");
        String name = (String) profile.get("nickname");
        String profileImage = (String) profile.get("profile_image_url");


        Optional<User> userOptional = userRepository.findByEmail(email);

        if(userOptional.isEmpty()){
            String uid = nanoIdProvider.createNanoId();
            User newUser = User.of(
                    uid,
                    email,
                    name,
                    profileImage,
                    new KakaoOAuth2AuthInfo(kakaoUid)
            );

            userRepository.save(newUser);

            return uid;
        }

        User savedUser = userOptional.get();
        savedUser.setKakaoOAuth2AuthInfo(kakaoUid);
        savedUser.setName(name);
        savedUser.setProfileImageUrl(profileImage);

        return savedUser.getUid();
    }

    private String saveOrUpdateGoogle(OAuth2User user){
        String googleUid = (String) user.getAttributes().get("sub");
        String email = (String) user.getAttributes().get("email");
        String name = (String) user.getAttributes().get("name");


        Optional<User> userOptional = userRepository.findByEmail(email);


        if(userOptional.isEmpty()){
            String uid = nanoIdProvider.createNanoId();
            User newUser = User.of(
                    uid,
                    email,
                    name,
                    null,
                    new GoogleOAuth2AuthInfo(googleUid)
            );

            userRepository.save(newUser);

            return uid;
        }


        User savedUser = userOptional.get();
        savedUser.setKakaoOAuth2AuthInfo(googleUid);
        savedUser.setName(name);

        return savedUser.getUid();
    }

}
