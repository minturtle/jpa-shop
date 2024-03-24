package jpabook.jpashop.repository.product;

import jpabook.jpashop.domain.product.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ProductRepository extends CrudRepository<Product, Long>, SearchProductRepository {

    Optional<Product> findByUid(String givenUid);
}
