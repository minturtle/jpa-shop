package jpabook.jpashop.service.user;


import jpabook.jpashop.domain.user.Account;
import jpabook.jpashop.dto.AccountDto;
import jpabook.jpashop.repository.AccountRepository;
import jpabook.jpashop.repository.UserRepository;
import jpabook.jpashop.service.AccountService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static jpabook.jpashop.testUtils.TestDataUtils.account1;
import static jpabook.jpashop.testUtils.TestDataUtils.user1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@ActiveProfiles("test")
@Sql(value = "classpath:init-user-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class AccountConcurrencyTest {


    @Autowired
    private AccountService accountService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @AfterEach
    void tearDown() {
        accountRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("한 account에 동시에 출금을 시도할 시, 첫번째 출금 요청만 성공한다.")
    void testWithdrawConcurrency() throws Exception{
        // given
        String givenUserUid = user1.getUid();
        String accountUid = account1.getUid();
        long givenBalance = account1.getBalance();
        int withdrawAmount = 200;


        int threadSize = 2;

        CountDownLatch countDownLatch = new CountDownLatch(2);
        ExecutorService executorService = Executors.newFixedThreadPool(threadSize);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();


        // when
        for(int i = 0; i < threadSize; i++){
            executorService.execute(()->{
                try{
                    accountService.withdraw(new AccountDto.CashFlowRequest(givenUserUid, accountUid, withdrawAmount));
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
        Account actual = accountRepository.findByUid(accountUid).orElseThrow(RuntimeException::new);
        long expectedBalance = givenBalance - (withdrawAmount * successCount.get());

        assertAll("thread run count check",
                ()->assertThat(successCount.get()).isEqualTo(1),
                ()->assertThat(failCount.get()).isEqualTo(1));

        assertThat(actual.getBalance()).isEqualTo(expectedBalance);
    }



}
