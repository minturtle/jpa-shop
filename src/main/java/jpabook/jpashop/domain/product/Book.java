package jpabook.jpashop.domain.product;


import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@NoArgsConstructor
@Getter
@SuperBuilder
@DiscriminatorValue("BOOK")
public class Book extends Product{


    private String author;
    private String isbn;
}
