package jpabook.jpashop.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import jpabook.jpashop.controller.common.request.UserRequest;
import jpabook.jpashop.controller.common.response.UserResponse;
import jpabook.jpashop.domain.user.User;
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

import static jpabook.jpashop.testUtils.TestDataUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@Sql("classpath:init-user-test-data.sql")
public class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Test
    @DisplayName("Username/Password 방식으로 로그인을 수행해 성공시 유효한 엑세스 토큰을 발급받을 수 있다.")
    public void given_UsernamePasswordUser_when_DoLogin_then_ReturnAccessToken() throws Exception{
        //given
        User givenUser = user1;

        UserRequest.Login loginRequestBody = new UserRequest.Login(
                givenUser.getUsernamePasswordAuthInfo().getUsername(),
                "abc1234!"
        );

        //when
        MvcResult mvcResult = mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestBody)))
                .andExpect(status().isOk())
                .andReturn();
        //then
        UserResponse.Login responseBody = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserResponse.Login.class);


        assertThat(responseBody.getAccessToken()).isNotNull();
        assertThat(jwtTokenProvider.verify(responseBody.getAccessToken())).isEqualTo(givenUser.getUid());
        assertThat(responseBody.getUid()).isEqualTo(givenUser.getUid());
    }



}
