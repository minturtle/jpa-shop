package jpabook.jpashop.repository;


import jakarta.persistence.LockModeType;
import jpabook.jpashop.domain.user.Account;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {

    Optional<Account> findByUid(String accountUid);


    @Lock(value = LockModeType.OPTIMISTIC)
    @Query("select a from Account a where a.uid = :uid")
    Optional<Account> findByUidWithOptimisticLock(@Param("uid") String accountUid);

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Account a where a.uid = :uid")
    Optional<Account> findByUidWithPessimisticLock(@Param("uid") String accountUid);
}
