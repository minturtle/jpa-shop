package jpabook.jpashop.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import jpabook.jpashop.controller.common.request.UserRequest;
import jpabook.jpashop.controller.common.response.ErrorResponse;
import jpabook.jpashop.controller.common.response.UserResponse;
import jpabook.jpashop.domain.user.User;
import jpabook.jpashop.exception.user.UserExceptonMessages;
import jpabook.jpashop.testUtils.TestDataUtils;
import jpabook.jpashop.util.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static jpabook.jpashop.testUtils.TestDataUtils.user1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(value = "classpath:init-user-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class AuthenticationIntegrationTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;


    @Test
    @DisplayName("엑세스토큰 없이 인증이 필요한 API에 접근할 시 401 UnAuthorized를 반환한다.")
    public void given_NoAccessToken_when_RequestNeedAuthAPI_then_Return401() throws Exception{
        //given

        //when
        MvcResult mvcResult = mockMvc.perform(get("/api/health-check"))
                .andDo(print())
                .andReturn();

        //then
        ErrorResponse responseBody = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorResponse.class);

        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(401);
        assertThat(responseBody.getMessage()).isEqualTo(UserExceptonMessages.AUTHENTICATION_FAILED.getMessage());

    }

    @Test
    @DisplayName("엑세스토큰이 존재하는 경우 API에 접근할 시 200 OK를 반환한다.")
    public void given_validAccessToken_when_RequestNeedAuthAPI_then_Return200() throws Exception{
        //given
        User givenUser = TestDataUtils.user1;

        String givenAccessToken = jwtTokenProvider.sign(givenUser.getUid(), new Date());

        //when
        MvcResult mvcResult = mockMvc.perform(get("/api/health-check")
                .header("Authorization", "Bearer " + givenAccessToken))
                .andDo(print())
                .andReturn();
        //then
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);

    }

    @Test
    @DisplayName("액세스 토큰이 만료된 경우 401 UnAuthorized를 반환한다.")
    public void given_ExpiredAccessToken_when_RequestNeedAuthAPI_then_Return401() throws Exception{
        //given
        User givenUser = TestDataUtils.user1;

        String givenAccessToken = jwtTokenProvider.sign(givenUser.getUid(), new Date(1L));
        //when
        MvcResult mvcResult = mockMvc.perform(get("/api/health-check")
                .header("Authorization", "Bearer " + givenAccessToken))
                .andDo(print())
                .andReturn();
        //then
        ErrorResponse responseBody = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorResponse.class);

        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(401);
        assertThat(responseBody.getMessage()).isEqualTo(UserExceptonMessages.EXPIRED_TOKEN.getMessage());
    }

    @Test
    @DisplayName("액세스 토큰이 유효하지 않은 경우 401 UnAuthorized를 반환한다.")
    public void given_InvalidAccessToken_when_RequestNeedAuthAPI_then_Return401() throws Exception{
        //given

        //when
        MvcResult mvcResult = mockMvc.perform(get("/api/health-check")
                        .header("Authorization", "Bearer " + "invalidToken"))
                .andDo(print())
                .andReturn();


        //then
        ErrorResponse responseBody = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorResponse.class);

        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(401);
        assertThat(responseBody.getMessage()).isEqualTo(UserExceptonMessages.INVALID_TOKEN.getMessage());
    }

    @Test
    @DisplayName("이미 가입되어 있는 username/password로 로그인을 수행하여 결과값인 uid와 access token을 받을 수 있다.")
    public void given_UsernameAuthTypeUser_when_LoginUsernamePassword_then_Success() throws Exception{
        //given
        User givenUser = user1;
        String givenPassword = "abc1234!";

        UserRequest.Login loginForm = new UserRequest.Login(givenUser.getUsernamePasswordAuthInfo().getUsername(), givenPassword);

        String loginFormString = objectMapper.writeValueAsString(loginForm);
        //when
        MvcResult mvcResponse = mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginFormString)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andDo(print()).andExpect(status().isOk())
                .andReturn();
        //then
        UserResponse.Login result = objectMapper.readValue(mvcResponse.getResponse().getContentAsString(), UserResponse.Login.class);

        assertAll("결과값엔 유효한 uid와 access token이 존재해야 한다.",
                ()->assertThat(result.getUid()).isEqualTo(givenUser.getUid()),
                ()->assertThat(isJwtToken(result.getAccessToken())).isTrue());
    }


    @Test
    @DisplayName("잘못된 username으로 로그인을 시도할 경우 401 UnAuthorized를 반환한다.")
    public void given_invalidUsername_when_login_then_return401() throws Exception{
        //given

        UserRequest.Login loginForm = new UserRequest.Login("invalidUsername", "abc1234!");

        String loginFormString = objectMapper.writeValueAsString(loginForm);
        //when
        MvcResult mvcResponse = mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginFormString)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andDo(print())
                .andReturn();
        //then
        ErrorResponse result = objectMapper.readValue(mvcResponse.getResponse().getContentAsString(), ErrorResponse.class);

        assertThat(mvcResponse.getResponse().getStatus()).isEqualTo(401);
        assertThat(result.getMessage()).isEqualTo(UserExceptonMessages.LOGIN_FAILED.getMessage());
    }

    @Test
    @DisplayName("잘못된 password로 로그인을 시도할 경우 401 UnAuthorized를 반환한다.")
    public void given_invalidPassword_when_login_then_return401() throws Exception{
        //given
        User givenUser = user1;

        UserRequest.Login loginForm = new UserRequest.Login(givenUser.getUsernamePasswordAuthInfo().getUsername(), "invalidPassword");

        String loginFormString = objectMapper.writeValueAsString(loginForm);
        //when
        MvcResult mvcResponse = mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginFormString)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andDo(print())
                .andReturn();
        //then
        ErrorResponse result = objectMapper.readValue(mvcResponse.getResponse().getContentAsString(), ErrorResponse.class);

        assertThat(mvcResponse.getResponse().getStatus()).isEqualTo(401);
        assertThat(result.getMessage()).isEqualTo(UserExceptonMessages.LOGIN_FAILED.getMessage());

    }



    /**
     * @author minseok kim
     * @description 해당 문자열이 JWT 토큰의 형태를 갖고있는지 확인하는 메서드
     */
    public boolean isJwtToken(String token) {
        String regex = "^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.?[A-Za-z0-9-_.+/=]*$";
        return token.matches(regex);
    }

}
