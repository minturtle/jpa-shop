package jpabook.jpashop.domain.product;

import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@NoArgsConstructor
@Getter
@SuperBuilder
public class Album extends Product{


    private String artist;
    private String etc;
}
