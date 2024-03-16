package jpabook.jpashop.domain.product;

import jakarta.persistence.Entity;

@Entity
public class Album extends Product{

    private String artist;
    private String etc;
}
