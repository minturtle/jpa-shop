package jpabook.jpashop.testUtils;


import jpabook.jpashop.domain.product.Album;
import jpabook.jpashop.domain.product.Book;
import jpabook.jpashop.domain.product.Category;
import jpabook.jpashop.domain.product.Movie;

import java.time.LocalDateTime;

public class TestDataUtils {
    public static final Category albumCategory;
    public static final Category bookCategory;
    public static final Category movieCategory;

    public static final Album album;
    public static final Book book;
    public static final Movie movie;



    static {
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
