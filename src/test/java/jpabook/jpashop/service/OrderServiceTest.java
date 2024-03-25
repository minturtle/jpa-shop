package jpabook.jpashop.service;

import jpabook.jpashop.domain.product.Album;
import jpabook.jpashop.domain.product.Book;
import jpabook.jpashop.domain.product.Movie;
import jpabook.jpashop.domain.product.Product;
import jpabook.jpashop.domain.user.Account;
import jpabook.jpashop.domain.user.User;
import jpabook.jpashop.dto.AccountDto;
import jpabook.jpashop.exception.common.CannotFindEntityException;
import jpabook.jpashop.repository.AccountRepository;
import jpabook.jpashop.repository.UserRepository;
import jpabook.jpashop.repository.product.ProductRepository;
import jpabook.jpashop.testUtils.InitDbUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;


import static org.assertj.core.api.Assertions.*;



@SpringBootTest
@ActiveProfiles("test")
@Import(OrderServiceTest.TestConfig.class)
class OrderServiceTest {

    @Autowired
    private InitDbUtils initDbUtils;


    @BeforeEach
    @Transactional
    void setUp(){
        initDbUtils.saveKakaoUser();
        initDbUtils.saveAccount();
        initDbUtils.saveTestProducts();
    }

    @AfterEach
    void tearDown() {
        initDbUtils.deleteAll();
    }

    @Test
    @DisplayName("Account에 주문 금액 이상의 잔고를 가진 유저가 특정 상품들에 대해 주문을 수행할 수 있다.")
    void testOrder() throws Exception{
        // given

        // when

        // then
    }

    @TestConfiguration
    public static class TestConfig{


        @Bean
        public InitDbUtils initDbUtils(
                UserRepository userRepository,
                ProductRepository productRepository
        ){
            return new InitDbUtils(productRepository, userRepository);
        }
    }

}