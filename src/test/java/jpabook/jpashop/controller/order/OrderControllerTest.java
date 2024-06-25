package jpabook.jpashop.controller.order;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jpabook.jpashop.controller.api.common.request.OrderRequest;
import jpabook.jpashop.controller.api.common.response.OrderResponse;
import jpabook.jpashop.domain.order.Order;
import jpabook.jpashop.domain.order.OrderProduct;
import jpabook.jpashop.domain.order.OrderStatus;
import jpabook.jpashop.domain.product.Cart;
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

import java.util.Date;
import java.util.List;

import static jpabook.jpashop.testUtils.TestDataUtils.*;
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
    public void given_AuthenticatedUserAccountProduct_when_Order_then_Success() throws Exception{
        //given
        String givenUserUid = user1.getUid();
        Account givenAccount = account1;
        Product givenProduct = album;


        String accessToken = tokenProvider.sign(givenUserUid, new Date());

        int givenOrderQuantity = 2;


        OrderRequest.Create orderRequest = new OrderRequest.Create(
                givenAccount.getUid(),
                List.of(
                        new OrderRequest.ProductOrderInfo(givenProduct.getUid(), givenOrderQuantity)
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
        Product product = productRepository.findByUid(givenProduct.getUid()).orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));
        Account account = accountRepository.findByUid(givenAccount.getUid()).orElseThrow(() -> new IllegalArgumentException("계정이 존재하지 않습니다."));

        assertAll("주문 완료후 주문 정보는 관련 정보를 모두 담고 있어야 한다.",
                ()->assertThat(actual.getOrderUid()).isNotNull(),
                ()->assertThat(actual.getOrderStatus()).isEqualTo(OrderStatus.ORDERED),
                ()->assertThat(actual.getOrderTime()).isNotNull(),
                ()->assertThat(actual.getOrderPaymentDetail())
                        .extracting("accountUid", "totalPrice")
                        .containsExactly(givenAccount.getUid(), givenProduct.getPrice() * givenOrderQuantity),
                ()->assertThat(actual.getOrderProducts())
                        .extracting("productUid","unitPrice", "quantity", "totalPrice")
                        .containsExactly(tuple(givenProduct.getUid(), givenProduct.getPrice(), givenOrderQuantity, givenProduct.getPrice() * givenOrderQuantity))
        );

        assertThat(product.getStockQuantity()).isEqualTo(givenProduct.getStockQuantity() - givenOrderQuantity);
        assertThat(account.getBalance()).isEqualTo(givenAccount.getBalance() - givenProduct.getPrice() * givenOrderQuantity);

    }
    @Test
    @DisplayName("사용자는 장바구니에 담긴 상품을 주문하면 Account의 잔액과 상품의 재고가 감소하고, 주문이 생성되며 장바구니에 담긴 해당 물건은 삭제된다.")
    public void given_AuthenticatedUserAccountCart_when_OrderInCart_then_SuccessAndRemoveCart() throws Exception{
        //given
        String givenUserUid = user1.getUid();
        Account givenAccount = account1;

        Cart givenCart1 = cart1;
        Cart givenCart2 = cart2;

        String accessToken = tokenProvider.sign(givenUserUid, new Date());

        OrderRequest.Create orderRequest = new OrderRequest.Create(
                givenAccount.getUid(),
                List.of(
                        new OrderRequest.ProductOrderInfo(givenCart1.getProduct().getUid(), givenCart1.getQuantity()),
                        new OrderRequest.ProductOrderInfo(givenCart2.getProduct().getUid(), givenCart2.getQuantity())
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
        User actualUser = userRepository.findByUid(givenUserUid).orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));
        Product actualProduct1 = productRepository.findByUid(givenCart1.getProduct().getUid()).orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));
        Product actualProduct2 = productRepository.findByUid(givenCart2.getProduct().getUid()).orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));
        Account actualAccount = accountRepository.findByUid(givenAccount.getUid()).orElseThrow(() -> new IllegalArgumentException("계좌가 존재하지 않습니다."));

        int expectedStockQuantity1 = givenCart1.getProduct().getStockQuantity() - givenCart1.getQuantity();
        int expectedStockQuantity2 = givenCart2.getProduct().getStockQuantity() - givenCart2.getQuantity();
        long expectedBalance = givenAccount.getBalance() - (givenCart1.getProduct().getPrice() * givenCart1.getQuantity() + givenCart2.getProduct().getPrice() * givenCart2.getQuantity());


        assertThat(actualUser.getCartList()).isEmpty();
        assertThat(actualProduct1.getStockQuantity()).isEqualTo(expectedStockQuantity1);
        assertThat(actualProduct2.getStockQuantity()).isEqualTo(expectedStockQuantity2);
        assertThat(actualAccount.getBalance()).isEqualTo(expectedBalance);

    }

    @Test
    @DisplayName("사용자가 상품의 남은 재고 이상을 주문하면 주문이 실패한다.")
    public void given_AuthenticatedUserNotEnoughStockProduct_when_Order_then_Failed() throws Exception{
        //given
        String givenUserUid = user1.getUid();
        Account givenAccount = account1;
        Product givenProduct = album;
        int givenOrderQuantity = 1000000;

        String accessToken = tokenProvider.sign(givenUserUid, new Date());


        OrderRequest.Create orderRequest = new OrderRequest.Create(
                givenAccount.getUid(),
                List.of(
                        new OrderRequest.ProductOrderInfo(givenProduct.getUid(), givenOrderQuantity)
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
        Product actualAlbum = productRepository.findByUid(givenProduct.getUid()).orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));
        Account actualAccount = accountRepository.findByUid(givenAccount.getUid()).orElseThrow(() -> new IllegalArgumentException("계좌가 존재하지 않습니다."));


        assertThat(actualAlbum.getStockQuantity()).isEqualTo(givenProduct.getStockQuantity());
        assertThat(actualAccount.getBalance()).isEqualTo(givenAccount.getBalance());
    }

    @Test
    @DisplayName("사용자가 잔액보다 많은 금액을 주문하면 주문이 실패한다.")
    public void given_AuthenticatedUserNotEnoughAccount_when_Order_then_Failed() throws Exception{
        //given
        String givenUserUid = user1.getUid();
        Account givenAccount = account2;
        Product givenProduct = album;


        String accessToken = tokenProvider.sign(givenUserUid, new Date());
        int givenOrderQuantity = 10;

        OrderRequest.Create orderRequest = new OrderRequest.Create(
                givenAccount.getUid(),
                List.of(
                        new OrderRequest.ProductOrderInfo(givenProduct.getUid(), givenOrderQuantity)
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
        Product actualProduct = productRepository.findByUid(givenProduct.getUid()).orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));
        Account actualAccount = accountRepository.findByUid(givenAccount.getUid()).orElseThrow(() -> new IllegalArgumentException("계좌가 존재하지 않습니다."));


        assertThat(actualProduct.getStockQuantity()).isEqualTo(givenProduct.getStockQuantity());
        assertThat(actualAccount.getBalance()).isEqualTo(givenAccount.getBalance());
    }

    @Test
    @DisplayName("사용자는 자신의 주문 기록 리스트를 조회할 수 있다.")
    public void given_AuthenticatedUser_when_GetOrderHistoryList_then_Return() throws Exception{
        //given
        String givenUserUid = user1.getUid();

        String accessToken = tokenProvider.sign(givenUserUid, new Date());
        //when
        MvcResult mvcResult = mockMvc.perform(get("/api/order/list")
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        //then
        PaginationListDto<OrderResponse.Preview> actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<PaginationListDto<OrderResponse.Preview>>(){});

        Order expectedOrder1 = order1;
        Order expectedOrder2 = order2;

        assertThat(actual.getCount()).isEqualTo(2);
        assertThat(actual.getData())
                .extracting("orderUid", "totalPrice", "orderStatus", "orderTime")
                .containsExactly(
                        tuple(expectedOrder1.getUid(), expectedOrder1.getPayment().getAmount(), expectedOrder1.getStatus(), expectedOrder1.getCreatedAt()),
                        tuple(expectedOrder2.getUid(), expectedOrder2.getPayment().getAmount(), expectedOrder2.getStatus(), expectedOrder2.getCreatedAt())
                );
        assertThat(actual.getData()).extracting("name")
                .doesNotContainNull();


    }

    @Test
    @DisplayName("사용자는 주문 상세 정보를 조회할 수 있다.")
    public void given_AuthenticatedUserOrder_when_GetOrderDetail_then_ReturnDetailOrderInfo() throws Exception{
        //given
        String givenUserUid = user1.getUid();
        String givenOrderUid = order1.getUid();

        String accessToken = tokenProvider.sign(givenUserUid, new Date());

        //when
        MvcResult mvcResult = mockMvc.perform(get("/api/order/{orderId}", givenOrderUid)
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        //then
        OrderResponse.Detail actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), OrderResponse.Detail.class);

        Order expectedOrder = order1;
        OrderProduct expectedOrderProduct = orderProduct1;

        assertThat(actual).extracting(
                "orderUid",
                        "orderStatus",
                        "orderTime",
                        "orderPaymentDetail.accountUid",
                        "orderPaymentDetail.totalPrice"
                )
                .containsExactly(
                        expectedOrder.getUid(),
                        expectedOrder.getStatus(),
                        expectedOrder.getCreatedAt(),
                        expectedOrder.getPayment().getAccount().getUid(),
                        expectedOrder.getPayment().getAmount()
                );
        assertThat(actual.getOrderProducts())
                .extracting(
                        "productUid",
                        "unitPrice",
                        "quantity",
                        "totalPrice"
                )
                .containsExactly(
                        tuple(
                                expectedOrderProduct.getProduct().getUid(),
                                expectedOrderProduct.getItemPrice(),
                                expectedOrderProduct.getCount(),
                                expectedOrderProduct.calculateTotalItemPrice()
                        )
                );
    }

    @Test
    @DisplayName("사용자는 주문을 취소할 수 있다.")
    public void given_AuthenticatedUserOrder_when_CancelOrder_then_Success() throws Exception{
        //given
        String givenUserUid = user1.getUid();
        String accessToken = tokenProvider.sign(givenUserUid, new Date());
        String givenOrderUid = order1.getUid();
        Account givenAccount = order1.getPayment().getAccount();
        Product givenProduct = orderProduct1.getProduct();

        //when
        mockMvc.perform(post("/api/order/{orderId}/cancel", givenOrderUid)
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isOk());
        //then
        Order actualOrder = orderRepository
                .findByUid(givenOrderUid).orElseThrow(() -> new IllegalArgumentException("주문이 존재하지 않습니다."));
        Account actualAccount = accountRepository.findByUid(givenAccount.getUid()).orElseThrow(() -> new IllegalArgumentException("계정이 존재하지 않습니다."));
        Product actualProduct = productRepository.findByUid(givenProduct.getUid()).orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));

        assertThat(actualOrder).extracting("status")
                .isEqualTo(OrderStatus.CANCELED);

        assertThat(actualAccount).extracting("balance")
                .isEqualTo(givenAccount.getBalance() + order1.getPayment().getAmount());

        assertThat(actualProduct.getStockQuantity())
                .isEqualTo(givenProduct.getStockQuantity() + orderProduct1.getCount());
    }



}