package jpabook.jpashop.domain.user;

import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class KakaoOauth2User extends User{

    @Builder
    public KakaoOauth2User(
            String uid,
            String email,
            String name,
            String profileImageUrl,
            String address,
            String detailedAddress,
            String kakaoUid
    ) {
        super(uid, email, name, profileImageUrl, address, detailedAddress);
        this.kakaoUid = kakaoUid;
    }

    private String kakaoUid;
}
