package jpabook.jpashop.testUtils;


import jpabook.jpashop.domain.order.Order;
import jpabook.jpashop.domain.order.OrderProduct;
import jpabook.jpashop.domain.order.OrderStatus;
import jpabook.jpashop.domain.order.Payment;
import jpabook.jpashop.domain.product.*;
import jpabook.jpashop.domain.user.*;

import java.time.LocalDateTime;
import java.util.List;

public class TestDataUtils {

    public static final User user1;

    public static final User user2;

    public static final User user3;

    public static final Account account1;

    public static final Account account2;

    public static final Category albumCategory;
    public static final Category bookCategory;
    public static final Category movieCategory;

    public static final Album album;
    public static final Book book;
    public static final Movie movie;

    public static final Cart cart1;

    public static final Cart cart2;

    public static final Order order1;

    public static final Order order2;


    public static final OrderProduct orderProduct1;

    public static final OrderProduct orderProduct2;



    static {
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
                .usernamePasswordAuthInfo(new UsernamePasswordAuthInfo("honggildong", "Jf2rTvFrXb8QTZfoz3szoVM0jZIS2xrXmVdBL05IL5t77TYgFT/4b/DAqqxd2+6lK/jdxkjWF3sc0Nm5VwRgSulvUEuuR774o2C5z08FjVdgvBgUWrmI6tPdPK7YMAWOPjRXet/qL5rjgjYGo16fOpDZAEStdsK9G9dhg3iJ5Jtoh2Cngq3uo6t2Souc0jt5i2D1qolHVG+bTQmIbgWSgKFBq+5yWm0bHGaCeFJMpAaN8izjjlIl6cVYkeKKlEdI", "salt"))
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
                .user(user1)
                .version(1)
                .build();

        account2 = Account.builder()
                .id(2L)
                .uid("account-002")
                .name("내 계좌2")
                .balance(500L)
                .user(user1)
                .version(1)
                .build();

        albumCategory = new Category(1L, "category-003", "hiphop");
        bookCategory = new Category(2L, "category-002", "self-development");
        movieCategory = new Category(3L, "category-001", "romance");

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

        user1.addCart(cart1);
        user1.addCart(cart2);

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
                .orderProducts(List.of(orderProduct1))
                .build();

        order2 = Order.builder()
                .uid("order-002")
                .user(user1)
                .deliveryInfo(new AddressInfo("123 Main St", "Apt 101"))
                .payment(new Payment(account1, 2000))
                .status(OrderStatus.CANCELED)
                .createdAt(LocalDateTime.of(2021, 8, 2, 0, 0))
                .modifiedAt(LocalDateTime.of(2021, 8, 2, 0, 0))
                .orderProducts(List.of(orderProduct2))
                .build();


    }

}
