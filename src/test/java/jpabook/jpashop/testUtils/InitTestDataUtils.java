package jpabook.jpashop.testUtils;


import jpabook.jpashop.domain.product.Album;
import jpabook.jpashop.domain.product.Book;
import jpabook.jpashop.domain.product.Movie;
import jpabook.jpashop.domain.product.Product;
import jpabook.jpashop.domain.user.Account;
import jpabook.jpashop.domain.user.User;
import jpabook.jpashop.repository.UserRepository;
import jpabook.jpashop.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@RequiredArgsConstructor
public class InitTestDataUtils {

    public static final String USER_UID = "userUid";
    public static final String ACCOUNT_UID = "accountUid";

    public static final String MOVIE_UID = "movie-001";
    public static final String ALBUM_UID = "album-001";
    public static final String BOOK_UID = "book-001";



    private final ProductRepository productRepository;
    private final UserRepository userRepository;


    public void saveKakaoUser(){
        String email ="email@email.com";
        String givenName = "givenName";
        String address = "address";
        String detailedAddress = "detailedAddress";
        String imageUrl = "http://image.com/image.png";
        String kakaoUid = "kakaoUid";

        User newUser = new User(
                USER_UID,
                email,
                givenName,
                imageUrl,
                address,
                detailedAddress
        );

        newUser.setKakaoOAuth2AuthInfo(kakaoUid);
        userRepository.save(newUser);
    }

    public void saveAccount(Long balance) {
        User user = userRepository.findByUid(USER_UID).orElseThrow(RuntimeException::new);

        Account account = new Account(ACCOUNT_UID, balance);
        user.addAccount(account);
    }

    public void saveTestProducts(){
        Product movie = Movie.builder()
                .uid(MOVIE_UID)
                .name("Inception")
                .price(15000)
                .stockQuantity(100)
                .description("movie description")
                .thumbnailImageUrl("http://example.com/inception.jpg")
                .director("Christopher Nolan")
                .actor("Leonardo DiCaprio")
                .build();



        Product album  = Album.builder()
                .uid(ALBUM_UID)
                .name("The Dark Side of the Moon")
                .price(20000)
                .stockQuantity(50)
                .description("album description")
                .thumbnailImageUrl("http://example.com/darkside.jpg")
                .artist("Pink Floyd")
                .etc("1973, Progressive rock")
                .build();


        Product book  = Book.builder()
                .uid(BOOK_UID)
                .name("The Great Gatsby")
                .price(10000)
                .stockQuantity(100)
                .description("book description")
                .thumbnailImageUrl("http://example.com/gatsby.jpg")
                .author("F. Scott Fitzgerald")
                .isbn("978-3-16-148410-0")
                .build();

        productRepository.saveAll(List.of(movie, album, book));
    }


    public void deleteAll(){
        userRepository.deleteAll();
        productRepository.deleteAll();
    }
}
