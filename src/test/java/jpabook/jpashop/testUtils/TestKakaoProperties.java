package jpabook.jpashop.testUtils;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "spring.security.oauth2.client.registration.kakao")
@Getter
@Setter
public class TestKakaoProperties {
    public static final String KAKAO_AUTH_CODE = "kakao_auth_code";
    public static final String KAKAO_ACCESS_TOKEN = "kakao_access_token";
    public static final String KAKAO_REFRESH_TOKEN = "kakao_refresh_token";
    private String clientId;
    private String clientSecret;
    private String clientAuthenticationMethod;
    private String redirectUri;
    private String authorizationGrantType;
    private String clientName;
    private List<String> scope;

}
