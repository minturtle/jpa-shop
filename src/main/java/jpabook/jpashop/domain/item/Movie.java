package jpabook.jpashop.domain.item;

import lombok.Getter;

import javax.persistence.Entity;

@Entity
@Getter
public class Movie extends Item {

    protected Movie(){}

    public Movie(int stockQuantity, int price, String name, String director, String actor) {
        super(stockQuantity, price, name);
        this.director = director;
        this.actor = actor;
    }

    private String director;
    private String actor;
}
