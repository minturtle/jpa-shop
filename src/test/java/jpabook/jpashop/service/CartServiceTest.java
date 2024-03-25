package jpabook.jpashop.service;

import jpabook.jpashop.domain.product.Album;
import jpabook.jpashop.domain.product.Book;
import jpabook.jpashop.domain.product.Movie;
import jpabook.jpashop.domain.product.Product;
import jpabook.jpashop.domain.user.User;
import jpabook.jpashop.dto.CartDto;
import jpabook.jpashop.repository.UserRepository;
import jpabook.jpashop.repository.product.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class CartServiceTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartService cartService;


    private final String TEST_USER_UID = "userUid";
    private Product testMovie;
    private Product testAlbum;
    private Product testBook;


    @BeforeEach
    void setUp() {
        saveKakaoUser();
        saveTestProducts();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    @DisplayName("특정 유저가 상품의 식별자와 갯수를 입력해 해당 상품을 장바구니에 추가해 저장할 수 있다.")
    void testAddCarts() throws Exception{
        // given

        List<CartDto.Add> dtoList = List.of(
                new CartDto.Add("movie-001", 1),
                new CartDto.Add("album-001", 2),
                new CartDto.Add("book-001", 3)
        );

        // when
        cartService.addCarts(TEST_USER_UID, dtoList);

        // then
        User user = getUserWithFetchJoinProduct();

        assertThat(user.getCartList())
                .extracting("product", "quantity")
                .contains(
                        tuple(testMovie, 1),
                        tuple(testAlbum, 2),
                        tuple(testBook, 3)
                );



    }

    private User getUserWithFetchJoinProduct() {

        return userRepository.findByUidJoinCartProduct(TEST_USER_UID)
                .orElseThrow(RuntimeException::new);
    }


    private void saveKakaoUser(){
        String email ="email@email.com";
        String givenName = "givenName";
        String address = "address";
        String detailedAddress = "detailedAddress";
        String imageUrl = "http://image.com/image.png";
        String kakaoUid = "kakaoUid";

        User newUser = new User(
                TEST_USER_UID,
                email,
                givenName,
                imageUrl,
                address,
                detailedAddress
        );

        newUser.setKakaoOAuth2AuthInfo(kakaoUid);
        userRepository.save(newUser);
    }




    private void saveTestProducts(){
        testMovie = Movie.builder()
                .uid("movie-001")
                .name("Inception")
                .price(15000)
                .stockQuantity(100)
                .description("movie description")
                .thumbnailImageUrl("http://example.com/inception.jpg")
                .director("Christopher Nolan")
                .actor("Leonardo DiCaprio")
                .build();



        testAlbum = Album.builder()
                .uid("album-001")
                .name("The Dark Side of the Moon")
                .price(20000)
                .stockQuantity(50)
                .description("album description")
                .thumbnailImageUrl("http://example.com/darkside.jpg")
                .artist("Pink Floyd")
                .etc("1973, Progressive rock")
                .build();


        testBook = Book.builder()
                .uid("book-001")
                .name("The Great Gatsby")
                .price(10000)
                .stockQuantity(100)
                .description("book description")
                .thumbnailImageUrl("http://example.com/gatsby.jpg")
                .author("F. Scott Fitzgerald")
                .isbn("978-3-16-148410-0")
                .build();

        productRepository.saveAll(List.of(testMovie, testAlbum, testBook));
    }

}