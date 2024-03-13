package jpabook.jpashop.domain;

import jpabook.jpashop.util.Encryptor;
import lombok.Getter;

import jakarta.persistence.*;


@Getter @Entity
public class Member {
    protected Member() {
    }

    public static Member createMember(String name, String userId, String password,
                                      String city, String street, String zipcode) {
        Member member = new Member();

        member.name = name;
        member.userId = userId;
        member.password = password;
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


    public void setPassword(String password) {
        this.password = password;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean comparePassword(String finePassword){
        return this.password.equals(Encryptor.encrypt(finePassword));
    }

    public void encryptPassword(){
        this.password = Encryptor.encrypt(password);
    }
}
