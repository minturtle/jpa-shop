package jpabook.jpashop.domain.product;

import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Album extends Product{


    @Builder
    public Album(String uid, String name, int price, int stockQuantity, String thumbnailImageUrl, String artist, String etc) {
        super(uid, name, price, stockQuantity, thumbnailImageUrl);
        this.artist = artist;
        this.etc = etc;
    }

    private String artist;
    private String etc;
}
