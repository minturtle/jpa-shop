package jpabook.jpashop.domain.item;

import lombok.Getter;

import javax.persistence.Entity;

@Entity
@Getter
public class Album extends Item{

    protected Album(){}


    public Album(int stockQuantity, int price, String name, String artist, String etc) {
        super(stockQuantity, price, name);
        this.artist = artist;
        this.etc = etc;
    }

    private String artist;
    private String etc;
}
