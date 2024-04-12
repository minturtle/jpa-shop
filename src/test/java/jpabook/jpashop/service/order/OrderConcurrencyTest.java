package jpabook.jpashop.service.order;

import jpabook.jpashop.domain.product.Product;
import jpabook.jpashop.domain.user.Account;
import jpabook.jpashop.dto.OrderDto;
import jpabook.jpashop.repository.AccountRepository;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.product.ProductRepository;
import jpabook.jpashop.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static jpabook.jpashop.testUtils.TestDataUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertAll;


@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = {"classpath:init-product-test-data.sql", "classpath:init-user-test-data.sql", "classpath:init-cart-test-data.sql", "classpath:init-order-test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"classpath:clean-up.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class OrderConcurrencyTest {
    @Autowired
    private OrderService orderService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ProductRepository productRepository;




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
