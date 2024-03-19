package jpabook.jpashop.domain.user;


import jakarta.persistence.*;
import jpabook.jpashop.domain.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "accounts")
@Getter
@NoArgsConstructor
public class Account extends BaseEntity {


    public Account(String uid) {
        this.uid = uid;
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;


    @Column(name = "account_uid", unique = true, nullable = false, updatable = false)
    private String uid;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Long balance = 0L;


    public void setUser(User user) {
        this.user = user;
    }
}
