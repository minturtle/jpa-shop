package jpabook.jpashop.controller.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import jpabook.jpashop.controller.common.request.OrderRequest;
import jpabook.jpashop.controller.common.response.OrderResponse;
import jpabook.jpashop.domain.order.OrderStatus;
import jpabook.jpashop.domain.product.Product;
import jpabook.jpashop.domain.user.Account;
import jpabook.jpashop.domain.user.User;
import jpabook.jpashop.repository.AccountRepository;
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
@Sql(scripts = {"classpath:init-product-test-data.sql", "classpath:init-user-test-data.sql", "classpath:init-cart-test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
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

        assertThat(album.getStockQuantity()).isEqualTo(5);
        assertThat(book.getStockQuantity()).isEqualTo(20);
        assertThat(movie.getStockQuantity()).isEqualTo(8);
    }


}