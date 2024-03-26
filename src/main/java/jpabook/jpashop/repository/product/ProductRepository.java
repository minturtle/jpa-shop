package jpabook.jpashop.repository.product;

import jakarta.persistence.LockModeType;
import jpabook.jpashop.domain.product.Product;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ProductRepository extends CrudRepository<Product, Long>, SearchProductRepository {

    Optional<Product> findByUid(String givenUid);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.uid = :uid")
    Optional<Product> findByUidWithPessimisticLock(@Param("uid") String givenUid);
}
