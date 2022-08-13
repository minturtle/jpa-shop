package jpabook.jpashop.domain.item;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Inheritance;

@Entity
@Getter
public class Book extends Item{

    protected Book(){}

    public Book(int stockQuantity, int price, String name, String author, String isbn) {
        super(stockQuantity, price, name);
        this.author = author;
        this.isbn = isbn;
    }

    private String author;
    private String isbn;
}
