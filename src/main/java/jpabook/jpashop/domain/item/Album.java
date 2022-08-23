package jpabook.jpashop.domain.item;

import jpabook.jpashop.dto.ItemDto;
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
    protected void updateInheritedFields(ItemDto item) {
        if(item.getItemType() != Album.class) return;
        this.artist = item.getArtist();
        this.etc = item.getEtc();
    }

    private String artist;
    private String etc;
}
