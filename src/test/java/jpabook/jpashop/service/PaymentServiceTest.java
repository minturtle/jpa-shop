package jpabook.jpashop.service;

import jakarta.persistence.LockTimeoutException;
import jakarta.persistence.PessimisticLockException;
import jpabook.jpashop.domain.user.Account;
import jpabook.jpashop.domain.user.User;
import jpabook.jpashop.dto.AccountDto;
import jpabook.jpashop.exception.user.CannotFindUserException;
import jpabook.jpashop.exception.user.account.AccountExceptionMessages;
import jpabook.jpashop.exception.user.account.NegativeBalanceException;
import jpabook.jpashop.repository.AccountRepository;
import jpabook.jpashop.repository.UserRepository;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;


@SpringBootTest
@ActiveProfiles("test")
class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("유저의 Account를 추가할 수 있다. 이 때, account의 금액은 0원으로 초기화 된다.")
    public void testCreateUserAcccount() throws Exception{
        //given
        String givenUid = "uid";
        User testUser = new User(
                givenUid, "email@email.com", "name", "http://naver.com/image.png", "address", "detailedAddress"
        );


        userRepository.save(testUser);
        //when
        paymentService.addAccount(new AccountDto.Create(givenUid, 0L));

        //then
        User actual = userRepository.findByUidJoinAccount(givenUid).orElseThrow(RuntimeException::new);
        List<Account> actualAccountList = actual.getAccountList();


        assertThat(actualAccountList).hasSize(1);
        assertThat(actualAccountList.get(0).getBalance()).isEqualTo(0L);
    }

    @Test
    @DisplayName("유저의 잔고에서 잔고의 금액보다 작은 금액을 출금할 수 있다.")
    public void testWithdraw() throws Exception{
        //given
        String givenUserUid = "uid";
        long givenBalance = 1000L;
        long withdrawAmount = 500L;

        String accountUid = createTestUserAndAccount(givenUserUid, givenBalance);

        //when
        paymentService.withdraw(new AccountDto.Transfer(accountUid, withdrawAmount));

        //then
        Account actual = getAccount(accountUid);
        assertThat(actual.getBalance()).isEqualTo(givenBalance - withdrawAmount);

    }


    @Test
    @DisplayName("유저의 잔고보다 큰 금액을 출금시도할 시 오류를 throw한다.")
    void testWithdrawFail() throws Exception{
        // given
        String givenUserUid = "uid";
        long givenBalance = 500L;
        long withdrawAmount = 1000L;

        String accountUid = createTestUserAndAccount(givenUserUid, givenBalance);
        // when
        ThrowableAssert.ThrowingCallable throwingCallable =
                ()-> paymentService.withdraw(new AccountDto.Transfer(accountUid, withdrawAmount));

        // then
        assertThatThrownBy(throwingCallable)
                .isInstanceOf(NegativeBalanceException.class)
                .hasMessage(AccountExceptionMessages.NEGATIVE_ACCOUNT_BALANCE.getMessage());

    }

    @Test
    @DisplayName("한 account에 동시에 출금을 시도할 시, 순차적으로 출금 요청이 수행된다.")
    void testWithdrawConcurrency() throws Exception{
        // given
        String givenUserUid = "uid";
        long givenBalance = 1000L;
        long withdrawAmount = 200L;

        String accountUid = createTestUserAndAccount(givenUserUid, givenBalance);


        int threadSize = 2;

        CountDownLatch countDownLatch = new CountDownLatch(2);
        ExecutorService executorService = Executors.newFixedThreadPool(threadSize);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();


        // when
        for(int i = 0; i < threadSize; i++){
            executorService.execute(()->{
                try{
                    paymentService.withdraw(new AccountDto.Transfer(accountUid, withdrawAmount));
                    successCount.getAndIncrement();
                }catch (LockTimeoutException e){
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
        Account actual = getAccount(accountUid);
        long expectedBalance = givenBalance - (withdrawAmount * successCount.get());

        assertAll("thread run count check",
                ()->assertThat(successCount.get()).isEqualTo(2),
                ()->assertThat(failCount.get()).isEqualTo(0));

        assertThat(actual.getBalance()).isEqualTo(expectedBalance);
    }
    
    
    

    private String createTestUserAndAccount(String givenUserUid, long givenBalance) throws CannotFindUserException {
        User testUser = new User(
                givenUserUid, "email@email.com", "name", "http://naver.com/image.png", "address", "detailedAddress"
        );
        userRepository.save(testUser);
        String accountUid = paymentService.addAccount(new AccountDto.Create(givenUserUid, givenBalance));
        return accountUid;
    }

    private Account getAccount(String accountUid) {
        Account actual = accountRepository.findByUid(accountUid).orElseThrow(RuntimeException::new);
        return actual;
    }

}