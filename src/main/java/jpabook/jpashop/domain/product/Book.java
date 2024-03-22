package jpabook.jpashop.domain.product;


import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@NoArgsConstructor
@Getter
@SuperBuilder
public class Book extends Product{


    private String author;
    private String isbn;
}
