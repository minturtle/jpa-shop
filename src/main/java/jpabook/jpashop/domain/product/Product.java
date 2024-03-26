package jpabook.jpashop.domain.product;

import jpabook.jpashop.domain.BaseEntity;
import jpabook.jpashop.domain.Cart;
import jpabook.jpashop.exception.product.InvalidStockQuantityException;
import jpabook.jpashop.exception.product.ProductExceptionMessages;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Table(name = "products")
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor
@SuperBuilder
public abstract class Product extends BaseEntity {



    public Product(String uid, String name, int price, int stockQuantity, String thumbnailImageUrl, String description) {
        this.uid = uid;
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.thumbnailImageUrl = thumbnailImageUrl;
        this.description = description;
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


    @Lob
    private String description;


    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductCategory> categories = new ArrayList<>();


    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE)
    private List<Cart> cartList = new ArrayList<>();


    public void addStock(int quantity){
        this.stockQuantity += quantity;
    }

    public void removeStock(int quantity) throws InvalidStockQuantityException {
        checkIsOrderQuantityBiggerThanStock(quantity); //주문양이 재고보다 많으면 예외발생
        this.stockQuantity -= quantity;
    }

    public void addCategory(Category category) {
        ProductCategory productCategory = new ProductCategory(this, category);
        this.categories.add(productCategory);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id) && Objects.equals(uid, product.uid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, uid);
    }


    private void checkIsOrderQuantityBiggerThanStock(int quantity) throws InvalidStockQuantityException {
        if(stockQuantity < quantity) throw new InvalidStockQuantityException(ProductExceptionMessages.NOT_ENOUGH_STOCK.getMessage());
    }
}
