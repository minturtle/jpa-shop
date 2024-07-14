package jpabook.jpashop.service.user;

import jpabook.jpashop.domain.user.Account;
import jpabook.jpashop.domain.user.User;
import jpabook.jpashop.dto.AccountDto;
import jpabook.jpashop.enums.user.account.CashFlowStatus;
import jpabook.jpashop.enums.user.account.CashFlowType;
import jpabook.jpashop.exception.user.account.AccountExceptionMessages;
import jpabook.jpashop.exception.user.account.InvalidBalanceValueException;
import jpabook.jpashop.repository.AccountRepository;
import jpabook.jpashop.repository.UserRepository;
import jpabook.jpashop.service.AccountService;
import jpabook.jpashop.testUtils.ServiceTest;
import org.assertj.core.api.ThrowableAssert;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static jpabook.jpashop.testUtils.TestDataUtils.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;


@Sql(value = "classpath:init-user-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"classpath:clean-up.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class AccountServiceTest extends ServiceTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;


    @Test
    @DisplayName("유저의 Account를 추가할 수 있다.")
    public void given_User_when_addAccount_thenSave() throws Exception{
        //given
        String givenUid = user1.getUid();
        String givenAccountName = "내계좌";
        long givenAccountBalance = 0L;

        //when
        accountService.addAccount(new AccountDto.Create(givenUid, givenAccountName, givenAccountBalance));

        //then
        User actual = userRepository.findByUidJoinAccount(givenUid).orElseThrow(RuntimeException::new);
        List<Account> actualAccountList = actual.getAccountList();


        assertThat(actualAccountList).hasSize(3);
        assertThat(actualAccountList).extracting("name", "balance")
                .contains(
                        Tuple.tuple(account1.getName(), account1.getBalance()),
                        Tuple.tuple(account2.getName(), account2.getBalance()),
                        Tuple.tuple(givenAccountName, givenAccountBalance)
                );
    }

    @Test
    @DisplayName("유저의 잔고에서 잔고의 금액보다 작은 금액을 출금할 수 있다.")
    public void given_accountHasEnoughBalance_when_WithDraw_thenSuccess() throws Exception{
        //given
        String givenUserUid = user1.getUid();
        String givenAccountUid = account1.getUid();
        Long givenBalance = account1.getBalance();
        int givenWithdrawAmount = 500;


        //when
        AccountDto.CashFlowResult result = accountService.withdraw(new AccountDto.CashFlowRequest(givenUserUid, givenAccountUid, givenWithdrawAmount));

        //then
        assertThat(result).extracting("accountUid", "amount", "type", "status")
                .contains(givenAccountUid, givenWithdrawAmount, CashFlowType.WITHDRAW, CashFlowStatus.DONE);

        Account actual = getAccount(givenAccountUid);
        assertThat(actual.getBalance()).isEqualTo(givenBalance - givenWithdrawAmount);

    }


    @Test
    @DisplayName("유저의 잔고보다 큰 금액을 출금시도할 시 오류를 throw한다.")
    void given_accountHasNotEnoughBalance_when_Withdraw_then_failedAndRollback() throws Exception{
        // given
        String givenUserUid =  user1.getUid();
        String accountUid = account1.getUid();
        Long givenBalance = account1.getBalance();
        int withdrawAmount = (int) (givenBalance + 1000);

        // when
        ThrowableAssert.ThrowingCallable throwingCallable =
                ()-> accountService.withdraw(new AccountDto.CashFlowRequest(givenUserUid, accountUid, withdrawAmount));

        // then
        assertThatThrownBy(throwingCallable)
                .isInstanceOf(InvalidBalanceValueException.class)
                .hasMessage(AccountExceptionMessages.NEGATIVE_ACCOUNT_BALANCE.getMessage());

    }


    
    @Test
    @DisplayName("특정 Account에 입금할 수 있다.")
    void given_Account_when_Deposit_then_success() throws Exception{
        // given
        String givenUserUid = user1.getUid();
        String accountUid = account1.getUid();
        long givenBalance = account1.getBalance();
        int depositAmount = 500;


        // when
        AccountDto.CashFlowResult result = accountService.deposit(new AccountDto.CashFlowRequest(givenUserUid, accountUid, depositAmount));

        // then
        assertThat(result).extracting("accountUid", "amount", "type", "status")
                .contains(accountUid, depositAmount, CashFlowType.DEPOSIT, CashFlowStatus.DONE);

        Account actual = getAccount(accountUid);
        assertThat(actual.getBalance()).isEqualTo(givenBalance + depositAmount);

    }
    
    @Test
    @DisplayName("특정 Account에 값이 MAX값보다 큰 경우, 오버플로우를 방지하기 위해 오류가 throw된다.")
    void given_Account_when_DepositMuch_then_FailedAndRollback() throws Exception{
        // given
        String givenUserUid = user1.getUid();
        String accountUid = account1.getUid();
        int depositAmount = Integer.MAX_VALUE - 1;

        // when
        ThrowableAssert.ThrowingCallable throwingCallable = ()->{
            accountService.deposit(new AccountDto.CashFlowRequest(givenUserUid, accountUid, depositAmount));
        };
        // then
        assertThatThrownBy(throwingCallable)
                .isInstanceOf(InvalidBalanceValueException.class)
                .hasMessage(AccountExceptionMessages.BALANCE_OVERFLOW.getMessage());
    }

    @Test
    @DisplayName("유저의 가상계좌 리스트를 조회할 수 있다.")
    public void given_UserHasAccount_when_getUsersAccountList_then_Return() throws Exception{
        //given
        List<AccountDto.Info> givenUser = accountService.findAccountsByUser(user1.getUid());
        Account givenAccount1 = account1;
        Account givenAccount2 = account2;

        //when
        List<AccountDto.Info> result = givenUser;

        //then
        assertThat(result).extracting("accountName", "balance")
                .contains(Tuple.tuple(givenAccount1.getName(), givenAccount1.getBalance()),
                        Tuple.tuple(givenAccount2.getName(), givenAccount2.getBalance()));
    }

    @Test
    @DisplayName("한 account에 동시에 출금을 시도할 시, 첫번째 출금 요청만 성공한다.")
    @DisabledIfEnvironmentVariable(named = "CI", matches = "true")
    void given_Account_when_DepositConcurrently_then_SuccessFirstOnly() throws Exception{
        // given
        String givenUserUid = user1.getUid();
        String givenAccountUid = account1.getUid();
        long givenBalance = account1.getBalance();
        int givenWithdrawAmount = 200;


        int threadSize = 2;

        CountDownLatch countDownLatch = new CountDownLatch(2);
        ExecutorService executorService = Executors.newFixedThreadPool(threadSize);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();


        // when
        for(int i = 0; i < threadSize; i++){
            executorService.execute(()->{
                try{
                    accountService.withdraw(new AccountDto.CashFlowRequest(givenUserUid, givenAccountUid, givenWithdrawAmount));
                    successCount.getAndIncrement();
                }catch (OptimisticLockingFailureException e){
                    failCount.getAndIncrement();
                }catch (Exception e){
                    e.printStackTrace();
                    Assertions.fail();
                }finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();
        executorService.shutdown();

        // then
        Account actual = accountRepository.findByUid(givenAccountUid).orElseThrow(RuntimeException::new);
        long expectedBalance = givenBalance - (givenWithdrawAmount * successCount.get());

        assertAll("thread run count check",
                ()->assertThat(successCount.get()).isEqualTo(1),
                ()->assertThat(failCount.get()).isEqualTo(1));

        assertThat(actual.getBalance()).isEqualTo(expectedBalance);
    }



    private Account getAccount(String accountUid) {
        Account actual = accountRepository.findByUid(accountUid).orElseThrow(RuntimeException::new);
        return actual;
    }

}