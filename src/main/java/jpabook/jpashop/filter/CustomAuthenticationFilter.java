package jpabook.jpashop.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jpabook.jpashop.controller.common.request.UserRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;


@Component
public class CustomAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final ObjectMapper objectMapper;

    private static final String LOGIN_URI = "/api/user/login";

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager, ObjectMapper objectMapper) {
        super(LOGIN_URI, authenticationManager);
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if(!request.getMethod().equals("POST")){
            throw new AuthenticationException("로그인은 'POST' 메서드로 수행되어야 합니다.") {};
        }

        UserRequest.Login loginRequestBody = getLoginRequestBody(request);

        UsernamePasswordAuthenticationToken authRequest = UsernamePasswordAuthenticationToken.unauthenticated(
                loginRequestBody.getUserId(),
                loginRequestBody.getPassword()
        );

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    private UserRequest.Login getLoginRequestBody(HttpServletRequest request) throws AuthenticationException {
        StringBuilder json = new StringBuilder();
        String line;
        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }

            return objectMapper.readValue(json.toString(), UserRequest.Login.class);
        }catch (Exception e){
            throw new AuthenticationException("로그인 요청 값을 읽어들이는데 실패했습니다.") {};
        }
    }
}
