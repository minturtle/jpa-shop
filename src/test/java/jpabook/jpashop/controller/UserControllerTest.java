package jpabook.jpashop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jpabook.jpashop.controller.request.UserRequest;
import jpabook.jpashop.controller.response.ErrorResponse;
import jpabook.jpashop.controller.response.UserResponse;
import jpabook.jpashop.domain.user.AddressInfo;
import jpabook.jpashop.domain.user.User;
import jpabook.jpashop.exception.user.UserExceptonMessages;
import jpabook.jpashop.repository.UserRepository;
import jpabook.jpashop.util.PasswordUtils;
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

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(value = "classpath:init-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordUtils passwordUtils;



    @Test
    @DisplayName("Username/Password로 회원가입을 수행해 DB에 저장할 수 있다.")
    public void testRegisterUsernamePasswordUser() throws Exception{
        //given
        UserRequest.Create createForm = new UserRequest.Create(
                "name",
                "email@email.com",
                "address",
                "detailedAddress",
                "http://example.com/image.png",
                "username",
                "abc1234!"
        );
        String createFormString = objectMapper.writeValueAsString(createForm);

        //when
        mockMvc.perform(post("/api/user/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createFormString))
                .andDo(print()).andExpect(status().isOk());
        //then
        User user = userRepository.findByUsername("username")
                .orElseThrow(RuntimeException::new);

        assertAll("유저의 기본 정보를 모두 담고 있어야 한다.",
                ()->assertThat(user).extracting("email", "name", "profileImageUrl", "addressInfo")
                        .contains("email@email.com", "name", "http://example.com/image.png", new AddressInfo("address", "detailedAddress")),
                ()->assertThat(user.getUid()).isNotNull());

        boolean isPasswordMatches = passwordUtils.matches("abc1234!", user.getUsernamePasswordAuthInfo().getSaltBytes(), user.getUsernamePasswordAuthInfo().getPassword());

        assertAll("유저의 인증정보가 저장되어 후에 인증이 수행가능해야 한다.",
                ()->assertThat(user.getUsernamePasswordAuthInfo().getUsername()).isEqualTo("username"),
                ()->assertThat(isPasswordMatches).isTrue());

    }

    @Test
    @DisplayName("Username/Password로 회원가입을 수행할 때 비밀번호가 조건에 만족하지 못한다면 400 코드를 반환하며 회원가입에 실패한다.")
    public void testRegisterInvalidPassword() throws Exception{
        //given
        UserRequest.Create createForm = new UserRequest.Create(
                "name",
                "email@email.com",
                "address",
                "detailedAddress",
                "http://example.com/image.png",
                "username",
                "1234"
        );
        String createFormString = objectMapper.writeValueAsString(createForm);
        //when
        MvcResult mvcResponse = mockMvc.perform(post("/api/user/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createFormString)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andDo(print()).andExpect(status().isBadRequest())
                .andReturn();
        //then
        ErrorResponse result = objectMapper.readValue(mvcResponse.getResponse().getContentAsString(), ErrorResponse.class);

        assertThat(result.getMessage()).isEqualTo(UserExceptonMessages.INVALID_PASSWORD_EXPRESSION.getMessage());


    }

    @Test
    @DisplayName("이미 가입되어 있는 username이라면 400오류를 throw하며 회원가입에 실패한다.")
    public void testDuplicateUsername() throws Exception{
        //given
        UserRequest.Create createForm = new UserRequest.Create(
                "name",
                "email@email.com",
                "address",
                "detailedAddress",
                "http://example.com/image.png",
                "honggildong",
                "1234"
        );
        String createFormString = objectMapper.writeValueAsString(createForm);
        //when
        MvcResult mvcResponse = mockMvc.perform(post("/api/user/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createFormString)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andDo(print()).andExpect(status().isBadRequest())
                .andReturn();
        //then
        ErrorResponse result = objectMapper.readValue(mvcResponse.getResponse().getContentAsString(), ErrorResponse.class);
        assertThat(result.getMessage()).isEqualTo(UserExceptonMessages.ALREADY_EXISTS_USERNAME.getMessage());
    }

    @Test
    @DisplayName("이미 가입되어 있는 email이라면 400오류를 throw하며 회원가입에 실패한다.")
    public void testDuplicatedEmail() throws Exception{
        //given
        UserRequest.Create createForm = new UserRequest.Create(
                "name",
                "user@example.com",
                "address",
                "detailedAddress",
                "http://example.com/image.png",
                "username",
                "1234"
        );
        String createFormString = objectMapper.writeValueAsString(createForm);
        //when
        MvcResult mvcResponse = mockMvc.perform(post("/api/user/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createFormString)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andDo(print()).andExpect(status().isBadRequest())
                .andReturn();
        //then
        ErrorResponse result = objectMapper.readValue(mvcResponse.getResponse().getContentAsString(), ErrorResponse.class);
        assertThat(result.getMessage()).isEqualTo(UserExceptonMessages.ALREADY_EXISTS_EMAIL.getMessage());
    }

    @Test
    @DisplayName("이미 가입되어 있는 username/password로 로그인을 수행하여 결과값인 uid와 access token을 받을 수 있다.")
    public void testLogin() throws Exception{
        //given
        UserRequest.Login loginForm = new UserRequest.Login("honggildong", "abc1234!");

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
                ()->assertThat(result.getUid()).isEqualTo("user-001"),
                ()->assertThat(isJwtToken(result.getAccessToken())).isTrue());
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