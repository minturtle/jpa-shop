package jpabook.jpashop.service.product;

import jpabook.jpashop.domain.product.Cart;
import jpabook.jpashop.domain.user.User;
import jpabook.jpashop.dto.CartDto;
import jpabook.jpashop.repository.UserRepository;
import jpabook.jpashop.service.CartService;
import jpabook.jpashop.testUtils.ServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static jpabook.jpashop.testUtils.TestDataFixture.*;
import static org.assertj.core.api.Assertions.*;

@Sql(scripts = {"classpath:init-product-test-data.sql", "classpath:init-user-test-data.sql", "classpath:init-cart-test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"classpath:clean-up.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class CartServiceTest extends ServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartService cartService;

    @Test
    @DisplayName("특정 유저가 상품의 식별자와 갯수를 입력해 해당 상품을 장바구니에 추가해 저장할 수 있다.")
    void given_UserAndProduct_when_AddCart_then_SaveCart() throws Exception{
        // given
        String givenProductUid = movie.getUid();
        String givenUserUid = user2.getUid();

        int givenCartQuantity = 1;

        CartDto.Add cartDto = new CartDto.Add(givenProductUid, givenCartQuantity);

        // when
        cartService.addCarts(givenUserUid, cartDto);

        // then
        User user = userRepository.findByUidJoinCartProduct(givenUserUid)
                .orElseThrow(RuntimeException::new);

        assertThat(user.getCartList())
                .extracting("product.uid", "quantity")
                .contains(
                        tuple(givenProductUid, givenCartQuantity)
                );

    }


    @Test
    @DisplayName("사용자가 이전에 저장한 카트 정보를 사용자의 고유식별자를 통해 조회할 수 있다.")
    @Transactional
    void given_UserHasCart_when_getCartList_then_returnUsersCartList() throws Exception{
        // given
        String givenUserUid = user1.getUid();
        Cart givenCart1 = cart1;
        Cart givenCart2 = cart2;

        // when
        List<CartDto.Detail> result = cartService.findCartByUser(givenUserUid);

        // then
        assertThat(result).extracting(
                "productUid",
                        "productName",
                        "productImageUrl",
                        "price",
                        "quantity"
                )
                .contains(
                        tuple(
                                givenCart1.getProduct().getUid(),
                                givenCart1.getProduct().getName(),
                                givenCart1.getProduct().getThumbnailImageUrl(),
                                givenCart1.getProduct().getPrice(),
                                givenCart1.getQuantity()
                        ),
                        tuple(
                                givenCart2.getProduct().getUid(),
                                givenCart2.getProduct().getName(),
                                givenCart2.getProduct().getThumbnailImageUrl(), 
                                givenCart2.getProduct().getPrice(),
                                givenCart2.getQuantity()
                        )
                );
    }


}