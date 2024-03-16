package jpabook.jpashop.domain.user;


import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class GoogleOAuth2AuthInfo {

    private String googleUid;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GoogleOAuth2AuthInfo that = (GoogleOAuth2AuthInfo) o;
        return Objects.equals(googleUid, that.googleUid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(googleUid);
    }
}
