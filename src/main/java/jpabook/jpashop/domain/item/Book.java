package jpabook.jpashop.domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

@Entity
@Getter @Setter
public class Book extends Item{

    protected Book(){}

    public Book(String name, int price, int stockQuantity, String author, String isbn) {
        super(stockQuantity, price, name);
        this.author = author;
        this.isbn = isbn;
    }

    private String author;
    private String isbn;

    @Override
    protected void updateInheritedValues(Item item) {
        if(!(item instanceof Book)) return;
        this.author = ((Book)item).getAuthor();
        this.isbn = ((Book)item).getIsbn();
    }


}
