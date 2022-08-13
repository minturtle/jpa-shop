package jpabook.jpashop.domain.item;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Inheritance;

@Entity
@Getter
public class Book extends Item{

    private String author;
    private String isbn;
}
