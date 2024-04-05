package jpabook.jpashop.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jpabook.jpashop.controller.response.ProductResponse;
import jpabook.jpashop.dto.PaginationListDto;
import jpabook.jpashop.util.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(value = "classpath:init-product-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Autowired
    private JwtTokenProvider tokenProvider;


    @Test
    @DisplayName("사용자는 DB에 등록된 상품을 검색 조건 없이 검색할 시 DB에서 조회해 상품 정보를 최신순으로 정렬해 반환한다.")
    void testWhenSearchProductThenReturnList() throws Exception{
        // given

        // when
        MvcResult mvcResponse = mockMvc.perform(get("/api/product/list"))
                .andExpect(status().isOk())
                .andReturn();
        // then
        PaginationListDto<ProductResponse.Preview> result = objectMapper.readValue(mvcResponse.getResponse().getContentAsString(), new TypeReference<PaginationListDto<ProductResponse.Preview>>(){});


        assertThat(result.getCount()).isEqualTo(3);
        assertThat(result.getData()).extracting("itemUid", "itemName", "price", "productImage")
                .containsExactly(
                        tuple("movie-001", "Movie Name", 3000, "http://example.com/movie_thumbnail.jpg"),
                        tuple("album-001", "Album Name", 2000, "http://example.com/album_thumbnail.jpg"),
                        tuple("book-001", "Book Name", 1500, "http://example.com/book_thumbnail.jpg")
                );



    }


}