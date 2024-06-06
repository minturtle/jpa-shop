package jpabook.jpashop.repository.product;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jpabook.jpashop.domain.product.*;
import jpabook.jpashop.dto.ProductDto;
import jpabook.jpashop.enums.product.ProductType;
import jpabook.jpashop.enums.product.SortOption;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static jpabook.jpashop.testUtils.TestDataUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;


@DataJpaTest
@Import(ProductRepositoryTest.TestConfig.class)
@ActiveProfiles("test")
@Sql(value = "classpath:init-product-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Transactional
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;



    @Test
    @DisplayName("이미 저장된 상품의 리스트를 아무 검색 필터 조건 없이 검색할 수 있다.")
    void testSearchWithNoCondition() throws Exception{
        // given
        int searchSize = 10;
        ProductDto.SearchCondition searchCondition = new ProductDto.SearchCondition(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                SortOption.BY_NAME,
                ProductType.ALL
        );


        // when
        List<ProductDto.Preview> result = productRepository.search(searchCondition, PageRequest.of(0, searchSize));


        // then
        assertThat(result).extracting("uid", "name", "price", "thumbnailUrl")
                .containsExactly(
                        tuple(album.getUid() , album.getName(), album.getPrice(), album.getThumbnailImageUrl()),
                        tuple(book.getUid(), book.getName(), book.getPrice(), book.getThumbnailImageUrl()),
                        tuple(movie.getUid(), movie.getName(), movie.getPrice(), movie.getThumbnailImageUrl())
                );
    }

    @Test
    @DisplayName("이미 저장된 물품의 이름을 기준으로 검색할 수 있다")
    public void testSearchByName() throws Exception{
        //given

        int searchSize = 10;
        ProductDto.SearchCondition searchCondition = new ProductDto.SearchCondition(
                Optional.of("Album"),
                Optional.empty(),
                Optional.empty(),
                SortOption.BY_NAME,
                ProductType.ALL
        );


        //when
        List<ProductDto.Preview> result = productRepository.search(searchCondition, PageRequest.of(0, searchSize));

        //then
        assertThat(result).extracting("uid", "name", "price", "thumbnailUrl")
                .containsExactly(
                        tuple(album.getUid(), album.getName(), album.getPrice(), album.getThumbnailImageUrl())
                );
    }

    @Test
    @DisplayName("이미 저장된 물품의 카테고리 식별자로 검색할 수 있다.")
    public void testSearchByCategoryUid() throws Exception{
        //given

        int searchSize = 10;
        ProductDto.SearchCondition searchCondition = new ProductDto.SearchCondition(
                Optional.empty(),
                Optional.empty(),
                Optional.of(bookCategory.getUid()),
                SortOption.BY_NAME,
                ProductType.ALL
        );

        //when
        List<ProductDto.Preview> result = productRepository.search(searchCondition, PageRequest.of(0, searchSize));


        //then
        assertThat(result).extracting("uid", "name", "price", "thumbnailUrl")
                .containsExactly(
                        tuple(book.getUid(), book.getName(), book.getPrice(), book.getThumbnailImageUrl())
                );
    }

    @Test
    @DisplayName("저장된 물건의 가격범위로 물건을 검색할 수 있다")
    public void testSearchByPriceRange() throws Exception{
        //given

        int searchSize = 10;
        ProductDto.SearchCondition searchCondition = new ProductDto.SearchCondition(
                Optional.empty(),
                Optional.of(new ProductDto.PriceRange(1500, 2000)),
                Optional.empty(),
                SortOption.BY_NAME,
                ProductType.ALL
        );
        //when
        List<ProductDto.Preview> result = productRepository.search(searchCondition, PageRequest.of(0, searchSize));


        //then
        assertThat(result).extracting("uid", "name", "price", "thumbnailUrl")
                .containsExactly(
                        tuple(album.getUid(), album.getName(), album.getPrice(), album.getThumbnailImageUrl()),
                        tuple(book.getUid(), book.getName(), book.getPrice(), book.getThumbnailImageUrl())
                );
    }

    @ParameterizedTest
    @CsvSource(value = {"BY_NAME,album-001:book-001:movie-001", "BY_DATE,movie-001:album-001:book-001", "BY_PRICE,book-001:album-001:movie-001"})
    @DisplayName("다양한 정렬기준을 통해 물품 검색 결과를 정렬할 수 있다.")
    public void testOrder(SortOption sortOption, String expectedUidListString) throws Exception{
        //given

        int searchSize = 10;
        ProductDto.SearchCondition searchCondition = new ProductDto.SearchCondition(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                sortOption,
                ProductType.ALL
        );
        //when
        List<ProductDto.Preview> result = productRepository.search(searchCondition, PageRequest.of(0, searchSize));

        //then
        String[] expectedUidList = expectedUidListString.split(":");

        assertThat(result)
                .extracting("uid")
                .containsExactly((Object[])expectedUidList);
    }


    @Test
    @DisplayName("저장된 물픔에 동시에 여러 검색 조건으로 물품을 검색할 수 있다.")
    public void testSearchCondtionMultiple() throws Exception{
        //given

        int searchSize = 10;
        ProductDto.SearchCondition searchCondition = new ProductDto.SearchCondition(
                Optional.of("Name"),
                Optional.of(new ProductDto.PriceRange(1500, 2000)),
                Optional.of(albumCategory.getUid()),
                SortOption.BY_NAME,
                ProductType.ALBUM
        );
        //when
        List<ProductDto.Preview> result = productRepository.search(searchCondition, PageRequest.of(0, searchSize));


        //then
        assertThat(result).extracting("uid", "name", "price", "thumbnailUrl")
                .containsExactly(
                        tuple(album.getUid(), album.getName(), album.getPrice(), album.getThumbnailImageUrl())
                );

    }

    @Test
    @DisplayName("물품 검색시 페이지네이션이 적용되지 않은 검색 결과 물품의 총 갯수를 알 수 있다.")
    public void testCountSearch() throws Exception{
        //given

        ProductDto.SearchCondition searchCondition = new ProductDto.SearchCondition(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                SortOption.BY_NAME,
                ProductType.ALL
        );

        //when
        Long result = productRepository.getCount(searchCondition);
        //then
        assertThat(result).isEqualTo(3L);

    }


    @Test
    @DisplayName("물품 검색시 CursorValue 없이 검색을 수행할 경우 첫페이지의 결과를 Cursor 방식으로 받아올 수 있다.")
    void given_Nocursor_when_Search_then_ReturnFirstPage() throws Exception{
        // given
        ProductDto.SearchCondition searchCondition = new ProductDto.SearchCondition(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                SortOption.BY_DATE,
                ProductType.ALL
        );
        // when
        Optional<String> cursorValue = Optional.empty();
        List<ProductDto.Preview> result = productRepository.search(searchCondition, cursorValue, 2);
        // then
        assertThat(result).extracting("uid", "name", "price", "thumbnailUrl")
                .containsExactly(
                        tuple(movie.getUid(), movie.getName(), movie.getPrice(), movie.getThumbnailImageUrl()),
                        tuple(album.getUid(), album.getName(), album.getPrice(), album.getThumbnailImageUrl())
                );
    }

    @Test
    @DisplayName("물품 검색시 CursorValue를 통해 다음 페이지의 결과를 Cursor 방식으로 받아올 수 있다.")
    void given_cursor_when_Search_then_ReturnNextPage() throws Exception{
        // given
        ProductDto.SearchCondition searchCondition = new ProductDto.SearchCondition(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                SortOption.BY_NAME,
                ProductType.ALL
        );

        Optional<String> cursorValue = Optional.of(album.getName());
        // when
        List<ProductDto.Preview> result = productRepository.search(searchCondition, cursorValue, 2);
        // then
        assertThat(result).extracting("uid", "name", "price", "thumbnailUrl")
                .containsExactly(
                        tuple(book.getUid(), book.getName(), book.getPrice(), book.getThumbnailImageUrl()),
                        tuple(movie.getUid(), movie.getName(), movie.getPrice(), movie.getThumbnailImageUrl())
                );
    }


    @TestConfiguration
    public static class TestConfig{

        @PersistenceContext
        EntityManager em;


        @Bean
        public JPAQueryFactory jpaQueryFactory(){
            return new JPAQueryFactory(em);
        }


    }

}