package jpabook.jpashop.domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

@Entity
@Getter @Setter
public class Movie extends Item {

    protected Movie(){}

    public Movie(int stockQuantity, int price, String name, String director, String actor) {
        super(stockQuantity, price, name);
        this.director = director;
        this.actor = actor;
    }


    @Override
    protected void updateInheritedValues(Item item) {
        if(!(item instanceof Movie)) return;
        this.director = ((Movie)item).getDirector();
        this.actor = ((Movie)item).getActor();
    }

    private String director;
    private String actor;
}
