package jpabook.jpashop.service;

import jakarta.persistence.EntityNotFoundException;
import jpabook.jpashop.domain.product.Album;
import jpabook.jpashop.domain.product.Movie;
import jpabook.jpashop.domain.product.Product;
import jpabook.jpashop.dto.PaginationListDto;
import jpabook.jpashop.dto.ProductDto;
import jpabook.jpashop.exception.common.CannotFindEntityException;
import jpabook.jpashop.exception.product.ProductExceptionMessages;
import jpabook.jpashop.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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



    /**
     * @author minseok kim
     * @description 상품의 고유식별자로 상품의 상세 정보를 조회하는 메서드
     * @param givenUid 상품의 고유식별자
     * @return 상품의 상세정보
     * @exception
    */
    public ProductDto.Detail findByUid(String givenUid) throws CannotFindEntityException {
        Product product = productRepository.findByUid(givenUid)
                .orElseThrow(()->new CannotFindEntityException(ProductExceptionMessages.CANNOT_FIND_PRODUCT.getMessage()));

        if(product instanceof Movie){
            return modelMapper.map(product, ProductDto.MovieDetail.class);
        }
        else if(product instanceof Album){
            return modelMapper.map(product, ProductDto.AlbumDetail.class);
        }

        return null;
    }
    
    
}
