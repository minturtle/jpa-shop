package jpabook.jpashop.repository;

import jpabook.jpashop.domain.product.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {

}
