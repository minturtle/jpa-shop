package jpabook.jpashop.controller.product;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jpabook.jpashop.controller.common.response.ProductResponse;
import jpabook.jpashop.domain.product.Album;
import jpabook.jpashop.domain.product.Book;
import jpabook.jpashop.domain.product.Movie;
import jpabook.jpashop.domain.product.Product;
import jpabook.jpashop.dto.PaginationListDto;
import jpabook.jpashop.enums.product.SortOption;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static jpabook.jpashop.testUtils.TestDataUtils.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

    @Test
    @DisplayName("사용자는 DB에 등록된 상품을 검색 조건 없이 검색할 시 DB에서 조회해 상품 정보를 최신순으로 정렬된 채 조회한다.")
    void given_Product_when_SearchProductWithoutSearchCondition_then_ReturnDefaultOrderRegisterDate() throws Exception{
        // given
        Product product1 = movie;
        Product product2 = album;
        Product product3 = book;

        // when
        MvcResult mvcResponse = mockMvc.perform(get("/api/product/list"))
                .andExpect(status().isOk())
                .andReturn();
        // then
        PaginationListDto<ProductResponse.Preview> result = objectMapper.readValue(mvcResponse.getResponse().getContentAsString(), new TypeReference<PaginationListDto<ProductResponse.Preview>>(){});


        assertThat(result.getCount()).isEqualTo(3);
        assertThat(result.getData()).extracting("productUid", "productName", "price", "productImage")
                .containsExactly(
                        tuple(product1.getUid(), product1.getName(), product1.getPrice(), product1.getThumbnailImageUrl()),
                        tuple(product2.getUid(), product2.getName(), product2.getPrice(), product2.getThumbnailImageUrl()),
                        tuple(product3.getUid(), product3.getName(), product3.getPrice(), product3.getThumbnailImageUrl())
                );
    }

    @Test
    @DisplayName("사용자는 DB에 등록된 상품을 상품의 이름으로 필터링하여 조회할 시 필터링된 상품정보를 조회한다.")
    public void given_Product_when_SearchProductFilterWithName_then_ReturnFilteredList() throws Exception{
        //given
        String givenProductName = "Movie";

        //when
        MvcResult mvcResponse = mockMvc.perform(get("/api/product/list")
                        .param("query", givenProductName))
                .andExpect(status().isOk())
                .andReturn();

        //then
        PaginationListDto<ProductResponse.Preview> result = objectMapper.readValue(mvcResponse.getResponse().getContentAsString(), new TypeReference<PaginationListDto<ProductResponse.Preview>>(){});

        Movie expectedProduct = movie;


        assertThat(result.getCount()).isEqualTo(1);
        assertThat(result.getData()).extracting("productUid", "productName", "price", "productImage")
                .contains(
                        tuple(expectedProduct.getUid(), expectedProduct.getName(), expectedProduct.getPrice(), expectedProduct.getThumbnailImageUrl())
                );
    }

    @Test
    @DisplayName("사용자는 DB에 등록된 상품을 상품의 가격 범위로 필터링하여 결과를 조회할 수 있다.")
    public void given_product_when_SearchWithPriceRange_then_ReturnFilteredList() throws Exception{
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

        Product expectedProduct1 = album;
        Product expectedProduct2 = book;


        assertThat(result.getCount()).isEqualTo(2);
        assertThat(result.getData()).extracting("productUid", "productName", "price", "productImage")
                .contains(
                        tuple(expectedProduct1.getUid(), expectedProduct1.getName(), expectedProduct1.getPrice(), expectedProduct1.getThumbnailImageUrl()),
                        tuple(expectedProduct2.getUid(), expectedProduct2.getName(), expectedProduct2.getPrice(), expectedProduct2.getThumbnailImageUrl())
                );
    }

    @Test
    @DisplayName("사용자는 DB에 등록된 상품을 상품의 카테고리로 필터링하여 조회할 수 있다.")
    public void given_product_when_SearchWithCategory_then_ReturnFilteredList() throws Exception{
        //given
        String givenCategory = albumCategory.getUid();

        //when
        MvcResult mvcResponse = mockMvc.perform(get("/api/product/list")
                        .param("category",givenCategory)
                )
                .andExpect(status().isOk())
                .andReturn();


        //then
        PaginationListDto<ProductResponse.Preview> result = objectMapper.readValue(mvcResponse.getResponse().getContentAsString(), new TypeReference<PaginationListDto<ProductResponse.Preview>>(){});

        Product expectedProduct = album;

        assertThat(result.getCount()).isEqualTo(1);
        assertThat(result.getData()).extracting("productUid", "productName", "price", "productImage")
                .contains(
                        tuple(expectedProduct.getUid(), expectedProduct.getName(), expectedProduct.getPrice(), expectedProduct.getThumbnailImageUrl())
                );

    }

    @Test
    @DisplayName("사용자는 DB에 등록된 상품 중 특정 타입의 상품만을 필터링하여 조회할 수 있다.")
    public void given_product_when_SearchFilterWithProductType_then_ReturnFilteredList() throws Exception{
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

        Product expectedProduct = book;

        assertThat(result.getCount()).isEqualTo(1);
        assertThat(result.getData()).extracting("productUid", "productName", "price", "productImage")
                .contains(
                        tuple(expectedProduct.getUid(), expectedProduct.getName(), expectedProduct.getPrice(), expectedProduct.getThumbnailImageUrl())
                );


    }

    @ParameterizedTest
    @CsvSource(value = {"BY_DATE:movie-001,album-001,book-001", "BY_NAME:album-001,book-001,movie-001", "BY_PRICE:book-001,album-001,movie-001"}, delimiter = ':')
    @DisplayName("사용자는 검색시 검색결과를 원하는대로 정렬할 수 있다.")
    public void given_product_when_SearchWithOrder_then_ReturnOrderedList(SortOption productSortOption, String expectedString) throws Exception{
        //given

        //when
        MvcResult mvcResponse = mockMvc.perform(get("/api/product/list")
                        .param("sortType", String.valueOf(productSortOption))
                )
                .andExpect(status().isOk())
                .andReturn();

        //then
        PaginationListDto<ProductResponse.Preview> result = objectMapper.readValue(mvcResponse.getResponse().getContentAsString(), new TypeReference<PaginationListDto<ProductResponse.Preview>>(){});
        String[] expected = expectedString.split(",");

        assertThat(result.getCount()).isEqualTo(3);
        assertThat(result.getData()).extracting("productUid")
                .containsExactly((Object[])expected);
    }

    @Test
    @DisplayName("사용자는 검색시 여러개의 조건을 걸어 원하는대로 정렬해 필터링된 결과값을 받아볼 수 있다.")
    public void given_Product_when_SearchWithMultiFiltering_then_ReturnFilteredResult() throws Exception{
        //given

        String givenName = book.getName();
        String minPrice = "1500";
        String maxPrice = "2000";
        String givenCategory = bookCategory.getUid();
        String productType = "BOOK";
        SortOption productSortOption = SortOption.BY_DATE;


        //when
        MvcResult mvcResponse = mockMvc.perform(get("/api/product/list")
                        .param("query", givenName)
                        .param("minPrice", minPrice)
                        .param("maxPrice", maxPrice)
                        .param("category", givenCategory)
                        .param("productType", productType)
                        .param("sortType", String.valueOf(productSortOption))
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        //then
        PaginationListDto<ProductResponse.Preview> result = objectMapper.readValue(mvcResponse.getResponse().getContentAsString(), new TypeReference<PaginationListDto<ProductResponse.Preview>>(){});

        Product expectedProduct = book;

        assertThat(result.getCount()).isEqualTo(1);
        assertThat(result.getData()).extracting("productUid", "productName", "price", "productImage")
                .contains(
                        tuple(expectedProduct.getUid(), expectedProduct.getName(), expectedProduct.getPrice(), expectedProduct.getThumbnailImageUrl())
                );


    }

    @Test
    @DisplayName("사용자는 특정 영화 상품을 상품의 고유 식별자로 선택해 상세 정보를 조회할 수 있다.")
    public void given_Movie_when_FindMovieByUid_then_ReturnMovieDetail() throws Exception{
        //given
        String givenMovieId = movie.getUid();


        //when
        MvcResult mvcResponse = mockMvc.perform(get("/api/product/" + givenMovieId))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        //then
        ProductResponse.MovieDetail actual = objectMapper.readValue(mvcResponse.getResponse().getContentAsString(), ProductResponse.MovieDetail.class);

        Movie expectedProduct = movie;

        assertThat(actual).extracting("uid", "name", "description", "price", "stockQuantity", "thumbnailUrl", "director", "actor")
                .containsExactly(
                        expectedProduct.getUid(),
                        expectedProduct.getName(),
                        expectedProduct.getDescription(),
                        expectedProduct.getPrice(),
                        expectedProduct.getStockQuantity(),
                        expectedProduct.getThumbnailImageUrl(),
                        expectedProduct.getDirector(),
                        expectedProduct.getActor()
                );
    }

    @Test
    @DisplayName("사용자는 특정 앨범 상품을 상품의 고유 식별자로 선택해 상세 정보를 조회할 수 있다.")
    public void given_Product_WhenFindAlbumByUidThenReturnAlbumDetail() throws Exception{
        //given
        String givenAlbumId = album.getUid();
        //when
        MvcResult mvcResponse = mockMvc.perform(get("/api/product/" + givenAlbumId))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        //then
        ProductResponse.AlbumDetail actual = objectMapper.readValue(mvcResponse.getResponse().getContentAsString(), ProductResponse.AlbumDetail.class);

        Album expectedProduct = album;

        assertThat(actual).extracting("uid", "name", "description", "price", "stockQuantity", "thumbnailUrl", "artist", "etc")
                .containsExactly(
                        expectedProduct.getUid(),
                        expectedProduct.getName(),
                        expectedProduct.getDescription(),
                        expectedProduct.getPrice(),
                        expectedProduct.getStockQuantity(),
                        expectedProduct.getThumbnailImageUrl(),
                        expectedProduct.getArtist(),
                        expectedProduct.getEtc()
                );
    }


    @Test
    @DisplayName("사용자는 특정 책 상품을 상품의 고유 식별자로 선택해 상세 정보를 조회할 수 있다.")
    public void given_Product_WhenFindBookByUidThenReturnBookDetail() throws Exception{
        //given
        String givenBookId = book.getUid();
        //when
        MvcResult mvcResponse = mockMvc.perform(get("/api/product/" + givenBookId))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        //then
        ProductResponse.BookDetail bookDetail = objectMapper.readValue(mvcResponse.getResponse().getContentAsString(), ProductResponse.BookDetail.class);

        Book expectedProduct = book;


        assertThat(bookDetail).extracting("uid", "name", "description", "price", "stockQuantity", "thumbnailUrl", "author", "isbn")
                .containsExactly(
                        expectedProduct.getUid(),
                        expectedProduct.getName(),
                        expectedProduct.getDescription(),
                        expectedProduct.getPrice(),
                        expectedProduct.getStockQuantity(),
                        expectedProduct.getThumbnailImageUrl(),
                        expectedProduct.getAuthor(),
                        expectedProduct.getIsbn()
                );


    }


}