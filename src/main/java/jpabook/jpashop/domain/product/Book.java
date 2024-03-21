package jpabook.jpashop.domain.product;


import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Book extends Product{

    @Builder
    public Book(String uid, String name, int price, int stockQuantity, String thumbnailImageUrl, String author, String isbn) {
        super(uid, name, price, stockQuantity, thumbnailImageUrl);
        this.author = author;
        this.isbn = isbn;
    }

    private String author;
    private String isbn;
}
