package jpabook.jpashop.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jpabook.jpashop.controller.common.request.UserRequest;
import jpabook.jpashop.controller.common.response.UserResponse;
import jpabook.jpashop.dto.UserDto;
import jpabook.jpashop.util.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;


@Component
public class CustomAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final ObjectMapper objectMapper;

    private final JwtTokenProvider jwtTokenProvider;
    private static final String LOGIN_URI = "/api/user/login";

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager, ObjectMapper objectMapper, JwtTokenProvider jwtTokenProvider) {
        super(LOGIN_URI, authenticationManager);
        this.objectMapper = objectMapper;
        this.jwtTokenProvider = jwtTokenProvider;
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

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        UserDto.CustomUserDetails principal = (UserDto.CustomUserDetails) authResult.getPrincipal();

        String token = jwtTokenProvider.sign(principal.getUid(), new Date());

        UserResponse.Login responseBody = new UserResponse.Login(principal.getUid(), token);

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(200);
        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
    }

    private UserRequest.Login getLoginRequestBody(HttpServletRequest request) throws AuthenticationException {
        StringBuilder json = new StringBuilder();
        String line;
        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }

            return objectMapper.readValue(json.toString(), UserRequest.Login.class);
        }catch (IOException e){
            throw new AuthenticationException("로그인 요청 값을 읽어들이는데 실패했습니다.") {};
        }
    }
}
