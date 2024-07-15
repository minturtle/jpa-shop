package jpabook.jpashop.testUtils;


import jpabook.jpashop.domain.order.Order;
import jpabook.jpashop.domain.order.OrderProduct;
import jpabook.jpashop.domain.order.OrderStatus;
import jpabook.jpashop.domain.order.Payment;
import jpabook.jpashop.domain.product.*;
import jpabook.jpashop.domain.user.*;
import jpabook.jpashop.enums.product.ProductType;
import jpabook.jpashop.repository.AccountRepository;
import jpabook.jpashop.repository.CategoryRepository;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.UserRepository;
import jpabook.jpashop.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor
public class TestDataFixture {

    public static User user1;

    public static User user2;

    public static User user3;

    public static Account account1;

    public static Account account2;

    public static Category albumCategory;
    public static Category bookCategory;
    public static Category movieCategory;

    public static Album album;
    public static Book book;
    public static Movie movie;

    public static Movie movie2;

    public static Cart cart1;

    public static Cart cart2;

    public static Order order1;

    public static Order order2;


    public static OrderProduct orderProduct1;

    public static OrderProduct orderProduct2;


    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;


    static {
        setUpUsers();
        setUpProducts();
        setUpOrders();
    }

    private static void setUpOrders() {
        orderProduct1 = new OrderProduct(album, 2, 500);
        orderProduct2 = new OrderProduct(book, 2, 1000);

        order1 = Order.builder()
                .uid("order-001")
                .user(user1)
                .deliveryInfo(new AddressInfo("123 Main St", "Apt 101"))
                .payment(new Payment(account1, 1000))
                .status(OrderStatus.ORDERED)
                .createdAt(LocalDateTime.of(2021, 8, 1, 0, 0))
                .modifiedAt(LocalDateTime.of(2021, 8, 1, 0, 0))
                .build();

        order2 = Order.builder()
                .uid("order-002")
                .user(user1)
                .deliveryInfo(new AddressInfo("123 Main St", "Apt 101"))
                .payment(new Payment(account1, 2000))
                .status(OrderStatus.CANCELED)
                .createdAt(LocalDateTime.of(2021, 8, 2, 0, 0))
                .modifiedAt(LocalDateTime.of(2021, 8, 2, 0, 0))
                .build();

    }

    private static void setUpProducts() {

        albumCategory = new Category(1L, "category-003", "hiphop", ProductType.ALBUM, new ArrayList<>());
        bookCategory = new Category(2L, "category-002", "self-development", ProductType.BOOK, new ArrayList<>());
        movieCategory = new Category(3L, "category-001", "romance", ProductType.MOVIE, new ArrayList<>());

        album = Album.builder()
                .uid("album-001")
                .name("Album Name")
                .price(2000)
                .stockQuantity(5)
                .thumbnailImageUrl("http://example.com/album_thumbnail.jpg")
                .description("Album description")
                .createdAt(LocalDateTime.parse("2024-02-01T11:00:00"))
                .modifiedAt(LocalDateTime.parse("2024-02-02T11:00:00"))
                .artist("Artist Name")
                .etc("Etc information")
                .build();

        book = Book.builder()
                .uid("book-001")
                .name("Book Name")
                .price(1500)
                .stockQuantity(20)
                .thumbnailImageUrl("http://example.com/book_thumbnail.jpg")
                .description("Book description")
                .createdAt(LocalDateTime.parse("2024-01-01T10:00:00"))
                .modifiedAt(LocalDateTime.parse("2024-01-02T10:00:00"))
                .author("Author Name")
                .isbn("ISBN1234567890")
                .build();


        movie = Movie.builder()
                .uid("movie-001")
                .name("Movie Name")
                .price(3000)
                .stockQuantity(8)
                .thumbnailImageUrl("http://example.com/movie_thumbnail.jpg")
                .description("Movie description")
                .createdAt(LocalDateTime.parse("2024-03-01T12:00:00"))
                .modifiedAt(LocalDateTime.parse("2024-03-02T12:00:00"))
                .director("Director Name")
                .actor("Actor Name")
                .build();

        movie2 = Movie.builder()
                .uid("movie-002")
                .name("Movie Name2")
                .price(4000)
                .stockQuantity(8)
                .thumbnailImageUrl("http://example.com/movie2_thumbnail.jpg")
                .description("Movie2 description")
                .createdAt(LocalDateTime.parse("2024-03-01T12:00:00"))
                .modifiedAt(LocalDateTime.parse("2024-03-02T12:00:00"))
                .director("Director Name2")
                .actor("Actor Name2")
                .build();


        album.addCategory(albumCategory);
        book.addCategory(bookCategory);
        movie.addCategory(movieCategory);
        movie2.addCategory(movieCategory);

    }




    private static void setUpUsers() {
        user1 = User.builder()
                .id(1L)
                .uid("user-001")
                .email("user@example.com")
                .name("홍길동")
                .profileImageUrl("http://example.com/profiles/hong.png")
                .addressInfo(new AddressInfo("서울시 강남구", "역삼동 123-45"))
                .createdAt(LocalDateTime.parse("2024-03-31T12:00:00"))
                .modifiedAt(LocalDateTime.parse("2024-03-31T12:00:00"))
                .version(1)
                .usernamePasswordAuthInfo(new UsernamePasswordAuthInfo("honggildong", "Jf2rTvFrXb8QTZfoz3szoVM0jZIS2xrXmVdBL05IL5t77TYgFT/4b/DAqqxd2+6lK/jdxkjWF3sc0Nm5VwRgSulvUEuuR774o2C5z08FjVdgvBgUWrmI6tPdPK7YMAWOPjRXet/qL5rjgjYGo16fOpDZAEStdsK9G9dhg3iJ5Jtoh2Cngq3uo6t2Souc0jt5i2D1qolHVG+bTQmIbgWSgKFBq+5yWm0bHGaCeFJMpAaN8izjjlIl6cVYkeKKlEdI"))
                .build();

        // Building the second user instance
        user2 = User.builder()
                .id(2L)
                .uid("user-002")
                .email("user2@email.com")
                .name("김철수")
                .profileImageUrl("http://example.com/profiles/kim.png")
                .addressInfo(new AddressInfo("경상북도 구미시", "대학로 1"))
                .createdAt(LocalDateTime.parse("2024-03-31T12:00:00.000000"))
                .modifiedAt(LocalDateTime.parse("2024-03-31T12:00:00.000000"))
                .version(1)
                .kakaoOAuth2AuthInfo(new KakaoOAuth2AuthInfo("123214214"))
                .build();

        user3 = User.builder()
                .id(3L)
                .uid("user-003")
                .email("user3@email.com")
                .name("김영희")
                .profileImageUrl("http://example.com/profiles/young.png")
                .addressInfo(new AddressInfo("대구광역시", "달서구 123"))
                .createdAt(LocalDateTime.parse("2024-03-31T12:00:00.000000"))
                .modifiedAt(LocalDateTime.parse("2024-03-31T12:00:00.000000"))
                .version(1)
                .googleOAuth2AuthInfo(new GoogleOAuth2AuthInfo("123214214"))
                .build();

        account1 = Account.builder()
                .id(1L)
                .uid("account-001")
                .name("내 계좌")
                .balance(100000L)
                .version(1)
                .build();

        account2 = Account.builder()
                .id(2L)
                .uid("account-002")
                .name("내 계좌2")
                .balance(500L)
                .version(1)
                .build();


        user1.addAccount(account1);
        user1.addAccount(account2);


        setUpCarts();
    }

    private static void setUpCarts() {
        cart1 = Cart.builder()
                .id(1L)
                .user(user1)
                .product(album)
                .quantity(3)
                .build();

        cart2 = Cart.builder()
                .id(2L)
                .user(user1)
                .product(book)
                .quantity(2)
                .build();
    }


    public void saveUsers(){
        setUpUsers();

        user1.setCartList(List.of());

        userRepository.saveAll(List.of(user1, user2, user3));
        accountRepository.saveAll(List.of(account1, account2));
    }

    public void saveCarts(){
        setUpUsers();

        user1.setCartList(List.of(cart1, cart2));

        userRepository.saveAll(List.of(user1, user2, user3));
        accountRepository.saveAll(List.of(account1, account2));
    }

    public void saveProducts(){
        setUpProducts();

        categoryRepository.saveAll(List.of(albumCategory, bookCategory, movieCategory));
        productRepository.saveAll(List.of(album, book, movie, movie2));

    }

    public void saveOrders(){
        setUpOrders();
        order1.addOrderProduct(orderProduct1);
        order2.addOrderProduct(orderProduct2);

        orderRepository.saveAll(List.of(order1, order2));
    }

    public void deleteAll(){
        orderRepository.deleteAll();
        accountRepository.deleteAll();
        userRepository.deleteAll();
        categoryRepository.deleteAll();
        productRepository.deleteAll();
    }

}
