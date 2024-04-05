package jpabook.jpashop.controller.order;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jpabook.jpashop.controller.common.request.OrderRequest;
import jpabook.jpashop.controller.common.response.OrderResponse;
import jpabook.jpashop.domain.order.Order;
import jpabook.jpashop.domain.order.OrderStatus;
import jpabook.jpashop.domain.product.Product;
import jpabook.jpashop.domain.user.Account;
import jpabook.jpashop.domain.user.User;
import jpabook.jpashop.dto.PaginationListDto;
import jpabook.jpashop.repository.AccountRepository;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.UserRepository;
import jpabook.jpashop.repository.product.ProductRepository;
import jpabook.jpashop.util.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = {"classpath:init-product-test-data.sql", "classpath:init-user-test-data.sql", "classpath:init-cart-test-data.sql", "classpath:init-order-test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class OrderControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;


    @Test
    @DisplayName("사용자는 장바구니가 아닌 상품을 선택해 주문하면 Account의 잔액과 상품의 재고가 감소하고, 주문이 생성된다.")
    public void testWhenOrderNoCartItemThenSuccess() throws Exception{
        //given
        String givenUserUid = "user-001";

        String accessToken = tokenProvider.sign(givenUserUid, new Date());

        int orderQuantity = 2;


        OrderRequest.Create orderRequest = new OrderRequest.Create(
                "account-001",
                List.of(
                        new OrderRequest.ProductOrderInfo("album-001", orderQuantity)
                )
        );

        //when
        MvcResult mvcResult = mockMvc.perform(post("/api/order")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        //then
        OrderResponse.Detail actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), OrderResponse.Detail.class);
        Product product = productRepository.findByUid("album-001").orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));
        Account account = accountRepository.findByUid("account-001").orElseThrow(() -> new IllegalArgumentException("계정이 존재하지 않습니다."));

        assertAll("주문 완료후 주문 정보는 관련 정보를 모두 담고 있어야 한다.",
                ()->assertThat(actual.getOrderUid()).isNotNull(),
                ()->assertThat(actual.getOrderStatus()).isEqualTo(OrderStatus.ORDERED),
                ()->assertThat(actual.getOrderTime()).isNotNull(),
                ()->assertThat(actual.getOrderPaymentDetail())
                        .extracting("accountUid", "totalPrice")
                        .containsExactly("account-001", 4000),
                ()->assertThat(actual.getOrderProducts())
                        .extracting("productUid","unitPrice", "quantity", "totalPrice")
                        .containsExactly(tuple("album-001",2000, orderQuantity, 4000)));

        assertThat(product.getStockQuantity()).isEqualTo(5 - orderQuantity);
        assertThat(account.getBalance()).isEqualTo(100000 - 4000);

    }
    @Test
    @DisplayName("사용자는 장바구니에 담긴 상품을 주문하면 Account의 잔액과 상품의 재고가 감소하고, 주문이 생성되며 장바구니에 담긴 해당 물건은 삭제된다.")
    public void testWhenUserOrderInCartThenSuccess() throws Exception{
        //given
        String givenUserUid = "user-001";
        String accessToken = tokenProvider.sign(givenUserUid, new Date());
        int orderQuantity = 2;

        OrderRequest.Create orderRequest = new OrderRequest.Create(
                "account-001",
                List.of(
                        new OrderRequest.ProductOrderInfo("album-001", orderQuantity),
                        new OrderRequest.ProductOrderInfo("book-001", orderQuantity)
                )
        );
        //when
        mockMvc.perform(post("/api/order")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andDo(print())
                .andExpect(status().isOk());
        //then
        User user = userRepository.findByUid("user-001").orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));
        Product album = productRepository.findByUid("album-001").orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));
        Product book = productRepository.findByUid("book-001").orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));
        Account account = accountRepository.findByUid("account-001").orElseThrow(() -> new IllegalArgumentException("계좌가 존재하지 않습니다."));

        assertThat(user.getCartList()).isEmpty();
        assertThat(album.getStockQuantity()).isEqualTo(5 - orderQuantity);
        assertThat(book.getStockQuantity()).isEqualTo(20 - orderQuantity);
        assertThat(account.getBalance()).isEqualTo(100000 - 7000);

    }

    @Test
    @DisplayName("사용자가 상품의 남은 재고 이상을 주문하면 주문이 실패한다.")
    public void testWhenOrderMoreThanQuatityThenFailed() throws Exception{
        //given
        String givenUserUid = "user-001";
        String accessToken = tokenProvider.sign(givenUserUid, new Date());
        int orderQuantity = 1000;

        OrderRequest.Create orderRequest = new OrderRequest.Create(
                "account-001",
                List.of(
                        new OrderRequest.ProductOrderInfo("album-001", orderQuantity)
                )
        );
        //when
        mockMvc.perform(post("/api/order")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
        //then
        Product album = productRepository.findByUid("album-001").orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));
        Product book = productRepository.findByUid("book-001").orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));
        Product movie = productRepository.findByUid("movie-001").orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));
        Account account = accountRepository.findByUid("account-001").orElseThrow(() -> new IllegalArgumentException("계좌가 존재하지 않습니다."));


        assertThat(album.getStockQuantity()).isEqualTo(5);
        assertThat(book.getStockQuantity()).isEqualTo(20);
        assertThat(movie.getStockQuantity()).isEqualTo(8);
        assertThat(account.getBalance()).isEqualTo(100000L);
    }

    @Test
    @DisplayName("사용자가 잔액보다 많은 금액을 주문하면 주문이 실패한다.")
    public void testWhenOrderMoreThanBalanceThenFailed() throws Exception{
        //given
        String givenUserUid = "user-001";
        String accessToken = tokenProvider.sign(givenUserUid, new Date());
        int orderQuantity = 1000;

        OrderRequest.Create orderRequest = new OrderRequest.Create(
                "account-002",
                List.of(
                        new OrderRequest.ProductOrderInfo("album-001", orderQuantity)
                )
        );
        //when
        mockMvc.perform(post("/api/order")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
        //then
        Product album = productRepository.findByUid("album-001").orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));
        Product book = productRepository.findByUid("book-001").orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));
        Product movie = productRepository.findByUid("movie-001").orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));
        Account account = accountRepository.findByUid("account-002").orElseThrow(() -> new IllegalArgumentException("계좌가 존재하지 않습니다."));


        assertThat(album.getStockQuantity()).isEqualTo(5);
        assertThat(book.getStockQuantity()).isEqualTo(20);
        assertThat(movie.getStockQuantity()).isEqualTo(8);
        assertThat(account.getBalance()).isEqualTo(500L);
    }

    @Test
    @DisplayName("사용자는 자신의 주문 기록 리스트를 조회할 수 있다.")
    public void testWhenGetOrderHistoryListThenReturn() throws Exception{
        //given
        String givenUserUid = "user-001";
        String accessToken = tokenProvider.sign(givenUserUid, new Date());
        //when
        MvcResult mvcResult = mockMvc.perform(get("/api/order/list")
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        //then
        PaginationListDto<OrderResponse.Preview> actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<PaginationListDto<OrderResponse.Preview>>(){});

        assertThat(actual.getCount()).isEqualTo(2);
        assertThat(actual.getData())
                .extracting("orderUid", "totalPrice", "orderStatus", "orderTime")
                .containsExactly(
                        tuple("order-001", 1000, OrderStatus.ORDERED, LocalDateTime.of(2021, 8, 1, 0, 0, 0)),
                        tuple("order-002", 2000, OrderStatus.CANCELED, LocalDateTime.of(2021, 8, 2, 0, 0, 0))
                );
        assertThat(actual.getData()).extracting("name")
                .doesNotContainNull();


    }

    @Test
    @DisplayName("사용자는 주문 상세 정보를 조회할 수 있다.")
    public void thenWhenGetOrderDetailInfoThenReturn() throws Exception{
        //given
        String givenUserUid = "user-001";

        String accessToken = tokenProvider.sign(givenUserUid, new Date());
        String givenOrderUid = "order-001";

        //when
        MvcResult mvcResult = mockMvc.perform(get("/api/order/{orderId}", givenOrderUid)
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        //then
        OrderResponse.Detail actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), OrderResponse.Detail.class);

        assertThat(actual).extracting("orderUid", "orderStatus", "orderTime", "orderPaymentDetail.accountUid", "orderPaymentDetail.totalPrice")
                .containsExactly(
                        "order-001",
                        OrderStatus.ORDERED,
                        LocalDateTime.of(2021, 8, 1, 0, 0, 0),
                        "account-001",
                        1000
                );
        assertThat(actual.getOrderProducts())
                .extracting("productUid", "unitPrice", "quantity", "totalPrice")
                .containsExactly(tuple("album-001", 2000, 2, 4000));
    }

    @Test
    @DisplayName("사용자는 주문을 취소할 수 있다.")
    public void testWhenCancelOrderThenSuccess() throws Exception{
        //given
        String givenUserUid = "user-001";
        String accessToken = tokenProvider.sign(givenUserUid, new Date());
        String givenOrderUid = "order-001";

        //when
        mockMvc.perform(post("/api/order/{orderId}/cancel", givenOrderUid)
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isOk());
        //then
        Order order = orderRepository
                .findByUid(givenOrderUid).orElseThrow(() -> new IllegalArgumentException("주문이 존재하지 않습니다."));
        Account account = accountRepository.findByUid("account-001").orElseThrow(() -> new IllegalArgumentException("계정이 존재하지 않습니다."));
        Product product = productRepository.findByUid("album-001").orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));

        assertThat(order).extracting("orderStatus")
                .isEqualTo(OrderStatus.CANCELED);
        assertThat(account).extracting("balance")
                .isEqualTo(100000L + 4000L);
        assertThat(product.getStockQuantity()).isEqualTo(5 + 2);
    }



}