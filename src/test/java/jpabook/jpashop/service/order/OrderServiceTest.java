package jpabook.jpashop.service.order;

import jpabook.jpashop.domain.order.Order;
import jpabook.jpashop.domain.order.OrderStatus;
import jpabook.jpashop.domain.product.Product;
import jpabook.jpashop.domain.user.Account;
import jpabook.jpashop.dto.OrderDto;
import jpabook.jpashop.dto.PaginationListDto;
import jpabook.jpashop.exception.product.InvalidStockQuantityException;
import jpabook.jpashop.exception.product.ProductExceptionMessages;
import jpabook.jpashop.exception.user.account.AccountExceptionMessages;
import jpabook.jpashop.exception.user.account.InvalidBalanceValueException;
import jpabook.jpashop.repository.AccountRepository;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.product.ProductRepository;
import jpabook.jpashop.service.OrderService;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static jpabook.jpashop.testUtils.TestDataUtils.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = {"classpath:init-product-test-data.sql", "classpath:init-user-test-data.sql", "classpath:init-cart-test-data.sql", "classpath:init-order-test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"classpath:clean-up.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ProductRepository productRepository;



    @Test
    @DisplayName("Account에 주문 금액 이상의 잔고를 가진 유저가 특정 상품들에 대해 주문을 수행하여 상품의 재고와 계좌의 잔고를 감소시키고, DB에 관련 정보를 저장할 수 있다.")
    void testOrder() throws Exception{
        // given
        Long givenBalance = account1.getBalance();
        String givenUserUid = user1.getUid();
        String givenAccountUid = account1.getUid();

        int movieOrderQuantity = 1;
        int albumOrderQuantity = 2;
        int bookOrderQuantity = 3;

        List<OrderDto.OrderProductRequestInfo> orderList = List.of(
                new OrderDto.OrderProductRequestInfo(movie.getUid() , movieOrderQuantity),
                new OrderDto.OrderProductRequestInfo(album.getUid(), albumOrderQuantity),
                new OrderDto.OrderProductRequestInfo(book.getUid(), bookOrderQuantity)
        );

        // when

        OrderDto.Detail result = orderService.order(givenUserUid, givenAccountUid, orderList);


        // then
        Account account = accountRepository.findByUid(account1.getUid())
                .orElseThrow(RuntimeException::new);


        Order order = orderRepository.findByUidWithJoinProductAccount(result.getOrderUid()).orElseThrow(RuntimeException::new);


        int expectedTotalPrice = movie.getPrice() * movieOrderQuantity + album.getPrice() * albumOrderQuantity + book.getPrice() * bookOrderQuantity;


        assertAll("result는 주문에 관련한 정보를 모두 담고 있어야 한다.",
                ()->assertThat(result).extracting("orderStatus", "orderPaymentDetail.accountUid", "orderPaymentDetail.totalPrice")
                        .contains( OrderStatus.ORDERED, givenAccountUid, expectedTotalPrice ),
                ()->assertThat(result).extracting("orderUid", "orderTime").doesNotContainNull(),
                ()-> assertThat(result.getOrderProducts()).extracting("productUid", "productName","productImageUrl","unitPrice","quantity", "totalPrice")
                        .contains(
                                tuple(movie.getUid(), movie.getName(), movie.getThumbnailImageUrl(), movie.getPrice(), movieOrderQuantity, movie.getPrice() * movieOrderQuantity),
                                tuple(album.getUid(), album.getName(), album.getThumbnailImageUrl(), album.getPrice(), albumOrderQuantity, album.getPrice() * albumOrderQuantity),
                                tuple(book.getUid(), book.getName(), book.getThumbnailImageUrl(), book.getPrice(), bookOrderQuantity, book.getPrice() * bookOrderQuantity)
                        )
        );

        assertAll("주문 후에 주문 금액만큼 Account의 금액이 감소되어야 한다.",
                ()->assertThat(account.getBalance()).isEqualTo(givenBalance - expectedTotalPrice));

        assertAll("주문 정보를 모두 DB에 저장해야 한다.",
                ()->assertThat(order.getDeliveryInfo()).extracting("address", "detailedAddress").contains(user1.getAddressInfo().getAddress(), user1.getAddressInfo().getDetailedAddress()),
                ()->assertThat(order.getPayment()).extracting("account", "amount").contains(account, expectedTotalPrice),
                ()->assertThat(order.getOrderProducts()).extracting("product").doesNotContainNull(),
                ()->assertThat(order.getOrderProducts()).extracting("count", "itemPrice").contains(
                        tuple(movieOrderQuantity, movie.getPrice()),
                        tuple(albumOrderQuantity, album.getPrice()),
                        tuple(bookOrderQuantity, book.getPrice())
                ));
    }

    @Test
    @DisplayName("상품 주문시 상품의 갯수가 부족하다면 오류를 throw하며 상품의 갯수와 계좌의 잔고가 줄어들지 않는다.")
    void testOrderNotEnoughStock() throws Exception{
        // given
        Long givenBalance = account1.getBalance();

        int movieOrderQuantity = 1000000;
        int albumOrderQuantity = 2;
        int bookOrderQuantity = 3;

        List<OrderDto.OrderProductRequestInfo> orderList = List.of(
                new OrderDto.OrderProductRequestInfo(movie.getUid() , movieOrderQuantity),
                new OrderDto.OrderProductRequestInfo(album.getUid(), albumOrderQuantity),
                new OrderDto.OrderProductRequestInfo(book.getUid(), bookOrderQuantity)
        );

        // when

        ThrowableAssert.ThrowingCallable throwingCallable = ()->orderService.order(user1.getUid(), account1.getUid(), orderList);

        // then
        assertThatThrownBy(throwingCallable)
                .isInstanceOf(InvalidStockQuantityException.class)
                .hasMessage(ProductExceptionMessages.NOT_ENOUGH_STOCK.getMessage());

        Account account = accountRepository.findByUid(account1.getUid())
                .orElseThrow(RuntimeException::new);
        Product actualMovie = productRepository.findByUid(movie.getUid())
                .orElseThrow(RuntimeException::new);
        Product actualAlbum = productRepository.findByUid(album.getUid())
                .orElseThrow(RuntimeException::new);
        Product actualBook = productRepository.findByUid(book.getUid())
                .orElseThrow(RuntimeException::new);


        assertThat(account.getBalance()).isEqualTo(givenBalance);

        assertAll("각 상품은 결제되기 전 초기의 수량을 가지고 있어야 한다.",
                ()->assertThat(actualMovie.getStockQuantity()).isEqualTo(movie.getStockQuantity()),
                ()->assertThat(actualAlbum.getStockQuantity()).isEqualTo(album.getStockQuantity()),
                ()->assertThat(actualBook.getStockQuantity()).isEqualTo(book.getStockQuantity()));
    }


    @Test
    @DisplayName("상품 주문시 계좌의 잔고가 부족하다면 오류를 throw하며 상품의 갯수와 계좌의 잔고가 줄어들지 않는다.")
    void testOrderNotEnoughMoney() throws Exception{
        // given
        Long givenBalance = account2.getBalance();

        int movieOrderQuantity = 1;
        int albumOrderQuantity = 2;
        int bookOrderQuantity = 3;

        String givenMovieUid = movie.getUid();
        String givenAlbumUid = album.getUid();
        String givenBookUid = book.getUid();


        String givenUserUid = user1.getUid();
        String givenAccountUid = account2.getUid();


        List<OrderDto.OrderProductRequestInfo> orderList = List.of(
                new OrderDto.OrderProductRequestInfo(givenMovieUid, movieOrderQuantity),
                new OrderDto.OrderProductRequestInfo(givenAlbumUid, albumOrderQuantity),
                new OrderDto.OrderProductRequestInfo(givenBookUid, bookOrderQuantity)
        );
        // when
        ThrowableAssert.ThrowingCallable throwingCallable = ()->orderService.order(givenUserUid, givenAccountUid, orderList);

        // then
        assertThatThrownBy(throwingCallable)
                .isInstanceOf(InvalidBalanceValueException.class)
                .hasMessage(AccountExceptionMessages.NEGATIVE_ACCOUNT_BALANCE.getMessage());

        Account account = accountRepository.findByUid(givenAccountUid)
                .orElseThrow(RuntimeException::new);

        Product actualMovie = productRepository.findByUid(givenMovieUid)
                .orElseThrow(RuntimeException::new);
        Product actualAlbum = productRepository.findByUid(givenAlbumUid)
                .orElseThrow(RuntimeException::new);
        Product actualBook = productRepository.findByUid(givenBookUid)
                .orElseThrow(RuntimeException::new);


        assertThat(account.getBalance()).isEqualTo(givenBalance);

        assertAll("각 상품은 결제되기 전 초기의 수량을 가지고 있어야 한다.",
                ()->assertThat(actualMovie.getStockQuantity()).isEqualTo(movie.getStockQuantity()),
                ()->assertThat(actualAlbum.getStockQuantity()).isEqualTo(album.getStockQuantity()),
                ()->assertThat(actualBook.getStockQuantity()).isEqualTo(book.getStockQuantity()));
    }

    @Test
    @DisplayName("주문을 취소할시 주문되었던 금액과 상품의 갯수가 다시 반환되고, 주문의 Status가 CANCELED로 변경된다.")
    void testOrderCancel() throws Exception{
        // given
        Long givenBalance = account1.getBalance();
        String givenOrderUid = order1.getUid();

        String givenMovieUid = movie.getUid();
        String givenAlbumUid = album.getUid();
        String givenBookUid = book.getUid();

        // when
        orderService.cancel(givenOrderUid);
        // then
        Order order = orderRepository.findByUidWithJoinProductAccount(givenOrderUid)
                .orElseThrow(RuntimeException::new);

        Product actualMovie = productRepository.findByUid(givenMovieUid)
                .orElseThrow(RuntimeException::new);

        Product actualAlbum = productRepository.findByUid(givenAlbumUid)
                .orElseThrow(RuntimeException::new);

        Product actualBook = productRepository.findByUid(givenBookUid)
                .orElseThrow(RuntimeException::new);

        Account account = accountRepository.findByUid(account1.getUid())
                .orElseThrow(RuntimeException::new);

        assertAll("상품의 갯수는 초기상태를 유지해야 한다.",
                ()->assertThat(actualMovie.getStockQuantity()).isEqualTo(movie.getStockQuantity()),
                ()->assertThat(actualAlbum.getStockQuantity()).isEqualTo(album.getStockQuantity() + orderProduct1.getCount()),
                ()->assertThat(actualBook.getStockQuantity()).isEqualTo(book.getStockQuantity())
        );

        assertThat(account.getBalance()).isEqualTo(givenBalance + order.getPayment().getAmount());
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELED);

    }

    @Test
    @DisplayName("사용자의 주문 리스트 조회시 사용자가 주문했던 리스트가 조회된다.")
    void testFindUsersOrder() throws Exception{
        // given
        // when
        PaginationListDto<OrderDto.Preview> result = orderService.findByUser(user1.getUid(), PageRequest.of(0, 10));
        // then
        assertAll("주문 리스트 정보는 UID, 물품 요약, 총액, 상태, 주문시간 정보를 모두 담고 있어야 한다.",
                ()->assertThat(result.getData()).extracting("orderUid", "name", "totalPrice", "orderTime", "orderStatus")
                        .contains(tuple(order1.getUid(), "Album Name외 0건", order1.getPayment().getAmount(), order1.getCreatedAt(), OrderStatus.ORDERED),
                                tuple(order2.getUid(), "Book Name외 0건", order2.getPayment().getAmount(), order2.getCreatedAt(), OrderStatus.CANCELED))
        );
    }

    @Test
    @DisplayName("ORDER의 고유식별자로 ORDER의 상세정보를 조회할 수 있다.")
    public void testFindByOrderUid() throws Exception{
        //given
        String givenAlbumUid = album.getUid();


        int albumOrderQuantity = orderProduct1.getCount();

        //when
        OrderDto.Detail result = orderService.findByOrderId(order1.getUid());

        //then
        int expectedTotalPrice = orderProduct1.calculateTotalItemPrice();

        assertAll("result는 주문에 관련한 정보를 모두 담고 있어야 한다.",
                ()->assertThat(result.getOrderStatus()).isEqualTo(OrderStatus.ORDERED),
                ()->assertThat(result.getOrderPaymentDetail()).extracting("accountUid", "totalPrice")
                        .contains(account1.getUid() , expectedTotalPrice),
                ()->assertThat(result).extracting("orderUid", "orderTime").doesNotContainNull(),
                ()-> assertThat(result.getOrderProducts()).extracting("productUid", "productName","productImageUrl","unitPrice","quantity", "totalPrice")
                        .contains(
                                tuple(givenAlbumUid, album.getName(), album.getThumbnailImageUrl(), orderProduct1.getItemPrice(), albumOrderQuantity, orderProduct1.getItemPrice() * albumOrderQuantity)
                        )
        );
    }
    @Test
    @DisplayName("동시에 여러개의 주문 요청을 보낼 시, 동시성이 보장되어 물품의 갯수가 알맞게 유지되어야 한다.")
    void testOrderMultithread() throws Exception{
        // given
        String givenMovieUid = movie.getUid();
        String givenAlbumUid = album.getUid();
        String givenBookUid = book.getUid();

        String givenUserUid = user1.getUid();
        String givenAccountUid = account1.getUid();

        int movieOrderQuantity = 1;
        int albumOrderQuantity = 1;
        int bookOrderQuantity = 1;

        List<OrderDto.OrderProductRequestInfo> orderList = List.of(
                new OrderDto.OrderProductRequestInfo(givenMovieUid , movieOrderQuantity),
                new OrderDto.OrderProductRequestInfo(givenAlbumUid, albumOrderQuantity),
                new OrderDto.OrderProductRequestInfo(givenBookUid, bookOrderQuantity)
        );

        int threadSize = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(threadSize);
        CountDownLatch countDownLatch = new CountDownLatch(threadSize);
        // when
        for(int i = 0 ; i < threadSize; i++){
            executorService.execute(()-> {
                try {
                    orderService.order(givenUserUid, givenAccountUid, orderList);
                } catch (Exception e){
                    fail("모든 요청이 정상수행되어야 한다.");
                }finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        executorService.shutdown();


        // then
        Account account = accountRepository.findByUid(givenAccountUid)
                .orElseThrow(RuntimeException::new);
        Product actualMovie = productRepository.findByUid(givenMovieUid)
                .orElseThrow(RuntimeException::new);
        Product actualAlbum = productRepository.findByUid(givenAlbumUid)
                .orElseThrow(RuntimeException::new);
        Product actualBook = productRepository.findByUid(givenBookUid)
                .orElseThrow(RuntimeException::new);

        Long expectedBalance = account1.getBalance() - (actualMovie.getPrice() + actualAlbum.getPrice() + actualBook.getPrice()) * threadSize;
        assertThat(account.getBalance()).isEqualTo(expectedBalance);

        assertAll("각 상품은 결제가 완료되어 반영된 갯수를 가지고 있어야 한다.",
                ()->assertThat(actualMovie.getStockQuantity()).isEqualTo(movie.getStockQuantity() - threadSize),
                ()->assertThat(actualAlbum.getStockQuantity()).isEqualTo(album.getStockQuantity()- threadSize),
                ()->assertThat(actualBook.getStockQuantity()).isEqualTo(book.getStockQuantity() - threadSize));

    }

}