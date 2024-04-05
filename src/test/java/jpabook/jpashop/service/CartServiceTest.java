package jpabook.jpashop.service;

import jpabook.jpashop.domain.product.Cart;
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
import org.springframework.transaction.annotation.Transactional;

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

        userRepository.deleteAll();
        productRepository.deleteAll();
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

        CartDto.Add cartDto = new CartDto.Add("movie-001", 1);

        // when
        cartService.addCarts(TEST_USER_UID, cartDto);

        // then
        User user = getUserWithFetchJoinProduct();

        assertThat(user.getCartList())
                .extracting("product", "quantity")
                .contains(
                        tuple(testMovie, 1)
                );

    }


    @Test
    @DisplayName("사용자가 이전에 저장한 카트 정보를 사용자의 고유식별자를 통해 조회할 수 있다.")
    @Transactional
    void testGetCartListByUserUid() throws Exception{
        // given
        saveTestCarts();

        // when
        List<CartDto.Detail> result = cartService.findCartByUserUid(TEST_USER_UID);

        // then
        assertThat(result).extracting( "productUid", "productName", "productImageUrl", "price", "quantity")
                .contains(
                        tuple( "movie-001", "Inception", "http://example.com/inception.jpg", 15000, 1),
                        tuple( "album-001", "The Dark Side of the Moon", "http://example.com/darkside.jpg", 20000, 2),
                        tuple( "book-001", "The Great Gatsby", "http://example.com/gatsby.jpg", 10000, 3)
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

    public void saveTestCarts() {
        User user = userRepository.findByUid(TEST_USER_UID)
                .orElseThrow(RuntimeException::new);

        Cart cart1 = Cart.builder()
                .user(user)
                .product(testMovie)
                .quantity(1)
                .build();

        Cart cart2 = Cart.builder()
                .user(user)
                .product(testAlbum)
                .quantity(2)
                .build();

        Cart cart3 = Cart.builder()
                .user(user)
                .product(testBook)
                .quantity(3)
                .build();

        user.addCart(cart1);
        user.addCart(cart2);
        user.addCart(cart3);

    }


}