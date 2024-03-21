package jpabook.jpashop.domain.product;


import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Movie extends Product{

    @Builder
    public Movie(String uid, String name, int price, int stockQuantity, String thumbnailImageUrl, String director, String actor) {
        super(uid, name, price, stockQuantity, thumbnailImageUrl);
        this.director = director;
        this.actor = actor;
    }

    private String director;
    private String actor;

}
