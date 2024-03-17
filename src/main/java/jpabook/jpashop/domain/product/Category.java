package jpabook.jpashop.domain.product;


import jakarta.persistence.*;

@Entity
@Table(name="categories")
public class Category {

    @Id @GeneratedValue
    private Long id;

    @Column(name = "category_uid")
    private String uid;


    private String name;


}
