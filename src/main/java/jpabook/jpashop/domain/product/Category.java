package jpabook.jpashop.domain.product;


import jakarta.persistence.*;
import jpabook.jpashop.enums.product.ProductType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="categories")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    @Column(name = "category_uid", updatable = false, unique = true, nullable = false)
    private String uid;

    private String name;

    @Enumerated(EnumType.STRING)
    private ProductType productType;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductCategory> productCategoryList = new ArrayList<>();
}
