package jpabook.jpashop.testUtils;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
@ConfigurationProperties(prefix = "spring.security.oauth2.client.registration.google")
@Getter
@Setter
public class TestGoogleProperties {

    public static final String GOOGLE_AUTH_CODE = "google_auth_code";

    public static final String GOOGLE_ACCESS_TOKEN = "google_access_token";

    public static final String GOOGLE_REFRESH_TOKEN = "google_refresh_token";

    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private List<String> scope;

}
