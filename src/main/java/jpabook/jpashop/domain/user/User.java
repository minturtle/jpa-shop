package jpabook.jpashop.domain.user;

import jpabook.jpashop.domain.BaseEntity;
import jpabook.jpashop.domain.product.Cart;
import jpabook.jpashop.domain.order.Order;
import lombok.Getter;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;


@Getter
@Entity
@NoArgsConstructor
@Table(name = "users")
public class User extends BaseEntity {


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

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
    private List<Cart> cartList = new ArrayList<>();


    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "user")
    private List<Order> orderList = new ArrayList<>();


    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
    private List<Account> accountList = new ArrayList<>();

    @Embedded
    private UsernamePasswordAuthInfo usernamePasswordAuthInfo;

    @Embedded
    private KakaoOAuth2AuthInfo kakaoOAuth2AuthInfo;

    @Embedded
    private GoogleOAuth2AuthInfo googleOAuth2AuthInfo;

    @Version
    private Integer version;


    public void setAddressInfo(String address, String detailedAddress) {
        this.addressInfo = new AddressInfo(address, detailedAddress);
    }

    public void setUsernamePasswordAuthInfo(String username, String encodedPassword, byte[] salt){
        this.usernamePasswordAuthInfo = new UsernamePasswordAuthInfo(username, encodedPassword, new String(Base64.getEncoder().encode(salt)));
    }

    public void setKakaoOAuth2AuthInfo(String kakaoUid) {
        this.kakaoOAuth2AuthInfo = new KakaoOAuth2AuthInfo(kakaoUid);
    }


    public void setGoogleOAuth2AuthInfo(String googleUid) {
        this.googleOAuth2AuthInfo = new GoogleOAuth2AuthInfo(googleUid);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }



    public void addAccount(Account account) {
        this.accountList.add(account);
        account.setUser(this);
    }


    public void addCart(Cart cart) {
        this.cartList.add(cart);
        cart.setUser(this);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void removeCart(Cart cart) {
        this.cartList.remove(cart);
        cart.setUser(null);
    }
}
