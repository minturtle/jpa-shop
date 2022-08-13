package jpabook.jpashop.domain;

import lombok.Getter;

import javax.persistence.*;

@Getter @Entity
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;
    @Embedded
    private Address address;

}
