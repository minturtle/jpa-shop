package jpabook.jpashop.domain.user;


import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
public class GoogleOAuth2User extends User{


    @Builder
    public GoogleOAuth2User(
            String uid,
            String email,
            String name,
            String profileImageUrl,
            String address,
            String detailedAddress,
            String googleUid
    ) {
        super(uid, email, name, profileImageUrl, address, detailedAddress);
        this.googleUid = googleUid;
    }

    private String googleUid;
}
