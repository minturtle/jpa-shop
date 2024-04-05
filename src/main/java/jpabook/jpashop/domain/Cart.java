package jpabook.jpashop.domain;


import jakarta.persistence.*;
import jpabook.jpashop.domain.product.Product;
import jpabook.jpashop.domain.user.User;
import jpabook.jpashop.exception.product.CartQuantityException;
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

    public void addQuantity(int quantity) throws CartQuantityException {
        if(this.quantity + quantity < 0){
            throw new CartQuantityException("수량은 0보다 작을 수 없습니다.");
        }

        this.quantity += quantity;
    }
}
