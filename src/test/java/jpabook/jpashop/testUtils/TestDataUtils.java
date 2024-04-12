package jpabook.jpashop.testUtils;


import jpabook.jpashop.domain.product.Album;
import jpabook.jpashop.domain.product.Book;
import jpabook.jpashop.domain.product.Category;
import jpabook.jpashop.domain.product.Movie;
import jpabook.jpashop.domain.user.*;

import java.time.LocalDateTime;

public class TestDataUtils {

    public static final User user1;

    public static final User user2;

    public static final Account account1;

    public static final Account account2;

    public static final Category albumCategory;
    public static final Category bookCategory;
    public static final Category movieCategory;

    public static final Album album;
    public static final Book book;
    public static final Movie movie;



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


    }

}
