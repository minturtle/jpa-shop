package jpabook.jpashop.domain.item;

import jpabook.jpashop.dto.ItemDto;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Entity;

@Entity
@Getter @Setter
public class Book extends Item{

    protected Book(){}

    public Book(String name, int price, String description, int stockQuantity, String author, String isbn) {
        super(name, price, description, stockQuantity);
        this.author = author;
        this.isbn = isbn;
    }

    private String author;
    private String isbn;

    @Override
    protected void updateInheritedFields(ItemDto item) {
        if(item.getItemType() != Book.class) return;
        this.author = item.getAuthor();
        this.isbn = item.getIsbn();
    }


}
