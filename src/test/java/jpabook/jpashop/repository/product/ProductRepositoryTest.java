package jpabook.jpashop.repository.product;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jpabook.jpashop.domain.product.*;
import jpabook.jpashop.dto.ProductDto;
import jpabook.jpashop.enums.product.ProductType;
import jpabook.jpashop.enums.product.SortOption;
import jpabook.jpashop.repository.CategoryRepository;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;


@DataJpaTest
@Import(ProductRepositoryTest.TestConfig.class)
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;


    @Test
    @DisplayName("이미 저장된 상품의 리스트를 아무 검색 필터 조건 없이 검색할 수 있다.")
    void testSearchWithNoCondition() throws Exception{
        // given
        Category bookCategory = saveCategory("c1", "bookCategory");
        Category albumCategory = saveCategory("c2", "albumCategory");
        Category movieCategory = saveCategory("c3", "MovieCategory");

        saveTestProducts(movieCategory, albumCategory, bookCategory);

        int searchSize = 10;
        ProductDto.SearchCondition searchCondition = new ProductDto.SearchCondition(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                SortOption.BY_NAME,
                ProductType.ALL
        );


        // when
        List<Product> result = productRepository.search(searchCondition, PageRequest.of(0, searchSize));


        // then
        assertThat(result).extracting("uid", "name", "price", "thumbnailImageUrl")
                .containsExactly(
                        tuple("movie-001", "Inception", 15000, "http://example.com/inception.jpg"),
                        tuple("album-001", "The Dark Side of the Moon", 20000, "http://example.com/darkside.jpg"),
                        tuple("book-001", "The Great Gatsby", 10000, "http://example.com/gatsby.jpg")
                );
    }

    @Test
    @DisplayName("이미 저장된 물품의 이름을 기준으로 검색할 수 있다")
    public void testSearchByName() throws Exception{
        //given
        Category bookCategory = saveCategory("c1", "bookCategory");
        Category albumCategory = saveCategory("c2", "albumCategory");
        Category movieCategory = saveCategory("c3", "MovieCategory");

        saveTestProducts(movieCategory, albumCategory, bookCategory);

        int searchSize = 10;
        ProductDto.SearchCondition searchCondition = new ProductDto.SearchCondition(
                Optional.of("The"),
                Optional.empty(),
                Optional.empty(),
                SortOption.BY_NAME,
                ProductType.ALL
        );


        //when
        List<Product> result = productRepository.search(searchCondition, PageRequest.of(0, searchSize));

        //then
        assertThat(result).extracting("uid", "name", "price", "thumbnailImageUrl")
                .containsExactly(
                        tuple("album-001", "The Dark Side of the Moon", 20000, "http://example.com/darkside.jpg"),
                        tuple("book-001", "The Great Gatsby", 10000, "http://example.com/gatsby.jpg")
                );
    }

    @Test
    @DisplayName("이미 저장된 물품의 카테고리 식별자로 검색할 수 있다.")
    public void testSearchByCategoryUid() throws Exception{
        //given
        Category bookCategory = saveCategory("c1", "bookCategory");
        Category albumCategory = saveCategory("c2", "albumCategory");
        Category movieCategory = saveCategory("c3", "MovieCategory");

        saveTestProducts(movieCategory, albumCategory, bookCategory);

        int searchSize = 10;
        ProductDto.SearchCondition searchCondition = new ProductDto.SearchCondition(
                Optional.empty(),
                Optional.empty(),
                Optional.of(bookCategory.getUid()),
                SortOption.BY_NAME,
                ProductType.ALL
        );

        //when
        List<Product> result = productRepository.search(searchCondition, PageRequest.of(0, searchSize));


        //then
        assertThat(result).extracting("uid", "name", "price", "thumbnailImageUrl")
                .containsExactly(
                        tuple("book-001", "The Great Gatsby", 10000, "http://example.com/gatsby.jpg")
                );
    }

    @Test
    @DisplayName("저장된 물건의 가격범위로 물건을 검색할 수 있다")
    public void testSearchByPriceRange() throws Exception{
        //given
        Category bookCategory = saveCategory("c1", "bookCategory");
        Category albumCategory = saveCategory("c2", "albumCategory");
        Category movieCategory = saveCategory("c3", "MovieCategory");

        saveTestProducts(movieCategory, albumCategory, bookCategory);

        int searchSize = 10;
        ProductDto.SearchCondition searchCondition = new ProductDto.SearchCondition(
                Optional.empty(),
                Optional.of(new ProductDto.PriceRange(15000, 20000)),
                Optional.empty(),
                SortOption.BY_NAME,
                ProductType.ALL
        );
        //when
        List<Product> result = productRepository.search(searchCondition, PageRequest.of(0, searchSize));


        //then
        assertThat(result).extracting("uid", "name", "price", "thumbnailImageUrl")
                .containsExactly(
                        tuple("movie-001", "Inception", 15000, "http://example.com/inception.jpg"),
                        tuple("album-001", "The Dark Side of the Moon", 20000, "http://example.com/darkside.jpg")
                );
    }

    @ParameterizedTest
    @CsvSource(value = {"BY_NAME,movie-001:album-001:book-001", "BY_DATE,book-001:album-001:movie-001", "BY_PRICE,book-001:movie-001:album-001"})
    @DisplayName("다양한 정렬기준을 통해 물품 검색 결과를 정렬할 수 있다.")
    public void testOrder(SortOption sortOption, String expectedUidListString) throws Exception{
        //given
        Category bookCategory = saveCategory("c1", "bookCategory");
        Category albumCategory = saveCategory("c2", "albumCategory");
        Category movieCategory = saveCategory("c3", "MovieCategory");

        saveTestProducts(movieCategory, albumCategory, bookCategory);

        int searchSize = 10;
        ProductDto.SearchCondition searchCondition = new ProductDto.SearchCondition(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                sortOption,
                ProductType.ALL
        );
        //when
        List<Product> result = productRepository.search(searchCondition, PageRequest.of(0, searchSize));

        //then
        String[] expectedUidList = expectedUidListString.split(":");

        assertThat(result)
                .extracting("uid")
                .containsExactly(expectedUidList);
    }


    @Test
    @DisplayName("저장된 물픔에 동시에 여러 검색 조건으로 물품을 검색할 수 있다.")
    public void testSearchCondtionMultiple() throws Exception{
        //given
        Category bookCategory = saveCategory("c1", "bookCategory");
        Category albumCategory = saveCategory("c2", "albumCategory");
        Category movieCategory = saveCategory("c3", "MovieCategory");

        saveTestProducts(movieCategory, albumCategory, bookCategory);

        int searchSize = 10;
        ProductDto.SearchCondition searchCondition = new ProductDto.SearchCondition(
                Optional.of("Inception"),
                Optional.of(new ProductDto.PriceRange(15000, 20000)),
                Optional.of(movieCategory.getUid()),
                SortOption.BY_NAME,
                ProductType.ALL
        );
        //when
        List<Product> result = productRepository.search(searchCondition, PageRequest.of(0, searchSize));


        //then
        assertThat(result).extracting("uid", "name", "price", "thumbnailImageUrl")
                .containsExactly(
                        tuple("movie-001", "Inception", 15000, "http://example.com/inception.jpg")
                );

    }



    public void saveTestProducts(Category movieCategory, Category albumCategory, Category bookCategory) throws InterruptedException {
        Movie movie = Movie.builder()
                .uid("movie-001")
                .name("Inception")
                .price(15000)
                .stockQuantity(100)
                .thumbnailImageUrl("http://example.com/inception.jpg")
                .director("Christopher Nolan")
                .actor("Leonardo DiCaprio")
                .build();

        movie.addCategory(movieCategory);
        Thread.sleep(10);

        Album album = Album.builder()
                .uid("album-001")
                .name("The Dark Side of the Moon")
                .price(20000)
                .stockQuantity(50)
                .thumbnailImageUrl("http://example.com/darkside.jpg")
                .artist("Pink Floyd")
                .etc("1973, Progressive rock")
                .build();

        album.addCategory(albumCategory);

        Thread.sleep(10);

        Book book = Book.builder()
                .uid("book-001")
                .name("The Great Gatsby")
                .price(10000)
                .stockQuantity(100)
                .thumbnailImageUrl("http://example.com/gatsby.jpg")
                .author("F. Scott Fitzgerald")
                .isbn("978-3-16-148410-0")
                .build();

        book.addCategory(bookCategory);

        productRepository.save(movie);
        productRepository.save(album);
        productRepository.save(book);
    }

    public Category saveCategory(String categoryUid, String name){
        Category category = Category.builder()
                .uid(categoryUid)
                .name(name)
                .build();
        return categoryRepository.save(category);
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