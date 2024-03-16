package jpabook.jpashop.domain.user;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Base64;
import java.util.Objects;

@NoArgsConstructor
@Getter
@Entity
public class UsernamePasswordUser extends User{


    @Builder
    public UsernamePasswordUser(
            String uid,
            String email,
            String name,
            String profileImageUrl,
            String address,
            String detailedAddress,
            String username,
            String password,
            String salt
    ) {
        super(uid, email, name, profileImageUrl, address, detailedAddress);
        this.username = username;
        this.password = password;
        this.salt = salt;
    }

    private String username;
    @Column(columnDefinition = "VARCHAR(512)")
    private String password;
    @Column(columnDefinition = "VARCHAR(256)")
    private String salt;




    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), username, password, salt);
    }


    public byte[] getSaltBytes(){
        return Base64.getDecoder().decode(salt);
    }

}
