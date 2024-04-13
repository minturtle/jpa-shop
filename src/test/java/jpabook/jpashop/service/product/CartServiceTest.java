package jpabook.jpashop.service.product;

import jpabook.jpashop.domain.product.Cart;
import jpabook.jpashop.domain.product.Album;
import jpabook.jpashop.domain.product.Book;
import jpabook.jpashop.domain.product.Movie;
import jpabook.jpashop.domain.product.Product;
import jpabook.jpashop.domain.user.User;
import jpabook.jpashop.dto.CartDto;
import jpabook.jpashop.repository.UserRepository;
import jpabook.jpashop.repository.product.ProductRepository;
import jpabook.jpashop.service.CartService;
import jpabook.jpashop.testUtils.TestDataUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static jpabook.jpashop.testUtils.TestDataUtils.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = {"classpath:init-product-test-data.sql", "classpath:init-user-test-data.sql", "classpath:init-cart-test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"classpath:clean-up.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class CartServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartService cartService;

    @Test
    @DisplayName("특정 유저가 상품의 식별자와 갯수를 입력해 해당 상품을 장바구니에 추가해 저장할 수 있다.")
    void testAddCarts() throws Exception{
        // given
        String givenProductUid = movie.getUid();
        int givenCartQuantity = 1;

        CartDto.Add cartDto = new CartDto.Add(givenProductUid, givenCartQuantity);

        // when
        cartService.addCarts(user2.getUid(), cartDto);

        // then
        User user = userRepository.findByUidJoinCartProduct(user2.getUid())
                .orElseThrow(RuntimeException::new);

        assertThat(user.getCartList())
                .extracting("product.uid", "quantity")
                .contains(
                        tuple(movie.getUid(), givenCartQuantity)
                );

    }


    @Test
    @DisplayName("사용자가 이전에 저장한 카트 정보를 사용자의 고유식별자를 통해 조회할 수 있다.")
    @Transactional
    void testGetCartListByUserUid() throws Exception{
        // given
        String givenUserUid = user1.getUid();


        // when
        List<CartDto.Detail> result = cartService.findCartByUserUid(givenUserUid);

        // then
        assertThat(result).extracting( "productUid", "productName", "productImageUrl", "price", "quantity")
                .contains(
                        tuple(album.getUid(), album.getName(), album.getThumbnailImageUrl(), album.getPrice(), 3),
                        tuple(book.getUid(), book.getName(), book.getThumbnailImageUrl(), book.getPrice(), 2)
                );
    }


}