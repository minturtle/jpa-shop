package jpabook.jpashop.domain.item;

import jpabook.jpashop.dto.ItemDto;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

@Entity
@Getter @Setter
public class Movie extends Item {

    protected Movie(){}

    public Movie(String name, int price, String description, int stockQuantity, String director, String actor) {
        super(name, price, description, stockQuantity);
        this.director = director;
        this.actor = actor;
    }

    @Override
    protected void updateInheritedFields(ItemDto item) {
        if(item.getItemType() != Movie.class) return;
        this.director = item.getDirector();
        this.actor = item.getActor();
    }

    private String director;
    private String actor;
}
