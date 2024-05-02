package jpabook.jpashop.domain.user;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Base64;
import java.util.Objects;

@NoArgsConstructor
@Getter
@Embeddable
@AllArgsConstructor
public class UsernamePasswordAuthInfo {


    private String username;
    @Column(columnDefinition = "VARCHAR(512)")
    private String password;
    @Column(columnDefinition = "VARCHAR(256)")
    private String salt;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsernamePasswordAuthInfo that = (UsernamePasswordAuthInfo) o;
        return Objects.equals(username, that.username) && Objects.equals(password, that.password) && Objects.equals(salt, that.salt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, salt);
    }


}
