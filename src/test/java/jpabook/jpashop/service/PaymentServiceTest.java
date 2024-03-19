package jpabook.jpashop.service;

import jpabook.jpashop.domain.user.Account;
import jpabook.jpashop.domain.user.User;
import jpabook.jpashop.dto.AccountDto;
import jpabook.jpashop.repository.AccountRepository;
import jpabook.jpashop.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.*;


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

        User testUser = new User(
                givenUserUid, "email@email.com", "name", "http://naver.com/image.png", "address", "detailedAddress"
        );
        userRepository.save(testUser);
        String accountUid = paymentService.addAccount(new AccountDto.Create(givenUserUid, givenBalance));

        //when
        paymentService.withdraw(new AccountDto.Transfer(accountUid, withdrawAmount));

        //then
        Account actual = accountRepository.findByUid(accountUid).orElseThrow(RuntimeException::new);
        assertThat(actual.getBalance()).isEqualTo(givenBalance - withdrawAmount);

    }


}