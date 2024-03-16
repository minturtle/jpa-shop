package jpabook.jpashop.domain.product;


import jakarta.persistence.Entity;
import lombok.Getter;

@Entity
@Getter
public class Movie extends Product{

    private String director;
    private String actor;

}
