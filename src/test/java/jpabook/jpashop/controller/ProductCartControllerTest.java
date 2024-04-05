package jpabook.jpashop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jpabook.jpashop.controller.request.CartRequest;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = {"classpath:init-product-test-data.sql", "classpath:init-user-test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
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

        assertThat(user.getCartList().size()).isEqualTo(1);
        assertThat(user.getCartList()).extracting("product.uid", "quantity")
                .containsExactly(tuple(givenMovieId, givenQuantity));

    }


}