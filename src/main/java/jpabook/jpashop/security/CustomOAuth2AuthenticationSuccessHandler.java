package jpabook.jpashop.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jpabook.jpashop.controller.common.response.UserResponse;
import jpabook.jpashop.dto.UserDto;
import jpabook.jpashop.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class CustomOAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        UserDto.CustomOAuth2User principal = (UserDto.CustomOAuth2User) authentication.getPrincipal();

        String token = jwtTokenProvider.sign(principal.getUid(), new Date());

        UserResponse.Login responseBody = new UserResponse.Login(principal.getUid(), token);

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(200);
        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
    }

}
