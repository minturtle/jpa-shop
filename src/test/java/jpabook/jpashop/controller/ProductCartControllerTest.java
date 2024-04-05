package jpabook.jpashop.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jpabook.jpashop.controller.request.CartRequest;
import jpabook.jpashop.controller.response.CartResponse;
import jpabook.jpashop.domain.user.User;
import jpabook.jpashop.repository.UserRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = {"classpath:init-product-test-data.sql", "classpath:init-user-test-data.sql", "classpath:init-cart-test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ProductCartControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private JwtTokenProvider tokenProvider;

    @Test
    @DisplayName("사용자는 특정 상품의 갯수를 선택해 장바구니에 담을 수 있다.")
    public void testWhenAddCartThenSuccess() throws Exception{
        //given
        String givenUid = "user-001";
        String givenMovieId = "movie-001";
        int givenQuantity = 1;

        String token = tokenProvider.sign(givenUid, new Date());

        //when
        mockMvc.perform(post("/api/product/cart")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CartRequest.Add(givenMovieId, givenQuantity))))
                .andDo(print())
                .andExpect(status().isOk());
        //then
        User user = userRepository.findByUid(givenUid)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        assertThat(user.getCartList().size()).isEqualTo(3);
        assertThat(user.getCartList()).extracting("product.uid", "quantity")
                .contains(
                        tuple(givenMovieId, givenQuantity),
                        tuple("album-001", 3),
                        tuple("book-001", 2)
                );

    }

    @Test
    @DisplayName("사용자는 자신의 장바구니에 속한 상품을 조회할 수 있다.")
    public void testWhenGetCategorylistThenReturn() throws Exception{
        //given
        String givenUid = "user-001";

        String token = tokenProvider.sign(givenUid, new Date());
        //when
        MvcResult mvcResponse = mockMvc.perform(get("/api/product/cart")
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        //then
        List<CartResponse.Info> result = objectMapper.readValue(mvcResponse.getResponse().getContentAsString(), new TypeReference<List<CartResponse.Info>>() {});

        assertThat(result.size()).isEqualTo(2);
        assertThat(result).extracting("productUid", "productName", "productImageUrl", "price" ,"quantity")
                .contains(
                        tuple("album-001", "Album Name", "http://example.com/album_thumbnail.jpg", 2000, 3),
                        tuple("book-001", "Book Name", "http://example.com/book_thumbnail.jpg", 1500, 2)
                );
    }



}