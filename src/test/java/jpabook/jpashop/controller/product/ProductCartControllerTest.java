package jpabook.jpashop.controller.product;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jpabook.jpashop.controller.common.request.CartRequest;
import jpabook.jpashop.controller.common.response.CartResponse;
import jpabook.jpashop.domain.product.Cart;
import jpabook.jpashop.domain.user.User;
import jpabook.jpashop.repository.UserRepository;
import jpabook.jpashop.testUtils.ControllerTest;
import jpabook.jpashop.util.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Date;
import java.util.List;

import static jpabook.jpashop.testUtils.TestDataFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



class ProductCartControllerTest extends ControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private JwtTokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        testDataFixture.saveProducts();
        testDataFixture.saveCarts();
    }

    @Test
    @DisplayName("사용자는 특정 상품의 갯수를 선택해 장바구니에 담을 수 있다.")
    public void given_AuthenticatedUserProduct_when_AddCart_then_Success() throws Exception{
        //given
        String givenUserUid = user2.getUid();
        String givenProductId = movie.getUid();
        int givenQuantity = 1;

        String token = tokenProvider.sign(givenUserUid, new Date());

        //when
        mockMvc.perform(post("/api/product/cart")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CartRequest.Add(givenProductId, givenQuantity))))
                .andDo(print())
                .andExpect(status().isOk());
        //then
        User actualUser = userRepository.findByUid(givenUserUid)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        assertThat(actualUser.getCartList().size()).isEqualTo(1);
        assertThat(actualUser.getCartList()).extracting("product.uid", "quantity")
                .contains(
                        tuple(givenProductId, givenQuantity)
                );

    }

    @Test
    @DisplayName("사용자가 장바구니에 이미 있는 상품을 추가할시 기존 장바구니에 있던 상품의 갯수가 증가한다.")
    public void given_AuthenticatedUserCart_when_AddCart_then_AddCartQuantity() throws Exception{
        //given
        String givenUserUid = user1.getUid();
        Cart givenCart = cart1;
        String givenProductUid = givenCart.getProduct().getUid();
        int givenQuantity = 1;

        String token = tokenProvider.sign(givenUserUid, new Date());

        //when
        mockMvc.perform(post("/api/product/cart")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CartRequest.Add(givenProductUid, givenQuantity))))
                .andDo(print())
                .andExpect(status().isOk());
        //then
        User actualUser = userRepository.findByUid(givenUserUid)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        assertThat(actualUser.getCartList().size()).isEqualTo(2);
        assertThat(actualUser.getCartList()).extracting("product.uid", "quantity")
                .contains(
                        tuple(givenProductUid, givenCart.getQuantity() + givenQuantity)
                );

    }


    @Test
    @DisplayName("사용자는 자신의 장바구니에 속한 상품을 조회할 수 있다.")
    public void given_AuthenticatedUser_when_GetCartlist_ThenReturn() throws Exception{
        //given
        String givenUid = user1.getUid();

        String token = tokenProvider.sign(givenUid, new Date());
        //when
        MvcResult mvcResponse = mockMvc.perform(get("/api/product/cart")
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        //then
        List<CartResponse.Info> result = objectMapper.readValue(mvcResponse.getResponse().getContentAsString(), new TypeReference<List<CartResponse.Info>>() {});

        Cart expectedCart1 = cart1;
        Cart expectedCart2 = cart2;

        assertThat(result.size()).isEqualTo(2);
        assertThat(result).extracting(
                "productUid",
                        "productName",
                        "productImageUrl",
                        "price",
                        "quantity")
                .contains(
                        tuple(
                                expectedCart1.getProduct().getUid(),
                                expectedCart1.getProduct().getName(),
                                expectedCart1.getProduct().getThumbnailImageUrl(),
                                expectedCart1.getProduct().getPrice(),
                                expectedCart1.getQuantity()
                        ),
                        tuple(
                                expectedCart2.getProduct().getUid(),
                                expectedCart2.getProduct().getName(),
                                expectedCart2.getProduct().getThumbnailImageUrl(),
                                expectedCart2.getProduct().getPrice(),
                                expectedCart2.getQuantity()
                        )
                );
    }

    @ParameterizedTest
    @CsvSource({"1", "-1"})
    @DisplayName("사용자는 자신의 장바구니의 상품의 갯수를 수정할 수 있다.")
    public void givenAuthenticatedUserCart_when_UpdateCartQuantity_then_Success(int updateQuantity) throws Exception{
        //given
        String givenUid = user1.getUid();
        String productUid = cart1.getProduct().getUid();

        String token = tokenProvider.sign(givenUid, new Date());
        //when
        mockMvc.perform(put("/api/product/cart")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CartRequest.Update(productUid, updateQuantity)))
                )
                .andDo(print())
                .andExpect(status().isOk());
        //then
        Cart actual = getCart(productUid);
        Cart expected = cart1;

        assertThat(actual.getQuantity()).isEqualTo(expected.getQuantity() + updateQuantity);

    }

    @Test
    @DisplayName("사용자는 자신의 장바구니의 갯수를 0이하로 수정할 수 없다.")
    public void given_AuthenticatedUserCart_when_UpdateCartQuantityUnderZero_then_Failed() throws Exception{
        String givenUid = user1.getUid();
        String productUid = cart1.getProduct().getUid();
        String token = tokenProvider.sign(givenUid, new Date());
        int updateQuantity = -100;

        //when
        mockMvc.perform(put("/api/product/cart")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CartRequest.Update(productUid, updateQuantity)))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
        //then
        Cart actual = getCart(productUid);
        Cart expected = cart1;
        assertThat(actual.getQuantity()).isEqualTo(expected.getQuantity());

    }

    @Test
    @DisplayName("사용자는 자신의 장바구니에 속한 상품을 삭제할 수 있다.")
    public void given_AuthenticatedUserCart_when_RemoveCart_then_Success() throws Exception{
        //given
        String givenUid = user1.getUid();
        String productUid = cart1.getProduct().getUid();
        String token = tokenProvider.sign(givenUid, new Date());
        //when
        mockMvc.perform(delete("/api/product/cart/" + productUid)
                        .header("Authorization", "Bearer " + token)
                )
                .andDo(print())
                .andExpect(status().isOk());
        //then
        User actualUser = userRepository.findByUid(givenUid)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        User expectedUser = user1;
        Cart expectedRemainCart = cart2;

        assertThat(actualUser.getCartList().size()).isEqualTo(expectedUser.getCartList().size() - 1);
        assertThat(actualUser.getCartList()).extracting("product.uid", "quantity")
                .contains(
                        tuple(expectedRemainCart.getProduct().getUid(), expectedRemainCart.getQuantity())
                );
    }


    private Cart getCart(String cartUid) {
        User user = userRepository.findByUid("user-001")
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Cart actual = user.getCartList().stream()
                .filter(cart -> cart.getProduct().getUid().equals(cartUid))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
        return actual;
    }


}