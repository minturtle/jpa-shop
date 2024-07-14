package jpabook.jpashop.service.product;

import jpabook.jpashop.domain.product.Album;
import jpabook.jpashop.domain.product.Book;
import jpabook.jpashop.domain.product.Category;
import jpabook.jpashop.domain.product.Movie;
import jpabook.jpashop.dto.CategoryDto;
import jpabook.jpashop.dto.PaginationListDto;
import jpabook.jpashop.dto.ProductDto;
import jpabook.jpashop.enums.product.ProductType;
import jpabook.jpashop.enums.product.SortOption;
import jpabook.jpashop.service.ProductService;
import jpabook.jpashop.testUtils.ServiceTest;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static jpabook.jpashop.testUtils.TestDataFixture.*;
import static org.assertj.core.api.Assertions.*;


class ProductServiceTest extends ServiceTest {

    @Autowired
    private ProductService productService;

    @BeforeEach
    void setUp() {
        testDataFixture.saveProducts();
    }

    @Test
    @DisplayName("이미 저장된 상품의 특정 검색 조건 없이 리스트를 조회해, 조회된 물품의 정보와 갯수를 알 수 있다.")
    void given_Product_when_SearchWithoutFilteringWithPagenation_then_ReturnAllProductsPaginated() throws Exception{
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
        assertThat(actual.getCount()).isEqualTo(4L);
        assertThat(actual.getData()).extracting("uid", "name", "price", "thumbnailUrl")
                .containsExactly(
                        tuple(givenAlbum.getUid(), givenAlbum.getName(), givenAlbum.getPrice(), givenAlbum.getThumbnailImageUrl()),
                        tuple(givenBook.getUid(), givenBook.getName(), givenBook.getPrice(), givenBook.getThumbnailImageUrl())
                );
    }

    @Test
    @DisplayName("이미 저장된 상품을 CursorValue를 통해 다음 페이지의 결과를 Cursor 방식으로 받아올 수 있다.")
    void given_cursor_when_Search_then_ReturnNextPage() throws Exception{
        // given
        Album givenAlbum = album;
        Book givenBook = book;
        Movie givenMovie = movie;

        int searchSize = 2;
        ProductDto.SearchCondition searchCondition = new ProductDto.SearchCondition(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                SortOption.BY_NAME,
                ProductType.ALL
        );
        // when
        List<ProductDto.Preview> result = productService.search(searchCondition, Optional.of(givenAlbum.getUid()), searchSize);

        // then
        assertThat(result).extracting("uid", "name", "price", "thumbnailUrl")
                .containsExactly(
                        tuple(givenBook.getUid(), givenBook.getName(), givenBook.getPrice(), givenBook.getThumbnailImageUrl()),
                        tuple(givenMovie.getUid(), givenMovie.getName(), givenMovie.getPrice(), givenMovie.getThumbnailImageUrl()));
    }


    @Test
    @DisplayName("이미 저장된 영화 상품의 상세 정보를 상품의 고유식별자로 조회할 수 있다.")
    public void given_Movie_when_GetMovieDetail_then_returnMovieInfo() throws Exception{
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
    public void given_Album_when_GetAlbumDetail_then_returnAlbumInfo() throws Exception{
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
    public void given_Movie_when_GetBookDetail_then_returnBookInfo() throws Exception{
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


    @Test
    @DisplayName("상품 카테고리의 리스트를 조회할 수 있다.")
    public void given_Categories_when_SearchCategory_then_ReturnList() throws Exception{
        //given
        Category givenCategory1 = albumCategory;
        Category givenCategory2 = bookCategory;
        Category givenCategory3 = movieCategory;

        //when
        List<CategoryDto.Info> result = productService.getCategories();

        //then
        assertThat(result).extracting("uid", "name", "productType")
                .contains(
                        Tuple.tuple(givenCategory1.getUid(), givenCategory1.getName(), givenCategory1.getProductType()),
                        Tuple.tuple(givenCategory2.getUid(), givenCategory2.getName(), givenCategory2.getProductType()),
                        Tuple.tuple(givenCategory3.getUid(), givenCategory3.getName(), givenCategory3.getProductType())
                );
    }

}