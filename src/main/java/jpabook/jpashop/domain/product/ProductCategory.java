package jpabook.jpashop.domain.product;


import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class ProductCategory {


    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;


}
