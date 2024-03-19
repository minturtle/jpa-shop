package jpabook.jpashop.service;

import jpabook.jpashop.domain.user.Account;
import jpabook.jpashop.domain.user.User;
import jpabook.jpashop.dto.AccountDto;
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
        paymentService.addAccount(new AccountDto.Create(givenUid));

        //then
        User actual = userRepository.findByUidJoinAccount(givenUid).orElseThrow(RuntimeException::new);
        List<Account> actualAccountList = actual.getAccountList();


        assertThat(actualAccountList).hasSize(1);
        assertThat(actualAccountList.get(0).getBalance()).isEqualTo(0L);
    }


}