package jpabook.jpashop.domain.user;


import jakarta.persistence.*;
import jpabook.jpashop.domain.BaseEntity;
import jpabook.jpashop.exception.user.account.AccountExceptionMessages;
import jpabook.jpashop.exception.user.account.InvalidBalanceValueException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

@Entity
@Table(name = "accounts")
@Getter
@NoArgsConstructor
@SuperBuilder
public class Account extends BaseEntity {

    public Account(String uid, String name,  Long balance) {
        this.uid = uid;
        this.name = name;
        this.balance = balance;
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;


    @Column(name = "account_uid", unique = true, nullable = false, updatable = false)
    private String uid;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Long balance = 0L;

    @Version
    private int version;

    public void setUser(User user) {
        this.user = user;
    }


    public void withdraw(int amount) throws InvalidBalanceValueException {
        if(balance <= amount){
            throw new InvalidBalanceValueException(AccountExceptionMessages.NEGATIVE_ACCOUNT_BALANCE.getMessage());
        }
        balance -= amount;
    }

    public void deposit(int amount) throws InvalidBalanceValueException {
        if(Integer.MAX_VALUE - balance < amount){
            throw new InvalidBalanceValueException(AccountExceptionMessages.BALANCE_OVERFLOW.getMessage());
        }
        balance += amount;
    }




    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(id, account.id) && Objects.equals(uid, account.uid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, uid);
    }
}
