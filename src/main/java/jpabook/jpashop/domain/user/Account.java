package jpabook.jpashop.domain.user;


import jakarta.persistence.*;
import jpabook.jpashop.domain.BaseEntity;

@Entity
@Table(name = "accounts")
public class Account extends BaseEntity {


    @Id @GeneratedValue
    @Column(name = "account_id")
    private Long id;

    private Long balance;

}
