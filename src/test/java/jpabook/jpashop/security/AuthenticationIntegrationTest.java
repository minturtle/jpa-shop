package jpabook.jpashop.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import jpabook.jpashop.controller.common.response.ErrorResponse;
import jpabook.jpashop.domain.user.User;
import jpabook.jpashop.exception.user.UserExceptonMessages;
import jpabook.jpashop.testUtils.TestDataUtils;
import jpabook.jpashop.util.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

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



}
