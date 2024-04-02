package jpabook.jpashop.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jpabook.jpashop.controller.request.UserAccountRequest;
import jpabook.jpashop.controller.response.UserAccountResponse;
import jpabook.jpashop.domain.user.Account;
import jpabook.jpashop.enums.user.account.CashFlowStatus;
import jpabook.jpashop.enums.user.account.CashFlowType;
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
import java.util.List;

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
    @DisplayName("회원 인증이 완료된 유저가 새로운 계좌를 추가해 잔고가 0원인 채로 DB에 저장할 수 있다.")
    public void testCreateAccount() throws Exception{
        //given
        String givenUid = "user-001";
        String givenToken = tokenProvider.sign(givenUid, new Date());

        UserAccountRequest.Create createAccountForm = new UserAccountRequest.Create("내 계좌");
        String createAccountFormString = objectMapper.writeValueAsString(createAccountForm);


        //when
        MvcResult mvcResponse = mockMvc.perform(post("/api/user/account")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + givenToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createAccountFormString)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andDo(print()).andExpect(status().isOk()).andReturn();

        //then
        UserAccountResponse.Create actual = objectMapper.readValue(mvcResponse.getResponse().getContentAsString(), UserAccountResponse.Create.class);
        Account account = accountRepository.findByUid(actual.getAccountUid())
                .orElseThrow(RuntimeException::new);

        assertThat(account.getUser().getUid()).isEqualTo(givenUid);
        assertThat(account).extracting("uid", "name", "balance")
                .contains(actual.getAccountUid(), "내 계좌", 0L);
    }

    @Test
    @DisplayName("회원인증이 완료된 유저는 자기가 가진 가상계좌의 리스트를 조회할 수 있다.")
    public void testGetAccountList() throws Exception{
        //given
        String givenUid = "user-001";
        String givenAccountUid = "account-001";
        String givenAccountName = "내 계좌";
        Long givenAccountBalance = 1000L;

        String givenToken = tokenProvider.sign(givenUid, new Date());

        //when
        MvcResult mvcResponse = mockMvc.perform(get("/api/user/account/list")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + givenToken)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andDo(print()).andExpect(status().isOk()).andReturn();
        //then
        List<UserAccountResponse.Info> actual = objectMapper.readValue(mvcResponse.getResponse().getContentAsString(), new TypeReference<List<UserAccountResponse.Info>>() {
        });

        assertThat(actual).extracting("accountUid", "accountName", "balance")
                .contains(tuple(givenAccountUid, givenAccountName, givenAccountBalance));
    }


    @Test
    @DisplayName("사용자는 DB에 존재하는 자신의 가상 계좌에 금액을 추가해 DB에 반영할 수 있다.")
    void testDeposit() throws Exception{
        // given
        String givenUid = "user-001";
        String givenAccountUid = "account-001";
        Long givenAccountBalance = 1000L;
        Integer givenDepositAmount = 500;

        String givenToken = tokenProvider.sign(givenUid, new Date());

        String reqBody = createDepositRequestJson(givenAccountUid, givenDepositAmount);


        // when
        MvcResult mvcResponse = mockMvc.perform(post("/api/user/account/deposit")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + givenToken)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reqBody))
                .andDo(print()).andExpect(status().isOk()).andReturn();

        // then
        UserAccountResponse.CashflowResult result = objectMapper.readValue(mvcResponse.getResponse().getContentAsString(), UserAccountResponse.CashflowResult.class);

        Account account = accountRepository.findByUid(givenAccountUid)
                .orElseThrow(RuntimeException::new);


        assertThat(result).extracting("accountUid", "amount", "type", "status")
                        .contains(givenAccountUid, givenDepositAmount, CashFlowType.DEPOSIT, CashFlowStatus.DONE);

        assertThat(account.getBalance()).isEqualTo(givenAccountBalance + givenDepositAmount);

    }

    @Test
    @DisplayName("사용자가 가상계좌에 입금하는 경우 자신의 가상계좌가 아니라면 403 오류와 함께 DB에 반영되지 않는다.")
    void testWhenDepositToOthersAccountThenThrowForbidden() throws Exception{
        // given
        String givenUid = "user-002";
        String givenAccountUid = "account-001";
        Long givenAccountBalance = 1000L;
        Integer givenDepositAmount = 500;

        String givenToken = tokenProvider.sign(givenUid, new Date());

        String reqBody = createDepositRequestJson(givenAccountUid, givenDepositAmount);

        // when
        mockMvc.perform(post("/api/user/account/deposit")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + givenToken)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reqBody))
                .andDo(print()).andExpect(status().isForbidden());

        // then
        Account account = accountRepository.findByUid(givenAccountUid)
                .orElseThrow(RuntimeException::new);
        assertThat(account.getBalance()).isEqualTo(givenAccountBalance);
    }


    private String createDepositRequestJson(String givenAccountUid, Integer givenDepositAmount) throws JsonProcessingException {
        UserAccountRequest.CashFlowRequest requestBody = new UserAccountRequest.CashFlowRequest(givenAccountUid, givenDepositAmount);
        String reqBody = objectMapper.writeValueAsString(requestBody);
        return reqBody;
    }


}