package jpabook.jpashop.domain;

import lombok.Getter;

import javax.persistence.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Getter @Entity
public class Member {
    protected Member() {
    }

    public static Member createMember(String name, String userId, String password, String city, String street, String zipcode) {
        Member member = new Member();

        member.name = name;
        member.userId = userId;
        member.password = SHA256Encrypt(password);
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


    private static String SHA256Encrypt(String pw){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(pw.getBytes());
            return String.format("%064x", new BigInteger(1, md.digest()));
        }catch (NoSuchAlgorithmException e){}
        throw new FailEncrypt();
    }

    public boolean comparePassword(String finePassword){
        return this.password.equals(SHA256Encrypt(finePassword));
    }
}

class FailEncrypt extends RuntimeException{}