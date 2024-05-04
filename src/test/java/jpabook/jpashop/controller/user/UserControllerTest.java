package jpabook.jpashop.controller.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jpabook.jpashop.controller.common.request.UserRequest;
import jpabook.jpashop.controller.common.response.ErrorResponse;
import jpabook.jpashop.controller.common.response.UserResponse;
import jpabook.jpashop.domain.user.AddressInfo;
import jpabook.jpashop.domain.user.User;
import jpabook.jpashop.exception.user.UserExceptonMessages;
import jpabook.jpashop.repository.UserRepository;
import jpabook.jpashop.util.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static jpabook.jpashop.testUtils.TestDataUtils.user1;
import static jpabook.jpashop.testUtils.TestDataUtils.user2;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(value = "classpath:init-user-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;


    @Test
    @DisplayName("Username/Password로 회원가입을 수행해 DB에 저장할 수 있다.")
    public void given_CreateUserForm_when_RegisterUsernamePasswordAuthType_then_Success() throws Exception{
        //given
        String givenUsername = "username";
        UserRequest.Create createForm = new UserRequest.Create(
                "name",
                "email@email.com",
                "address",
                "detailedAddress",
                "http://example.com/image.png",
                givenUsername,
                "abc1234!"
        );
        String createFormString = objectMapper.writeValueAsString(createForm);

        //when
        mockMvc.perform(post("/api/user/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createFormString))
                .andDo(print()).andExpect(status().isOk());
        //then
        User user = userRepository.findByUsername(givenUsername)
                .orElseThrow(RuntimeException::new);

        assertAll("유저의 기본 정보를 모두 담고 있어야 한다.",
                ()->assertThat(user).extracting("email", "name", "profileImageUrl", "addressInfo")
                        .contains("email@email.com", "name", "http://example.com/image.png", new AddressInfo("address", "detailedAddress")),
                ()->assertThat(user.getUid()).isNotNull());

        boolean isPasswordMatches = passwordEncoder.matches("abc1234!", user.getUsernamePasswordAuthInfo().getPassword());

        assertAll("유저의 인증정보가 저장되어 후에 인증이 수행가능해야 한다.",
                ()->assertThat(user.getUsernamePasswordAuthInfo().getUsername()).isEqualTo(givenUsername),
                ()->assertThat(isPasswordMatches).isTrue());

    }

    @Test
    @DisplayName("Username/Password로 회원가입을 수행할 때 비밀번호가 조건에 만족하지 못한다면 400 코드를 반환하며 회원가입에 실패한다.")
    public void given_CreateUserFormWithInvalidPassword_when_RegisterUsernamePasswordAuthType_then_Fail() throws Exception{
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
    public void given_CreateUserFormWithExistsUsername_when_RegisterUsernamePasswordAuthType_then_Fail() throws Exception{
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
    public void given_CreateUserFormWithExistsEmail_when_RegisterUsernamePasswordAuthType_then_Fail() throws Exception{
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
    @DisplayName("access Token을 가지고 있는 유저의 이름, 이메일, 주소, 프로필 이미지를 조회할 수 있다.")
    public void given_AuthenticatedUser_when_GetUserInfo_then_Return() throws Exception{
        //given
        User givenUser = user1;

        String givenToken = tokenProvider.sign(givenUser.getUid(), new Date());
        //when
        MvcResult mvcResponse = mockMvc.perform(get("/api/user/info")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + givenToken)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andDo(print()).andExpect(status().isOk())
                .andReturn();
        //then
        UserResponse.Detail result = objectMapper.readValue(mvcResponse.getResponse().getContentAsString(), UserResponse.Detail.class);

        assertThat(result).extracting(
                "uid",
                        "name",
                        "addressInfo",
                        "email",
                        "profileImageUrl"
                )
                .contains(
                        givenUser.getUid(),
                        givenUser.getName(),
                        givenUser.getAddressInfo(),
                        givenUser.getEmail(),
                        givenUser.getProfileImageUrl()
                );
    }

    @Test
    @DisplayName("access Token을 가지고 있는 유저의 이름, 주소, 프로필 이미지를 수정해 DB에 반영할 수 있다.")
    public void given_AuthenticatedUser_when_UpdateUserInfo_then_Success() throws Exception{
        //given
        String givenUid = user1.getUid();
        String givenToken = tokenProvider.sign(givenUid, new Date());

        String updatedName = "updatedName";
        AddressInfo updatedAddressInfo = new AddressInfo("updatedAddress", "updatedDetail");
        String updatedProfileImage = "http://example.com/update.png";

        String requestBody = createUserInfoUpdateBody(updatedName, updatedAddressInfo, updatedProfileImage);
        //when
         mockMvc.perform(put("/api/user/info")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + givenToken)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print()).andExpect(status().isOk());

        //then
        User user = userRepository.findByUid(givenUid)
                .orElseThrow(RuntimeException::new);

        assertThat(user).extracting("uid", "name", "addressInfo", "profileImageUrl")
                .contains(givenUid, updatedName, updatedAddressInfo, updatedProfileImage);

    }


    @Test
    @DisplayName("username/password 인증방식이 설정된 유저는 새 비밀번호와 기존 비밀번호를 입력해 비밀번호를 업데이트해 DB에 반영할 수 있다. ")
    public void given_AuthenticatedUser_when_UpdateUserPassword_then_Success() throws Exception{
        //given
        String givenUid = user1.getUid();


        String givenToken = tokenProvider.sign(givenUid, new Date());

        String beforePassword = "abc1234!";
        String updatedPassword = "update123!";

        String updateFormString = createUpdatePasswordBody(beforePassword, updatedPassword);

        //when
        mockMvc.perform(put("/api/user/password")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + givenToken)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateFormString))
                .andDo(print()).andExpect(status().isOk());
        //then
        User user = userRepository.findByUid(givenUid)
                .orElseThrow(RuntimeException::new);

        boolean isPasswordMatchesWithUpdatedPassword = passwordEncoder.matches(updatedPassword, user.getUsernamePasswordAuthInfo().getPassword());

        assertThat(isPasswordMatchesWithUpdatedPassword).isTrue();
    }

    @Test
    @DisplayName("사용자가 비밀번호 변경 시도시 새 비밀번호가 비밀번호 제약조건에 맞지 않으면 400오류를 throw하며 DB에 업데이트되지 않는다.")
    public void given_AuthenticatedUser_when_UpdateUserPasswordWithInValidAfterPassword_then_Fail() throws Exception{
        //given
        String givenUid = user1.getUid();
        String givenToken = tokenProvider.sign(givenUid, new Date());


        String beforePassword = "abc1234!";
        String updatedPassword = "1234";

        String updateFormString = createUpdatePasswordBody(beforePassword, updatedPassword);
        //when
        MvcResult mvcResponse = mockMvc.perform(put("/api/user/password")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + givenToken)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateFormString))
                .andDo(print()).andExpect(status().isBadRequest()).andReturn();
        //then
        ErrorResponse result = objectMapper.readValue(mvcResponse.getResponse().getContentAsString(), ErrorResponse.class);

        User user = userRepository.findByUid(givenUid)
                .orElseThrow(RuntimeException::new);

        boolean isPasswordMatchesWithUpdatedPassword = passwordEncoder.matches(updatedPassword, user.getUsernamePasswordAuthInfo().getPassword());

        assertThat(isPasswordMatchesWithUpdatedPassword).isFalse();
        assertThat(result.getMessage()).isEqualTo(UserExceptonMessages.INVALID_PASSWORD_EXPRESSION.getMessage());
    }
    
    @Test
    @DisplayName("사용자가 비밀번호 변경 시도시 기존 비밀번호가 일치하지 않는다면 401 오류를 throw하며 DB에 업데이트되지 않는다.")
    public void given_AuthenticatedUser_when_UpdateUserPasswordWithInCorrectBeforePassword_then_Fail() throws Exception{
        //given
        String givenUid = "user-001";
        String givenToken = tokenProvider.sign(givenUid, new Date());
        String incorrectBeforePassword = "1234";
        String updatedPassword = "updated1234!";

        String updateFormString = createUpdatePasswordBody(incorrectBeforePassword, updatedPassword);
        //when
        MvcResult mvcResponse = mockMvc.perform(put("/api/user/password")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + givenToken)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateFormString))
                .andDo(print()).andExpect(status().isUnauthorized()).andReturn();
        //then
        ErrorResponse result = objectMapper.readValue(mvcResponse.getResponse().getContentAsString(), ErrorResponse.class);;

        User user = userRepository.findByUid(givenUid)
                .orElseThrow(RuntimeException::new);

        boolean isPasswordMatchesWithUpdatedPassword = passwordEncoder.matches(updatedPassword, user.getUsernamePasswordAuthInfo().getPassword());

        assertThat(isPasswordMatchesWithUpdatedPassword).isFalse();
        assertThat(result.getMessage()).isEqualTo(UserExceptonMessages.INVALID_PASSWORD.getMessage());
    }

    @Test
    @DisplayName("사용자가 비밀번호 변경 시도시 username/password가 설정되지 않은 유저라면 403 오류를 throw하며 DB에 업데이트되지 않는다.")
    public void given_AuthenticatedUser_when_UpdateUserPasswordWithoutUsernameAuthType_then_Fail() throws Exception{
        //given
        String givenUid = user2.getUid();
        String givenToken = tokenProvider.sign(givenUid, new Date());
        String updatedPassword = "test123!@";

        String updateFormString = createUpdatePasswordBody("1234", updatedPassword);
        //when
        MvcResult mvcResponse = mockMvc.perform(put("/api/user/password")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + givenToken)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateFormString))
                .andDo(print()).andExpect(status().isForbidden()).andReturn();
        //then
        ErrorResponse result = objectMapper.readValue(mvcResponse.getResponse().getContentAsString(), ErrorResponse.class);;

        User user = userRepository.findByUid(givenUid)
                .orElseThrow(RuntimeException::new);

        assertThat(user.getUsernamePasswordAuthInfo()).isNull();
        assertThat(result.getMessage()).isEqualTo(UserExceptonMessages.NO_USERNAME_PASSWORD_AUTH_INFO.getMessage());
    }




    private String createUserInfoUpdateBody(String updatedName, AddressInfo updatedAddressInfo, String updatedProfileImage) throws JsonProcessingException {
        UserRequest.Update updatedForm = new UserRequest.Update(
                updatedName,
                updatedAddressInfo,
                updatedProfileImage
        );

        String updateFormString = objectMapper.writeValueAsString(updatedForm);
        return updateFormString;
    }

    private String createUpdatePasswordBody(String previousPassword, String updatedPassword) throws JsonProcessingException {
        UserRequest.UpdatePassword updateForm = new UserRequest.UpdatePassword(previousPassword, updatedPassword);

        String updateFormString = objectMapper.writeValueAsString(updateForm);
        return updateFormString;
    }

}