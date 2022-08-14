package jpabook.jpashop.domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

@Entity
@Getter @Setter
public class Album extends Item{

    protected Album(){}


    public Album(String name, int price, int stockQuantity, String artist, String etc) {
        super(stockQuantity, price, name);
        this.artist = artist;
        this.etc = etc;
    }

    @Override
    protected void updateInheritedValues(Item item) {
        if(!(item instanceof Album)) return;
        this.artist = ((Album)item).getArtist();
        this.etc = ((Album)item).getEtc();
    }

    private String artist;
    private String etc;
}
