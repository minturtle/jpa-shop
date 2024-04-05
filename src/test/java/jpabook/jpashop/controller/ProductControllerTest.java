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
    @DisplayName("사용자는 DB에 등록된 상품을 검색 조건 없이 검색할 시 DB에서 조회해 상품 정보를 최신순으로 정렬된 채 조회한다.")
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

    @Test
    @DisplayName("사용자는 DB에 등록된 상품을 상품의 이름으로 필터링하여 조회할 시 필터링된 상품정보를 조회한다.")
    public void testWhenSearchProductFilterWithNameThenReturnFilteredList() throws Exception{
        //given
        String givenProductName = "Movie";

        //when
        MvcResult mvcResponse = mockMvc.perform(get("/api/product/list")
                        .param("query", givenProductName))
                .andExpect(status().isOk())
                .andReturn();

        //then
        PaginationListDto<ProductResponse.Preview> result = objectMapper.readValue(mvcResponse.getResponse().getContentAsString(), new TypeReference<PaginationListDto<ProductResponse.Preview>>(){});


        assertThat(result.getCount()).isEqualTo(1);
        assertThat(result.getData()).extracting("itemUid", "itemName", "price", "productImage")
                .contains(
                        tuple("movie-001", "Movie Name", 3000, "http://example.com/movie_thumbnail.jpg")

                );
    }

    @Test
    @DisplayName("사용자는 DB에 등록된 상품을 상품의 가격 범위로 필터링하여 결과를 조회할 수 있다.")
    public void testWhenSearchWithPriceRangeThenReturnFilteredList() throws Exception{
        //given
        String minPrice = "1500";
        String maxPrice = "2000";
        //when
        MvcResult mvcResponse = mockMvc.perform(get("/api/product/list")
                        .param("minPrice",minPrice)
                        .param("maxPrice", maxPrice)
                )
                .andExpect(status().isOk())
                .andReturn();

        //then
        PaginationListDto<ProductResponse.Preview> result = objectMapper.readValue(mvcResponse.getResponse().getContentAsString(), new TypeReference<PaginationListDto<ProductResponse.Preview>>(){});



        assertThat(result.getCount()).isEqualTo(2);
        assertThat(result.getData()).extracting("itemUid", "itemName", "price", "productImage")
                .contains(
                        tuple("album-001", "Album Name", 2000, "http://example.com/album_thumbnail.jpg"),
                        tuple("book-001", "Book Name", 1500, "http://example.com/book_thumbnail.jpg")
                );
    }

    @Test
    @DisplayName("사용자는 DB에 등록된 상품을 상품의 카테고리로 필터링하여 조회할 수 있다.")
    public void testWhenSearchWithCategoryThenReturnFilteredList() throws Exception{
        //given
        String givenCategory = "category-001";

        //when
        MvcResult mvcResponse = mockMvc.perform(get("/api/product/list")
                        .param("category",givenCategory)
                )
                .andExpect(status().isOk())
                .andReturn();


        //then
        PaginationListDto<ProductResponse.Preview> result = objectMapper.readValue(mvcResponse.getResponse().getContentAsString(), new TypeReference<PaginationListDto<ProductResponse.Preview>>(){});


        assertThat(result.getCount()).isEqualTo(1);
        assertThat(result.getData()).extracting("itemUid", "itemName", "price", "productImage")
                .contains(
                        tuple("movie-001", "Movie Name", 3000, "http://example.com/movie_thumbnail.jpg")

                );

    }

    @Test
    @DisplayName("사용자는 DB에 등록된 상품 중 특정 타입의 상품만을 필터링하여 조회할 수 있다.")
    public void testWhenSearchFilterWithProductTypeThenReturnFilteredList() throws Exception{
        //given
        String productType = "BOOK";

        //when
        MvcResult mvcResponse = mockMvc.perform(get("/api/product/list")
                        .param("productType", productType)
                )
                .andExpect(status().isOk())
                .andReturn();
        //then
        PaginationListDto<ProductResponse.Preview> result = objectMapper.readValue(mvcResponse.getResponse().getContentAsString(), new TypeReference<PaginationListDto<ProductResponse.Preview>>(){});


        assertThat(result.getCount()).isEqualTo(1);
        assertThat(result.getData()).extracting("itemUid", "itemName", "price", "productImage")
                .contains(
                        tuple("book-001", "Book Name", 1500, "http://example.com/book_thumbnail.jpg")
                );


    }




}