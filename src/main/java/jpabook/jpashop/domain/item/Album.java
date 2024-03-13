package jpabook.jpashop.domain.item;

import jpabook.jpashop.dto.ItemDto;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Entity;

@Entity
@Getter @Setter
public class Album extends Item{

    protected Album(){}


    public Album(String name, int price, String description, int stockQuantity, String artist, String etc) {
        super(name, price, description, stockQuantity);
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
