package jpabook.jpashop.domain.user;

import jpabook.jpashop.domain.BaseEntity;
import jpabook.jpashop.domain.Cart;
import jpabook.jpashop.domain.order.Order;
import lombok.Getter;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Getter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor
@Table(name = "users")
public abstract class User extends BaseEntity {


    public User(
            String uid,
            String email,
            String name,
            String profileImageUrl,
            String address,
            String detailedAddress
    ) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.addressInfo = new AddressInfo(address, detailedAddress);
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "user_uid", unique = true, nullable = false, updatable = false)
    private String uid;

    @Column(unique = true, nullable = false, updatable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    private String profileImageUrl;

    @Embedded
    private AddressInfo addressInfo;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true, mappedBy = "user")
    private List<Cart> cartList = new ArrayList<>();


    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "user")
    private List<Order> orderList = new ArrayList<>();


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;


    public void setAddressInfo(String address, String detailedAddress) {
        this.addressInfo = new AddressInfo(address, detailedAddress);
    }


}
