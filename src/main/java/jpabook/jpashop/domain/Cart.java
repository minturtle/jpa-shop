package jpabook.jpashop.domain;


import jakarta.persistence.*;
import jpabook.jpashop.domain.product.Product;
import jpabook.jpashop.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "carts")
@NoArgsConstructor
@SuperBuilder
@Getter
public class Cart extends BaseEntity{


    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cart_uid", unique = true, nullable = false, updatable = false)
    private String uid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    private int quantity;

    public void setUser(User user) {
        this.user = user;
    }
}
