package jpabook.jpashop.domain.user;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
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
    private String password;
    private String salt;

}
