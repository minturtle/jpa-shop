package jpabook.jpashop.service;

import jpabook.jpashop.dto.OrderDto;
import jpabook.jpashop.repository.UserRepository;
import jpabook.jpashop.repository.product.ProductRepository;
import jpabook.jpashop.testUtils.InitTestDataUtils;
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

import java.util.List;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
@Import(OrderServiceTest.TestConfig.class)
class OrderServiceTest {

    @Autowired
    private InitTestDataUtils initTestDataUtils;

    @Autowired
    private OrderService orderService;


    @BeforeEach
    @Transactional
    void setUp(){
        initTestDataUtils.saveKakaoUser();
        initTestDataUtils.saveTestProducts();
    }

    @AfterEach
    void tearDown() {
        initTestDataUtils.deleteAll();
    }

    @Test
    @DisplayName("Account에 주문 금액 이상의 잔고를 가진 유저가 특정 상품들에 대해 주문을 수행할 수 있다.")
    void testOrder() throws Exception{
        // given
        Long givenBalance = 1000000L;
        initTestDataUtils.saveAccount(givenBalance);

        List<OrderDto.OrderProductRequestInfo> orderList = List.of();

        // when


        OrderDto.Detail result = orderService.order(initTestDataUtils.ACCOUNT_UID, orderList);

        assertThat(result);


        // then
    }

    @TestConfiguration
    public static class TestConfig{


        @Bean
        public InitTestDataUtils initDbUtils(
                UserRepository userRepository,
                ProductRepository productRepository
        ){
            return new InitTestDataUtils(productRepository, userRepository);
        }
    }

}