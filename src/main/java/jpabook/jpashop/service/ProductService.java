package jpabook.jpashop.service;

import jpabook.jpashop.domain.product.Product;
import jpabook.jpashop.dto.PaginationListDto;
import jpabook.jpashop.dto.ProductDto;
import jpabook.jpashop.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    /**
     * @description 물건 리스트 검색
     * @author minseok kim
     * @param searchCondition 물품 검색 조건, 자세한 검색 조건은 요구사항에 맞춰 설정되어 있다.
     * @throws
    */

    public PaginationListDto<ProductDto.Preview> search(ProductDto.SearchCondition searchCondition, Pageable pageable) {

        List<Product> searchResult = productRepository.search(searchCondition, pageable);
        Long count = productRepository.getCount(searchCondition);


        List<ProductDto.Preview> dtoList = searchResult.stream().map(p -> modelMapper.map(p, ProductDto.Preview.class)).toList();


        return PaginationListDto.<ProductDto.Preview>builder()
                .count(count)
                .data(dtoList)
                .build();
    }
}
