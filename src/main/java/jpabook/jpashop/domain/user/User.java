package jpabook.jpashop.domain.user;

import jpabook.jpashop.domain.BaseEntity;
import jpabook.jpashop.domain.product.Cart;
import jpabook.jpashop.domain.order.Order;
import jpabook.jpashop.dto.UserDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;


@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
@SuperBuilder
public class User extends BaseEntity {


    public static User of(
            String uid,
            String email,
            String name,
            String profileImageUrl,
            String address,
            String detailedAddress
    ) {
        return User.builder()
                .uid(uid)
                .email(email)
                .name(name)
                .profileImageUrl(profileImageUrl)
                .addressInfo(new AddressInfo(address, detailedAddress))
                .build();

    }


    public static User of(
            String uid,
            String email,
            String name,
            String profileImageUrl,
            KakaoOAuth2AuthInfo auth2AuthInfo
    ){
        return User.builder()
                .uid(uid)
                .email(email)
                .name(name)
                .profileImageUrl(profileImageUrl)
                .kakaoOAuth2AuthInfo(auth2AuthInfo)
                .build();

    }

    public static User of(
            String uid,
            String email,
            String name,
            String profileImageUrl,
            GoogleOAuth2AuthInfo auth2AuthInfo
    ){
        return User.builder()
                .uid(uid)
                .email(email)
                .name(name)
                .profileImageUrl(profileImageUrl)
                .googleOAuth2AuthInfo(auth2AuthInfo)
                .build();

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
    @Builder.Default
    private List<Cart> cartList = new ArrayList<>();


    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "user")
    @Builder.Default
    private List<Order> orderList = new ArrayList<>();


    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
    @Builder.Default
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

    public void setUsernamePasswordAuthInfo(String username, String encodedPassword){
        this.usernamePasswordAuthInfo = new UsernamePasswordAuthInfo(username, encodedPassword);
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
