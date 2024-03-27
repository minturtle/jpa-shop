package jpabook.jpashop.testUtils;


import jpabook.jpashop.domain.order.Order;
import jpabook.jpashop.domain.order.OrderProduct;
import jpabook.jpashop.domain.order.Payment;
import jpabook.jpashop.domain.product.Album;
import jpabook.jpashop.domain.product.Book;
import jpabook.jpashop.domain.product.Movie;
import jpabook.jpashop.domain.product.Product;
import jpabook.jpashop.domain.user.Account;
import jpabook.jpashop.domain.user.AddressInfo;
import jpabook.jpashop.domain.user.User;
import jpabook.jpashop.exception.product.InvalidStockQuantityException;
import jpabook.jpashop.exception.user.account.InvalidBalanceValueException;
import jpabook.jpashop.repository.AccountRepository;
import jpabook.jpashop.repository.OrderRepository;
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

    public static final String ORDER_UID = "order-001";
    public static final int MOVIE_PRICE = 15000;

    public static final int ALBUM_PRICE = 20000;

    public static final int BOOK_PRICE = 10000;

    public static final Integer MOVIE_INIT_STOCK = 100;

    public static final Integer ALBUM_INIT_STOCK = 50;

    public static final Integer BOOK_INIT_STOCK = 100;

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final AccountRepository accountRepository;

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
                .price(MOVIE_PRICE)
                .stockQuantity(MOVIE_INIT_STOCK)
                .description("movie description")
                .thumbnailImageUrl("http://example.com/inception.jpg")
                .director("Christopher Nolan")
                .actor("Leonardo DiCaprio")
                .build();



        Product album  = Album.builder()
                .uid(ALBUM_UID)
                .name("The Dark Side of the Moon")
                .price(ALBUM_PRICE)
                .stockQuantity(ALBUM_INIT_STOCK)
                .description("album description")
                .thumbnailImageUrl("http://example.com/darkside.jpg")
                .artist("Pink Floyd")
                .etc("1973, Progressive rock")
                .build();


        Product book  = Book.builder()
                .uid(BOOK_UID)
                .name("The Great Gatsby")
                .price(BOOK_PRICE)
                .stockQuantity(BOOK_INIT_STOCK)
                .description("book description")
                .thumbnailImageUrl("http://example.com/gatsby.jpg")
                .author("F. Scott Fitzgerald")
                .isbn("978-3-16-148410-0")
                .build();

        productRepository.saveAll(List.of(movie, album, book));
    }


    public void saveOrder() throws InvalidBalanceValueException, InvalidStockQuantityException {
        User user = userRepository.findByUidJoinAccount(USER_UID)
                .orElseThrow(RuntimeException::new);

        Product movie = productRepository.findByUid(MOVIE_UID)
                .orElseThrow(RuntimeException::new);

        Product album = productRepository.findByUid(ALBUM_UID)
                .orElseThrow(RuntimeException::new);

        Product book = productRepository.findByUid(BOOK_UID)
                .orElseThrow(RuntimeException::new);

        Account account = user.getAccountList().get(0);

        Order order = Order.builder()
                .uid(ORDER_UID)
                .payment(new Payment(account, MOVIE_PRICE + ALBUM_PRICE + BOOK_PRICE))
                .deliveryInfo(new AddressInfo("address", "detailedAddress"))
                .user(user)
                .build();

        order.addOrderProduct(new OrderProduct(movie, 1));
        order.addOrderProduct(new OrderProduct(album, 1));
        order.addOrderProduct(new OrderProduct(book, 1));

        movie.removeStock(1);
        album.removeStock(1);
        book.removeStock(1);


        account.withdraw(MOVIE_PRICE + ALBUM_PRICE + BOOK_PRICE);
        orderRepository.save(order);
    }


    public void deleteAll(){
        accountRepository.deleteAll();
        userRepository.deleteAll();
        orderRepository.deleteAll();
        productRepository.deleteAll();
    }

}
