package jpabook.jpashop.domain;

import jpabook.jpashop.util.Encryptor;
import lombok.Getter;

import javax.persistence.*;


@Getter @Entity
public class Member {
    protected Member() {
    }

    public static Member createMember(String name, String userId, String password,
                                      String city, String street, String zipcode, boolean isEncrypted) {
        Member member = new Member();

        member.name = name;
        member.userId = userId;
        if(!isEncrypted) member.password = Encryptor.encrypt(password);
        else member.password = password;
        member.address = new Address(city, street, zipcode);

        return member;
    }

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String userId;

    private String password;

    private String name;
    @Embedded
    private Address address;


    public void setPassword(String password, boolean isEncrypted) {
        if(isEncrypted) this.password = password;
        else this.password = Encryptor.encrypt(password);
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public boolean comparePassword(String finePassword){
        return this.password.equals(Encryptor.encrypt(finePassword));
    }
}
