package jpabook.jpashop.service.product;

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
import jpabook.jpashop.service.ProductService;
import jpabook.jpashop.testUtils.TestDataUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static jpabook.jpashop.testUtils.TestDataUtils.*;
import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
@Sql(value = "classpath:init-product-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"classpath:clean-up.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Test
    @DisplayName("이미 저장된 상품의 특정 검색 조건 없이 리스트를 조회해, 조회된 물품의 정보와 갯수를 알 수 있다.")
    void testGetProductListWithoutSearchCondition() throws Exception{
        // given
        Album givenAlbum = album;
        Book givenBook = book;


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
                        tuple(givenAlbum.getUid(), givenAlbum.getName(), givenAlbum.getPrice(), givenAlbum.getThumbnailImageUrl()),
                        tuple(givenBook.getUid(), givenBook.getName(), givenBook.getPrice(), givenBook.getThumbnailImageUrl())
                );
    }

    @Test
    @DisplayName("이미 저장된 영화 상품의 상세 정보를 상품의 고유식별자로 조회할 수 있다.")
    public void testFindMovieByProductUid() throws Exception{
        //given
        Movie givenMovie = movie;

        //when
        ProductDto.Detail result = productService.findByUid(givenMovie.getUid());

        //then
        if(!(result instanceof ProductDto.MovieDetail)){
            fail("Product DTO는 Movie 정보를 포함해서 담고 있어야 한다.");
            return;
        }


        assertThat(result).extracting("uid", "name", "thumbnailUrl", "description", "price", "stockQuantity", "actor", "director")
                .contains(
                        givenMovie.getUid(), givenMovie.getName(), givenMovie.getThumbnailImageUrl(), givenMovie.getDescription(), givenMovie.getPrice(), givenMovie.getStockQuantity(), givenMovie.getActor(), givenMovie.getDirector()
                );
    }

    @Test
    @DisplayName("이미 저장된 앨범 상품의 상세 정보를 상품의 고유 식별자로 조회할 수 있다.")
    public void testFindByAlbumProductUid() throws Exception{
        //given
        Album givenAlbum = album;
        //when
        ProductDto.Detail result = productService.findByUid(givenAlbum.getUid());
        //then
        if(!(result instanceof ProductDto.AlbumDetail)){
            fail("Product DTO는 Album 정보를 포함해서 담고 있어야 한다.");
            return;
        }

        assertThat(result).extracting("uid", "name", "thumbnailUrl", "description", "price", "stockQuantity", "artist", "etc")
                .contains(givenAlbum.getUid(), givenAlbum.getName(), givenAlbum.getThumbnailImageUrl(), givenAlbum.getDescription(), givenAlbum.getPrice(), givenAlbum.getStockQuantity(), givenAlbum.getArtist(), givenAlbum.getEtc());

    }

    @Test
    @DisplayName("이미 저장된 책 상품의 상세 정보를 상품의 고유 식별자로 조회할 수 있다.")
    public void testFindBookByUid() throws Exception{
        //given
        Book givenBook = book;
        //when
        ProductDto.Detail result = productService.findByUid(givenBook.getUid());
        //then
        if(!(result instanceof ProductDto.BookDetail)){
            fail("Product DTO는 Book 정보를 포함해서 담고 있어야 한다.");
            return;
        }

        assertThat(result).extracting("uid", "name", "thumbnailUrl", "description", "price", "stockQuantity", "author", "isbn")
                .contains(givenBook.getUid(), givenBook.getName(), givenBook.getThumbnailImageUrl(), givenBook.getDescription(), givenBook.getPrice(), givenBook.getStockQuantity(), givenBook.getAuthor(), givenBook.getIsbn());

    }



}