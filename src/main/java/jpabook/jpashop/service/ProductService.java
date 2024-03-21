package jpabook.jpashop.service;

import jpabook.jpashop.dto.PaginationListDto;
import jpabook.jpashop.dto.ProductDto;
import jpabook.jpashop.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    /**
     * @description 물건 리스트 검색
     * @author minseok kim
     * @param searchCondition 물품 검색 조건, 자세한 검색 조건은 요구사항에 맞춰 설정되어 있다.
     * @throws
    */

    public PaginationListDto<ProductDto.Preview> search(ProductDto.SearchCondition searchCondition) {
        return null;
    }
}
