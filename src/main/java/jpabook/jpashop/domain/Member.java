package jpabook.jpashop.domain;

import lombok.Getter;

import javax.persistence.*;

@Getter @Entity
public class Member {
    protected Member() {
    }

    public Member(String name, String city, String street, String zipcode) {
        this.name = name;
        this.address = new Address(city, street, zipcode);
    }

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;
    @Embedded
    private Address address;


}
