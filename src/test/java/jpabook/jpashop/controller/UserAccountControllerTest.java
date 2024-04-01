package jpabook.jpashop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jpabook.jpashop.controller.response.UserAccountResponse;
import jpabook.jpashop.domain.user.Account;
import jpabook.jpashop.repository.AccountRepository;
import jpabook.jpashop.repository.UserRepository;
import jpabook.jpashop.util.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(value = "classpath:init-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class UserAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Test
    @DisplayName("회원 인증이 완료된 유저는 새로운 계좌를 추가해 잔고가 0원인 채로 DB에 저장할 수 있다.")
    public void testCreateAccount() throws Exception{
        //given
        String givenUid = "user-001";
        String givenToken = tokenProvider.sign(givenUid, new Date());

        //when
        MvcResult mvcResponse = mockMvc.perform(post("/api/user/account")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + givenToken)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andDo(print()).andExpect(status().isOk()).andReturn();

        //then
        UserAccountResponse.Create actual = objectMapper.readValue(mvcResponse.getResponse().getContentAsString(), UserAccountResponse.Create.class);
        Account account = accountRepository.findByUid(actual.getAccountUid())
                .orElseThrow(RuntimeException::new);

        assertThat(account.getUser().getUid()).isEqualTo(givenUid);
        assertThat(account.getUid()).isEqualTo(actual.getAccountUid());
        assertThat(account.getBalance()).isEqualTo(0L);
    }

}