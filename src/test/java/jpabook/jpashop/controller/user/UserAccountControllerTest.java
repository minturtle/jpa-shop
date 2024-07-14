package jpabook.jpashop.controller.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jpabook.jpashop.controller.common.request.UserAccountRequest;
import jpabook.jpashop.controller.common.response.UserAccountResponse;
import jpabook.jpashop.domain.user.Account;
import jpabook.jpashop.enums.user.account.CashFlowStatus;
import jpabook.jpashop.enums.user.account.CashFlowType;
import jpabook.jpashop.repository.AccountRepository;
import jpabook.jpashop.testUtils.ControllerTest;
import jpabook.jpashop.util.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import static jpabook.jpashop.testUtils.TestDataFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class UserAccountControllerTest extends ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        testDataFixture.saveUsers();
    }

    @Test
    @DisplayName("회원 인증이 완료된 유저가 새로운 계좌를 추가해 잔고가 0원인 채로 DB에 저장할 수 있다.")
    public void given_AuthenticatedUser_when_CreateAccount_then_CreatedWithBalanceIsZero() throws Exception{
        //given
        String givenUid = user2.getUid();
        String givenToken = tokenProvider.sign(givenUid, new Date());

        String givenNewAccountName = "내 계좌";

        UserAccountRequest.Create createAccountForm = new UserAccountRequest.Create(givenNewAccountName);
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
                .contains(actual.getAccountUid(), givenNewAccountName, 0L);
    }

    @Test
    @DisplayName("회원인증이 완료된 유저는 자기가 가진 가상계좌의 리스트를 조회할 수 있다.")
    public void given_AuthenticatedUserAccount_when_GetUsersAccount_then_ReturnList() throws Exception{
        //given
        String givenUid = user1.getUid();

        Account givenAccount = account1;
        Account givenAccount2 = account2;


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
                .contains(
                        tuple(givenAccount.getUid(), givenAccount.getName(), givenAccount.getBalance()),
                        tuple(givenAccount2.getUid(), givenAccount2.getName(), givenAccount2.getBalance())
                );
    }


    @Test
    @DisplayName("사용자는 DB에 존재하는 자신의 가상 계좌에 금액을 추가해 DB에 반영할 수 있다.")
    void given_AuthenticatedUserAccount_when_Deposit_then_Success() throws Exception{
        // given
        String givenUserUid = user1.getUid();
        Account givenAccount = account1;
        Integer givenDepositAmount = 500;

        String givenToken = tokenProvider.sign(givenUserUid, new Date());

        String reqBody = createDepositRequestJson(givenAccount.getUid(), givenDepositAmount);


        // when
        MvcResult mvcResponse = mockMvc.perform(post("/api/user/account/deposit")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + givenToken)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reqBody))
                .andDo(print()).andExpect(status().isOk()).andReturn();

        // then
        UserAccountResponse.CashflowResult result = objectMapper.readValue(mvcResponse.getResponse().getContentAsString(), UserAccountResponse.CashflowResult.class);

        Account account = accountRepository.findByUid(givenAccount.getUid())
                .orElseThrow(RuntimeException::new);


        assertThat(result).extracting("accountUid", "amount", "type", "status")
                        .contains(givenAccount.getUid(), givenDepositAmount, CashFlowType.DEPOSIT, CashFlowStatus.DONE);

        assertThat(account.getBalance()).isEqualTo(givenAccount.getBalance() + givenDepositAmount);

    }

    @Test
    @DisplayName("사용자가 가상계좌에 입금하는 경우 자신의 가상계좌가 아니라면 403 오류와 함께 DB에 반영되지 않는다.")
    void testWhenDepositToOthersAccountThenThrowForbidden() throws Exception{
        // given
        String givenOtherUserUid = user2.getUid();
        Account givenOtherAccount = account1;
        Integer givenDepositAmount = 500;

        String givenToken = tokenProvider.sign(givenOtherUserUid, new Date());

        String reqBody = createDepositRequestJson(givenOtherAccount.getUid(), givenDepositAmount);

        // when
        mockMvc.perform(post("/api/user/account/deposit")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + givenToken)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reqBody))
                .andDo(print()).andExpect(status().isForbidden());

        // then
        Account account = accountRepository.findByUid(givenOtherAccount.getUid())
                .orElseThrow(RuntimeException::new);
        assertThat(account.getBalance()).isEqualTo(givenOtherAccount.getBalance());
    }


    private String createDepositRequestJson(String givenAccountUid, Integer givenDepositAmount) throws JsonProcessingException {
        UserAccountRequest.CashFlowRequest requestBody = new UserAccountRequest.CashFlowRequest(givenAccountUid, givenDepositAmount);
        String reqBody = objectMapper.writeValueAsString(requestBody);
        return reqBody;
    }


}