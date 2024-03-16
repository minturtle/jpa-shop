package jpabook.jpashop.domain.user;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
@AllArgsConstructor
public class KakaoOAuth2AuthInfo{

    private String kakaoUid;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KakaoOAuth2AuthInfo that = (KakaoOAuth2AuthInfo) o;
        return Objects.equals(kakaoUid, that.kakaoUid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kakaoUid);
    }
}
