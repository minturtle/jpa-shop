package jpabook.jpashop.domain.product;

import jpabook.jpashop.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "products")
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor
@SuperBuilder
public abstract class Product extends BaseEntity {



    public Product(String uid, String name, int price, int stockQuantity, String thumbnailImageUrl) {
        this.uid = uid;
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.thumbnailImageUrl = thumbnailImageUrl;
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(name = "product_uid", nullable = false, updatable = false, unique = true)
    private String uid;

    private String name;

    private int price;

    private int stockQuantity;

    private String thumbnailImageUrl;


    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductCategory> categories = new ArrayList<>();


    public void addStock(int quantity){
        this.stockQuantity += quantity;
    }

    public void removeStock(int quantity)throws IllegalArgumentException{
        checkIsOrderQuantityBiggerThanStock(quantity); //주문양이 재고보다 많으면 예외발생
        this.stockQuantity -= quantity;
    }

    private void checkIsOrderQuantityBiggerThanStock(int quantity) {
        if(stockQuantity < quantity) throw new IllegalArgumentException("주문한 수량이 남은 물건의 수량보다 많습니다.");
    }

    public void addCategory(Category category) {
        ProductCategory productCategory = new ProductCategory(this, category);
        this.categories.add(productCategory);
    }
}
