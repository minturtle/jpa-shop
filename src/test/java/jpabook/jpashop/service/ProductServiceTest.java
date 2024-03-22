package jpabook.jpashop.service;

import jpabook.jpashop.domain.product.Album;
import jpabook.jpashop.domain.product.Book;
import jpabook.jpashop.domain.product.Category;
import jpabook.jpashop.domain.product.Movie;
import jpabook.jpashop.dto.PaginationListDto;
import jpabook.jpashop.dto.ProductDto;
import jpabook.jpashop.enums.product.ProductType;
import jpabook.jpashop.enums.product.SortOption;
import jpabook.jpashop.repository.CategoryRepository;
import jpabook.jpashop.repository.product.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("이미 저장된 상품의 검색 필터링 없이 리스트를 조회할 수 있다.")
    void testGetProductListWithoutSearchCondition() throws Exception{
        // given
        Category bookCategory = saveCategory("c1", "bookCategory");
        Category albumCategory = saveCategory("c2", "albumCategory");
        Category movieCategory = saveCategory("c3", "MovieCategory");

        saveTestProducts(movieCategory, albumCategory, bookCategory);


        int searchSize = 2;
        ProductDto.SearchCondition searchCondition = new ProductDto.SearchCondition(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                SortOption.BY_NAME,
                ProductType.ALL
        );

        // when
        PaginationListDto<ProductDto.Preview> actual = productService.search(searchCondition, PageRequest.of(0, searchSize));

        // then
        assertThat(actual.getCount()).isEqualTo(3L);
        assertThat(actual.getData()).extracting("uid", "name", "price", "thumbnailUrl")
                .containsExactly(
                        tuple("movie-001", "Inception", 15000, "http://example.com/inception.jpg"),
                        tuple("album-001", "The Dark Side of the Moon", 20000, "http://example.com/darkside.jpg")
                );
    }


    public void saveTestProducts(Category movieCategory, Category albumCategory, Category bookCategory){
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

        productRepository.saveAll(List.of(movie, album, book));
    }

    public Category saveCategory(String categoryUid, String name){
        Category category = Category.builder()
                .uid(categoryUid)
                .name(name)
                .build();
        return categoryRepository.save(category);
    }

}