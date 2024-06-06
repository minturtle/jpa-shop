package jpabook.jpashop.repository.product;

import jpabook.jpashop.domain.product.Product;
import jpabook.jpashop.dto.ProductDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface SearchProductRepository {

    List<ProductDto.Preview> search(ProductDto.SearchCondition searchCondition, Pageable pageable);

    Long getCount(ProductDto.SearchCondition searchCondition);

    List<ProductDto.Preview> search(ProductDto.SearchCondition searchCondition, Optional cursor, int limit);

}
