package jpabook.jpashop.service;

import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.UserRepository;
import jpabook.jpashop.repository.product.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@ActiveProfiles("test")
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

//
//
//    @Test
//    @DisplayName("Account에 주문 금액 이상의 잔고를 가진 유저가 특정 상품들에 대해 주문을 수행하여 상품의 재고와 계좌의 잔고를 감소시키고, DB에 관련 정보를 저장할 수 있다.")
//    void testOrder() throws Exception{
//        // given
//        Long givenBalance = 1000000L;
//
//        int movieOrderQuantity = 1;
//        int albumOrderQuantity = 2;
//        int bookOrderQuantity = 3;
//
//        List<OrderDto.OrderProductRequestInfo> orderList = List.of(
//        );
//
//        // when
//
//
//        OrderDto.Detail result = orderService.order(USER_UID, ACCOUNT_UID, orderList);
//
//
//        // then
//        Account account = getAccountByUser();
//        Order order = orderRepository.findByUidWithJoinProductAccount(result.getOrderUid()).orElseThrow(RuntimeException::new);
//
//
//        int expectedTotalPrice = MOVIE_PRICE * movieOrderQuantity + ALBUM_PRICE * albumOrderQuantity + BOOK_PRICE * bookOrderQuantity;
//
//
//        assertAll("result는 주문에 관련한 정보를 모두 담고 있어야 한다.",
//                ()->assertThat(result.getOrderStatus()).isEqualTo(OrderStatus.ORDERED),
//                ()->assertThat(result.getOrderPaymentDetail()).extracting("accountUid", "totalPrice")
//                        .contains(ACCOUNT_UID, expectedTotalPrice),
//                ()->assertThat(result).extracting("orderUid", "orderTime").doesNotContainNull(),
//                ()-> assertThat(result.getOrderProducts()).extracting("productUid", "productName","productImageUrl","unitPrice","quantity", "totalPrice")
//                        .contains(
//                                tuple(MOVIE_UID, "Inception", "http://example.com/inception.jpg", MOVIE_PRICE, movieOrderQuantity, MOVIE_PRICE * movieOrderQuantity),
//                                tuple(ALBUM_UID, "The Dark Side of the Moon", "http://example.com/darkside.jpg", ALBUM_PRICE, albumOrderQuantity, ALBUM_PRICE * albumOrderQuantity),
//                                tuple(BOOK_UID, "The Great Gatsby", "http://example.com/gatsby.jpg", BOOK_PRICE, bookOrderQuantity, BOOK_PRICE * bookOrderQuantity)
//                        )
//        );
//
//        assertAll("주문 후에 주문 금액만큼 Account의 금액이 감소되어야 한다.",
//                ()->assertThat(account.getBalance()).isEqualTo(givenBalance - expectedTotalPrice));
//
//        assertAll("주문 정보를 모두 DB에 저장해야 한다.",
//                ()->assertThat(order.getDeliveryInfo()).extracting("address", "detailedAddress").contains("address", "detailedAddress"),
//                ()->assertThat(order.getPayment()).extracting("account", "amount").contains(account, expectedTotalPrice),
//                ()->assertThat(order.getOrderProducts()).extracting("product").doesNotContainNull(),
//                ()->assertThat(order.getOrderProducts()).extracting("count", "itemPrice").contains(
//                        tuple(1, 15000),
//                        tuple(2, 20000),
//                        tuple(3, 10000)
//                ));
//    }
//
//    @Test
//    @DisplayName("상품 주문시 상품의 갯수가 부족하다면 오류를 throw하며 상품의 갯수와 계좌의 잔고가 줄어들지 않는다.")
//    void testOrderNotEnoughStock() throws Exception{
//        // given
//        Long givenBalance = 1000000L;
//        initTestDataUtils.saveAccount(givenBalance);
//
//        int movieOrderQuantity = 1000000;
//        int albumOrderQuantity = 2;
//        int bookOrderQuantity = 3;
//
//        List<OrderDto.OrderProductRequestInfo> orderList = List.of(
//                new OrderDto.OrderProductRequestInfo(MOVIE_UID , movieOrderQuantity),
//                new OrderDto.OrderProductRequestInfo(ALBUM_UID, albumOrderQuantity),
//                new OrderDto.OrderProductRequestInfo(BOOK_UID, bookOrderQuantity)
//        );
//
//        // when
//
//        ThrowableAssert.ThrowingCallable throwingCallable = ()->orderService.order(USER_UID, ACCOUNT_UID, orderList);
//
//        // then
//        assertThatThrownBy(throwingCallable)
//                .isInstanceOf(InvalidStockQuantityException.class)
//                .hasMessage(ProductExceptionMessages.NOT_ENOUGH_STOCK.getMessage());
//
//        Account account = getAccountByUser();
//        Product movie = productRepository.findByUid(MOVIE_UID)
//                .orElseThrow(RuntimeException::new);
//        Product album = productRepository.findByUid(ALBUM_UID)
//                .orElseThrow(RuntimeException::new);
//        Product book = productRepository.findByUid(BOOK_UID)
//                .orElseThrow(RuntimeException::new);
//
//
//        assertThat(account.getBalance()).isEqualTo(givenBalance);
//
//        assertAll("각 상품은 결제되기 전 초기의 수량을 가지고 있어야 한다.",
//                ()->assertThat(movie.getStockQuantity()).isEqualTo(MOVIE_INIT_STOCK),
//                ()->assertThat(album.getStockQuantity()).isEqualTo(ALBUM_INIT_STOCK),
//                ()->assertThat(book.getStockQuantity()).isEqualTo(BOOK_INIT_STOCK));
//    }
//
//
//    @Test
//    @DisplayName("상품 주문시 계좌의 잔고가 부족하다면 오류를 throw하며 상품의 갯수와 계좌의 잔고가 줄어들지 않는다.")
//    void testOrderNotEnoughMoney() throws Exception{
//        // given
//        Long givenBalance = 1L;
//        initTestDataUtils.saveAccount(givenBalance);
//
//        int movieOrderQuantity = 1;
//        int albumOrderQuantity = 2;
//        int bookOrderQuantity = 3;
//
//        List<OrderDto.OrderProductRequestInfo> orderList = List.of(
//                new OrderDto.OrderProductRequestInfo(MOVIE_UID , movieOrderQuantity),
//                new OrderDto.OrderProductRequestInfo(ALBUM_UID, albumOrderQuantity),
//                new OrderDto.OrderProductRequestInfo(BOOK_UID, bookOrderQuantity)
//        );
//        // when
//        ThrowableAssert.ThrowingCallable throwingCallable = ()->orderService.order(USER_UID, ACCOUNT_UID, orderList);
//
//        // then
//        assertThatThrownBy(throwingCallable)
//                .isInstanceOf(InvalidBalanceValueException.class)
//                .hasMessage(AccountExceptionMessages.NEGATIVE_ACCOUNT_BALANCE.getMessage());
//
//        Account account = getAccountByUser();
//        Product movie = productRepository.findByUid(MOVIE_UID)
//                .orElseThrow(RuntimeException::new);
//        Product album = productRepository.findByUid(ALBUM_UID)
//                .orElseThrow(RuntimeException::new);
//        Product book = productRepository.findByUid(BOOK_UID)
//                .orElseThrow(RuntimeException::new);
//
//
//        assertThat(account.getBalance()).isEqualTo(givenBalance);
//
//        assertAll("각 상품은 결제되기 전 초기의 수량을 가지고 있어야 한다.",
//                ()->assertThat(movie.getStockQuantity()).isEqualTo(MOVIE_INIT_STOCK),
//                ()->assertThat(album.getStockQuantity()).isEqualTo(ALBUM_INIT_STOCK),
//                ()->assertThat(book.getStockQuantity()).isEqualTo(BOOK_INIT_STOCK));
//    }
//
//
//    @Test
//    @DisplayName("동시에 여러개의 주문 요청을 보낼 시, 동시성이 보장되어 물품의 갯수가 알맞게 유지되어야 한다.")
//    void testOrderMultithread() throws Exception{
//        // given
//        Long givenBalance = 1000000000L;
//
//        initTestDataUtils.saveAccount(givenBalance);
//
//        int movieOrderQuantity = 1;
//        int albumOrderQuantity = 1;
//        int bookOrderQuantity = 1;
//
//        List<OrderDto.OrderProductRequestInfo> orderList = List.of(
//                new OrderDto.OrderProductRequestInfo(MOVIE_UID , movieOrderQuantity),
//                new OrderDto.OrderProductRequestInfo(ALBUM_UID, albumOrderQuantity),
//                new OrderDto.OrderProductRequestInfo(BOOK_UID, bookOrderQuantity)
//        );
//
//        int threadSize = 50;
//        ExecutorService executorService = Executors.newFixedThreadPool(threadSize);
//        CountDownLatch countDownLatch = new CountDownLatch(threadSize);
//        // when
//        for(int i = 0 ; i < threadSize; i++){
//            executorService.execute(()-> {
//                try {
//                    orderService.order(USER_UID, ACCOUNT_UID, orderList);
//                } catch (Exception e){
//                    fail("모든 요청이 정상수행되어야 한다.");
//                }finally {
//                    countDownLatch.countDown();
//                }
//            });
//        }
//        countDownLatch.await();
//        executorService.shutdown();
//
//
//        // then
//        Account account = getAccountByUser();
//        Product movie = productRepository.findByUid(MOVIE_UID)
//                .orElseThrow(RuntimeException::new);
//        Product album = productRepository.findByUid(ALBUM_UID)
//                .orElseThrow(RuntimeException::new);
//        Product book = productRepository.findByUid(BOOK_UID)
//                .orElseThrow(RuntimeException::new);
//
//        Long expectedBalance = givenBalance - (MOVIE_PRICE + ALBUM_PRICE + BOOK_PRICE) * threadSize;
//        assertThat(account.getBalance()).isEqualTo(expectedBalance);
//
//        assertAll("각 상품은 결제되기 전 초기의 수량을 가지고 있어야 한다.",
//                ()->assertThat(movie.getStockQuantity()).isEqualTo(MOVIE_INIT_STOCK - threadSize),
//                ()->assertThat(album.getStockQuantity()).isEqualTo(ALBUM_INIT_STOCK- threadSize),
//                ()->assertThat(book.getStockQuantity()).isEqualTo(BOOK_INIT_STOCK - threadSize));
//
//    }
//
//    @Test
//    @DisplayName("주문을 취소할시 주문되었던 금액과 상품의 갯수가 다시 반환되고, 주문의 Status가 CANCELED로 변경된다.")
//    void testOrderCancel() throws Exception{
//        // given
//        Long givenBalance = 100000L;
//
//        initTestDataUtils.saveAccount(givenBalance);
//
//        // MOVIE, ALBUM, BOOK을 각 한개씩 주문했다고 가정
//        initTestDataUtils.saveOrder();
//        // when
//        orderService.cancel(ORDER_UID);
//        // then
//        Order order = orderRepository.findByUidWithJoinProductAccount(ORDER_UID)
//                .orElseThrow(RuntimeException::new);
//
//        Product movie = productRepository.findByUid(MOVIE_UID)
//                .orElseThrow(RuntimeException::new);
//
//        Product album = productRepository.findByUid(ALBUM_UID)
//                .orElseThrow(RuntimeException::new);
//
//        Product book = productRepository.findByUid(BOOK_UID)
//                .orElseThrow(RuntimeException::new);
//
//        Account account = userRepository.findByUidJoinAccount(USER_UID)
//                .orElseThrow(RuntimeException::new).getAccountList().get(0);
//
//        assertAll("상품의 갯수는 초기상태를 유지해야 한다.",
//                ()->assertThat(movie.getStockQuantity()).isEqualTo(MOVIE_INIT_STOCK),
//                ()->assertThat(album.getStockQuantity()).isEqualTo(ALBUM_INIT_STOCK),
//                ()->assertThat(book.getStockQuantity()).isEqualTo(BOOK_INIT_STOCK)
//        );
//
//        assertThat(account.getBalance()).isEqualTo(givenBalance);
//        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELED);
//
//    }
//
//    @Test
//    @DisplayName("사용자의 주문 리스트 조회시 사용자가 주문했던 리스트가 조회된다.")
//    void testFindUsersOrder() throws Exception{
//        // given
//        initTestDataUtils.saveAccount(100000L);
//        initTestDataUtils.saveOrder();
//        // when
//        PaginationListDto<OrderDto.Preview> result = orderService.findByUser(USER_UID, PageRequest.of(0, 10));
//        // then
//        assertAll("주문 리스트 정보는 UID, 물품 요약, 총액, 상태, 주문시간 정보를 모두 담고 있어야 한다.",
//                ()->assertThat(result.getData()).extracting("orderUid",  "totalPrice", "orderStatus")
//                        .contains(tuple("order-001", 45000, OrderStatus.ORDERED)),
//                ()->assertThat(result.getData().get(0).getOrderTime()).isNotNull(),
//                ()->assertThat(result.getData().get(0).getName()).contains("외 2건")
//        );
//    }
//
//    @Test
//    @DisplayName("ORDER의 고유식별자로 ORDER의 상세정보를 조회할 수 있다.")
//    public void testFindByOrderUid() throws Exception{
//        //given
//
//        int orderedQuantity= 1;
//        initTestDataUtils.saveAccount(1000000L);
//        initTestDataUtils.saveOrder();
//
//        //when
//        OrderDto.Detail result = orderService.findByOrderId(ORDER_UID);
//
//        //then
//        int expectedTotalPrice = MOVIE_PRICE + ALBUM_PRICE + BOOK_PRICE;
//
//        assertAll("result는 주문에 관련한 정보를 모두 담고 있어야 한다.",
//                ()->assertThat(result.getOrderStatus()).isEqualTo(OrderStatus.ORDERED),
//                ()->assertThat(result.getOrderPaymentDetail()).extracting("accountUid", "totalPrice")
//                        .contains(ACCOUNT_UID, expectedTotalPrice),
//                ()->assertThat(result).extracting("orderUid", "orderTime").doesNotContainNull(),
//                ()-> assertThat(result.getOrderProducts()).extracting("productUid", "productName","productImageUrl","unitPrice","quantity", "totalPrice")
//                        .contains(
//                                tuple(MOVIE_UID, "Inception", "http://example.com/inception.jpg", MOVIE_PRICE, orderedQuantity, MOVIE_PRICE * orderedQuantity),
//                                tuple(ALBUM_UID, "The Dark Side of the Moon", "http://example.com/darkside.jpg", ALBUM_PRICE, orderedQuantity, ALBUM_PRICE * orderedQuantity),
//                                tuple(BOOK_UID, "The Great Gatsby", "http://example.com/gatsby.jpg", BOOK_PRICE, orderedQuantity, BOOK_PRICE * orderedQuantity)
//                        )
//        );
//    }
//
//
//    private Account getAccountByUser() {
//        Account account = userRepository.findByUidJoinAccount(USER_UID).orElseThrow(RuntimeException::new)
//                .getAccountList().get(0);
//        return account;
//    }
//
//    @TestConfiguration
//    public static class TestConfig{
//
//
//        @Bean
//        public InitTestDataUtils initDbUtils(
//                UserRepository userRepository,
//                ProductRepository productRepository,
//                OrderRepository orderRepository,
//                AccountRepository accountRepository
//        ){
//            return new InitTestDataUtils(productRepository, userRepository, orderRepository, accountRepository);
//        }
//    }

}