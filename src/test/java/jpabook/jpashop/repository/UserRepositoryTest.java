package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceUnitUtil;
import jpabook.jpashop.config.QueryDslConfig;
import jpabook.jpashop.domain.Cart;
import jpabook.jpashop.domain.product.Album;
import jpabook.jpashop.domain.product.Book;
import jpabook.jpashop.domain.product.Movie;
import jpabook.jpashop.domain.product.Product;
import jpabook.jpashop.domain.user.User;
import jpabook.jpashop.repository.product.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.*;


@DataJpaTest
@ActiveProfiles("test")
@Import(QueryDslConfig.class)
class UserRepositoryTest {


    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;

    @PersistenceContext
    private EntityManager em;

    private final String TEST_USER_UID = "userUid";
    private Product testMovie;
    private Product testAlbum;
    private Product testBook;




    @BeforeEach
    void setUp() {
        User user = saveKakaoUser();
        saveTestProducts();
        saveTestCarts(user);

        em.flush();
        em.clear();
    }


    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    @DisplayName("유저의 Cart 정보 조회시, fetchJoin을 사용해 추가적인 쿼리 없이 Product의 정보를 가져올 수 있다.")
    void testfindUserCart() throws Exception{
        // given
            // User, Cart, Product 데이터가 설정되어 있고, 영속성 컨텍스트가 비어있는 초기상태.

        boolean[] isProductsProxy = {true, true, true};

        // when
        User user = userRepository.findByUidJoinCartProduct(TEST_USER_UID)
                .orElseThrow(RuntimeException::new);
        // then
        List<Cart> cartList = user.getCartList();
        boolean isCartListProxy = isProxy(cartList);
        for(int i = 0; i < 3; i++){
            isProductsProxy[i] = isProxy(cartList.get(i).getProduct());
        }

        assertThat(isCartListProxy).isFalse();
        assertThat(isProductsProxy).contains(false, false, false);

    }
    


    private boolean isProxy(Object entity){
        PersistenceUnitUtil util = em.getEntityManagerFactory().getPersistenceUnitUtil();

        return !util.isLoaded(entity);
    }


    private User saveKakaoUser(){
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

        return newUser;
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

    private void saveTestCarts(User user) {
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