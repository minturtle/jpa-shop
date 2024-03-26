package jpabook.jpashop.service;

import jpabook.jpashop.domain.order.Order;
import jpabook.jpashop.domain.order.OrderStatus;
import jpabook.jpashop.domain.user.Account;
import jpabook.jpashop.dto.OrderDto;
import jpabook.jpashop.repository.OrderRepository;
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
import static jpabook.jpashop.testUtils.InitTestDataUtils.*;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@ActiveProfiles("test")
@Import(OrderServiceTest.TestConfig.class)
class OrderServiceTest {

    @Autowired
    private InitTestDataUtils initTestDataUtils;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

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
    @DisplayName("Account에 주문 금액 이상의 잔고를 가진 유저가 특정 상품들에 대해 주문을 수행하고, DB에 관련 정보를 저장할 수 있다.")
    void testOrder() throws Exception{
        // given
        Long givenBalance = 1000000L;
        initTestDataUtils.saveAccount(givenBalance);

        int movieOrderQuantity = 1;
        int albumOrderQuantity = 2;
        int bookOrderQuantity = 3;

        List<OrderDto.OrderProductRequestInfo> orderList = List.of(
                new OrderDto.OrderProductRequestInfo(MOVIE_UID , movieOrderQuantity),
                new OrderDto.OrderProductRequestInfo(ALBUM_UID, albumOrderQuantity),
                new OrderDto.OrderProductRequestInfo(BOOK_UID, bookOrderQuantity)
        );

        // when


        OrderDto.Detail result = orderService.order(USER_UID, ACCOUNT_UID, orderList);


        // then
        Account account = getAccountByUser();
        Order order = orderRepository.findByUidWithJoin(result.getOrderUid()).orElseThrow(RuntimeException::new);


        int expectedTotalPrice = MOVIE_PRICE * movieOrderQuantity + ALBUM_PRICE * albumOrderQuantity + BOOK_PRICE * bookOrderQuantity;


        assertAll("result는 주문에 관련한 정보를 모두 담고 있어야 한다.",
                ()->assertThat(result.getOrderStatus()).isEqualTo(OrderStatus.ORDERED),
                ()->assertThat(result.getOrderPaymentDetail()).extracting("accountUid", "totalPrice")
                        .contains(ACCOUNT_UID, expectedTotalPrice),
                ()->assertThat(result).extracting("orderUid", "orderTime").doesNotContainNull(),
                ()-> assertThat(result.getOrderProducts()).extracting("productUid", "productName","productImageUrl","unitPrice","quantity", "totalPrice")
                        .contains(
                                tuple(MOVIE_UID, "Inception", "http://example.com/inception.jpg", MOVIE_PRICE, movieOrderQuantity, MOVIE_PRICE * movieOrderQuantity),
                                tuple(ALBUM_UID, "The Dark Side of the Moon", "http://example.com/darkside.jpg", ALBUM_PRICE, albumOrderQuantity, ALBUM_PRICE * albumOrderQuantity),
                                tuple(BOOK_UID, "The Great Gatsby", "http://example.com/gatsby.jpg", BOOK_PRICE, bookOrderQuantity, BOOK_PRICE * bookOrderQuantity)
                        )
        );

        assertAll("주문 후에 주문 금액만큼 Account의 금액이 감소되어야 한다.",
                ()->assertThat(account.getBalance()).isEqualTo(givenBalance - expectedTotalPrice));

        assertAll("주문 정보를 모두 DB에 저장해야 한다.",
                ()->assertThat(order.getDeliveryInfo()).extracting("address", "detailedAddress").contains("address", "detailedAddress"),
                ()->assertThat(order.getPayment()).extracting("account", "amount").contains(account, expectedTotalPrice),
                ()->assertThat(order.getOrderProducts()).extracting("product").doesNotContainNull(),
                ()->assertThat(order.getOrderProducts()).extracting("count", "itemPrice").contains(
                        tuple(1, 15000),
                        tuple(2, 20000),
                        tuple(3, 10000)
                ));
    }

    private Account getAccountByUser() {
        Account account = userRepository.findByUidJoinAccount(USER_UID).orElseThrow(RuntimeException::new)
                .getAccountList().get(0);
        return account;
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