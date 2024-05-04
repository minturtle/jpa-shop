package jpabook.jpashop.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import jpabook.jpashop.controller.common.response.ErrorResponse;
import jpabook.jpashop.exception.user.UserExceptonMessages;
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



}
