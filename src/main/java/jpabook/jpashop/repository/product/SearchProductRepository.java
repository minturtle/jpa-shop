package jpabook.jpashop.repository.product;

import jpabook.jpashop.domain.product.Product;
import jpabook.jpashop.dto.ProductDto;

import java.util.List;

public interface SearchProductRepository {

    List<Product> search(ProductDto.SearchCondition searchCondition);

}
