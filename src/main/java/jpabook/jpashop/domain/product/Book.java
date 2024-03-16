package jpabook.jpashop.domain.product;


import jakarta.persistence.Entity;

@Entity
public class Book extends Product{
    private String author;
    private String isbn;
}
